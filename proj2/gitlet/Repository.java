package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

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

    public static Commit head;

    public static final String author = "muqi";

    /**
     * used for stage mapping from the name to SHA1
     */
    private static Map<String, String> stageMap = new TreeMap<>();

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

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

        head = new Commit(author, "initial commit", null);
        writeObject(join(GITLET_DIR, "head"), head);

    }

    /**
     * TODO: if staging a already-staged file, overwrite the previous version
     * TODO: if the file to be staged is identical to current commit Version, don't stage and remove it if it exists in stage area
     * @param fileName the fileName in current working directory
     */
    public static void add(String fileName) {
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
            writeContents(join(STAGE_DIR, currentVersion), file);
        } else if (!previousVersion.equals(currentVersion)) {
            // if previous version is not identical to current version, overwrite it
            stageMap.put(fileName, currentVersion);
            writeContents(join(STAGE_DIR, currentVersion), file);
        }

        if (join(STAGE_DIR, previousVersion).exists()) {
            join(STAGE_DIR, previousVersion).delete();
        }
        // if these two versions are identical, do nothing
    }

    /**
     *
     *  TODO: only update the contents of files in the stage area
     *  TODO:
     * @param message the commit message
     */
    public static void commit(String message) {
        // 1. create a commit object whose parent is head
        Commit commit = new Commit(author, message, head);

        Map<String, String> newMap = new TreeMap<>(head.getMap());

        // 2. update from stage area
        for (String fileName: stageMap.keySet()) {
            if (newMap.containsKey(fileName))
                newMap.put(fileName, stageMap.get(fileName));
        }

        // write this commit object
//        writeObject(join(COMMIT_DIR, ""));

        // set head to the new commit
        head = commit;

    }
}
