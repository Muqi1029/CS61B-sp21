package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");

    public static final File BLOB_DIR = join(GITLET_DIR, "blob");

    public static final File STAGE_DIR = join(GITLET_DIR, "stage");

    public static final File STAGE_MAP = join(STAGE_DIR, "map");

    public static final File HEAD = join(GITLET_DIR, "head");

    /**
     * when reverting to an old commit, the front of the linked list will no longer reflect the current
     * state of files, which might be a little misleading.
     * In order to fix this problem, the head pointer is born to keep track of where in the linked list
     * we currently are
     */
    public static Commit head;

    /**
     * the author of all commits
     */
    public static final String author = "muqi";

    /**
     * used for stage mapping from the name to SHA1
     */
    private static Map<String, String> stageMap = new TreeMap<>();

    /**
     * used to store the whole commit operations
     */
    private static final List<String> commitList = new ArrayList<>();

    /**
     * 1. automatically start with one commit (commit message: 'initial commit')
     *
     */
    public static void init() {
        /** if there is already a Gitlet version-control system in the current directory, it should be aborted */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        /** make directory .gitlet */
        if (!GITLET_DIR.mkdir()) {
            System.out.println("Failed to mkdir .gitlet");
        }
        if (!COMMIT_DIR.mkdir()) {
            System.out.println("Failed to mkdir .gitlet/commit");
        }
        if (!STAGE_DIR.mkdir()) {
            System.out.println("Failed to mkdir .gitlet/stage");
        }
        if (!BLOB_DIR.mkdir()) {
            System.out.println("Failed to mkdir .gitlet/blob");
        }

        /** give the initial commitment */
        head = new Commit(author, "initial commit", null);
        writeObject(join(GITLET_DIR, "head"), head);
        writeObject(STAGE_MAP, (Serializable) stageMap);
    }

    /**
     * TODO: add a copy of the file as it currently exists to the staging area
     * TODO: if staging a already-staged file, overwrite the previous version
     * TODO: if the file to be staged is identical to current commit Version, don't stage and remove it if it exists in stage area
     * @param fileName the fileName in current working directory
     */
    public static void add(String fileName) {

        readInitial();

        // 1. find the file in CWD
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        // 2. check in stage area
        String previousVersion = head.getMap().getOrDefault(fileName, null);
        String currentVersion = sha1(readContents(file));

        if (previousVersion == null) {
            // if this file is not staged yet
            stageMap.put(fileName, currentVersion);
            // file name is sha-1 value, copy the file into stage area
            writeContents(join(STAGE_DIR, currentVersion), readContents(file));
        } else if (!previousVersion.equals(currentVersion)) {
            // if previous version is not identical to current version, overwrite it
            stageMap.put(fileName, currentVersion);
            writeContents(join(STAGE_DIR, currentVersion), readContents(file));

            // remove the previous version of this file in the stage area
            if (join(STAGE_DIR, previousVersion).exists()) {
                join(STAGE_DIR, previousVersion).delete();
            }
        }
        writeInitial();

        // if these two versions are identical, do nothing
    }

    /**
     *
     *  TODO: only update the contents of files in the stage area
     *  TODO: saves a snapshot of tracked files in the current commit and staging area
     *  TODO: the staging area is cleared after a commit
     *
     * @param message the commit message
     */
    public static void commit(String message) {

        readInitial();

        /** if no files have been staged, abort */
        if (stageMap.size() == 0) {
            System.out.println("No changes added to the commit");
            System.exit(0);
        }

        /** every commit must have a non-blank message */
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // 1. create a commit object whose parent is head
        Commit commit = new Commit(author, message, head);

        Map<String, String> newMap = new TreeMap<>(head.getMap());

        // 2. update from stage area and write the files into blob directory
        for (String fileName: stageMap.keySet()) {
            String sha1 = stageMap.get(fileName);
            newMap.put(fileName, sha1);
            writeContents(join(BLOB_DIR, sha1), readContents(join(CWD, fileName)));
        }
        commit.setMap(newMap);

        // 3. clear the staging area
        stageMap.clear();
        for (File file: Objects.requireNonNull(STAGE_DIR.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        // 4. write this commit object into commit directory
        writeObject(join(COMMIT_DIR, commit.getId()), commit);

        // 5. set head to the new commit
        head = commit;

        writeInitial();
    }

    /**
     *
     * @param fileName the file name you want to remove
     */
    public static void rm(String fileName) {
        readInitial();

        Map<String, String> map = head.getMap();
        String sha1 = stageMap.getOrDefault(fileName, null);

        if (!map.containsKey(fileName) && !stageMap.containsKey(fileName)) {
            /** if the file is neither staged nor tracked by the head commit, print the error message */
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        // unstage the file if it is currently staged for addition
        if (sha1 != null) {
            stageMap.remove(fileName);
            join(STAGE_DIR, sha1).delete();
        }

        // if the file is tracked in the current commit, stage it for removal and remove the file from the working directory
        // TODO: stage it for removal
        if (map.containsKey(fileName)) {
            map.remove(fileName);
            join(CWD, fileName).delete();
        }

        writeInitial();
    }

    /**
     * Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the commit
     */
    public static void log() {
        readInitial();
        Commit commit = head;
        while (commit != null) {
            System.out.println("===");
//            System.out.println("commit " + commit.getId());
            //TODO: merge information
            System.out.println("Date: " + commit.getTimestamp());
            System.out.println(commit.getMessage());
            System.out.println();
            commit = commit.getParent();
        }
    }


    /** ============================= private Utils ===========================*/
    private static void readInitial() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(1);
        }
        head = readObject(HEAD, Commit.class);
        stageMap = readObject(STAGE_MAP, TreeMap.class);
    }

    private static void writeInitial() {
        writeObject(HEAD, head);
        writeObject(STAGE_MAP, (Serializable) stageMap);
    }
}
