package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author Muqi
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The commit directory
     */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commit");

    /**
     * the blob directory
     */
    public static final File BLOB_DIR = join(GITLET_DIR, "blob");

    /**
     * the stage area directory
     */
    public static final File STAGE_DIR = join(GITLET_DIR, "stage");

    /**
     * file used for recording a map from filename to SHA1
     */
    public static final File STAGE_MAP = join(STAGE_DIR, "map");

    /**
     * file used for recording removal
     */
    public static final File STAGE_REMOVAL = join(STAGE_DIR, "removal");

    private static List<String> removalFileList = new ArrayList<>();

    /**
     * file used for recording current commit
     */
    public static final File HEAD = join(GITLET_DIR, "head");


    /**
     * when reverting to an old commit, the front of the linked list will no longer reflect the current
     * state of files, which might be a little misleading.
     * In order to fix this problem, the head pointer is born to keep track of where in the linked list
     * we currently are
     */
    private static Commit head = null;

    /**
     * the author of all commits
     */
    private static final String author = "muqi";

    /**
     * used for stage mapping from the name to SHA1
     */
    private static Map<String, String> stageMap = new TreeMap<>();

    /**
     * used to store the whole commit operations
     */
    private static List<Branch> branchList = new ArrayList<>();

    private static final File BRANCH = join(GITLET_DIR, "branch");


    /**
     * store the untracked files
     */
    private static final List<String> untrackedFiles = new ArrayList<>();


    /** ============================================================ */

    /**
     * init()
     * 1. automatically start with one commit (commit message: 'initial commit')
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            /** if there is already a Gitlet version-control system in the current directory, it should be aborted */
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
        head = new Commit("initial commit", null, new Date(0), author);
        branchList.add(new Branch("master", head));
        writeObject(join(GITLET_DIR, "head"), head);
        writeObject(STAGE_MAP, (Serializable) stageMap);
    }

    /**
     * TODO: add a copy of the file as it currently exists to the staging area
     * TODO: if staging a already-staged file, overwrite the previous version
     * TODO: if the file to be staged is identical to current commit Version, don't stage and remove it if it exists in stage area
     *
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
     * TODO: only update the contents of files in the stage area
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
        Commit commit = new Commit(message, head, author);

        Map<String, String> newMap = new TreeMap<>(head.getMap());

        // 2. update from stage area and write the files into blob directory
        for (String fileName : stageMap.keySet()) {
            String sha1 = stageMap.get(fileName);
            newMap.put(fileName, sha1);
            writeContents(join(BLOB_DIR, sha1), readContents(join(CWD, fileName)));
        }
        commit.setMap(newMap);

        // 3. clear the staging area
        stageMap.clear();
        for (File file : Objects.requireNonNull(STAGE_DIR.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        // 4. write this commit object into commit directory
        writeObject(join(COMMIT_DIR, commit.getId()), commit);

        // 5. set head to the new commit
        head = commit;
        Branch branch = branchList.get(0);
        branch.setCommit(head);

        writeInitial();
    }

    /**
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
        // stage it for removal
        if (map.containsKey(fileName)) {
            map.remove(fileName);
            join(CWD, fileName).delete();
            removalFileList.add(fileName);
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
        Formatter formatter = new Formatter();
        while (commit != null) {
            System.out.println("===");
            System.out.println("commit " + commit.getId());
            //TODO: merge information

            formatter.format("%1$ta %1$tb %1$td %1$tT %1$tY %1$tZ", commit.getTimestamp());
            String formattedDate = formatter.toString();
            System.out.println("Date " + formattedDate);

            System.out.println(commit.getMessage());
            System.out.println();
            commit = commit.getParent();
        }
    }


    private static void global_log() {
        //TODO: global-log
    }

    /**
     * prints out the ids of all commits that have the given commit message
     *
     * @param message the message of commit
     */
    public static void find(String message) {
        readInitial();

        boolean flag = false;
        Commit commit = head;
        while (commit != null) {
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getId());
                flag = true;
            }
            commit = commit.getParent();
        }
        if (!flag) {
            /** if no such commit exists, prints the error message */
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        readInitial();

        System.out.println("=== Branches ===");
        for (int i = 0; i < branchList.size(); i++) {
            if (i == 0) {
                System.out.print("*");
            }
            System.out.println(branchList.get(i).getName());
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        for (String filename : stageMap.keySet()) {
            System.out.println(filename);
        }
        System.out.println();

        System.out.println("Removed Files");
        for (String name : removalFileList) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        //TODO: Tracked in the current commit, changed in the working directory, but not staged;
        // or Staged for addition, but with different contents than in the working directory;
        // or Staged for addition, but deleted in the working directory;
        // or Not staged for removal, but tracked in the current commit and deleted from the working directory.
        System.out.println();

        System.out.println("=== Untracked Files ===");
        /** the untracked files refer to those that not only don't exist
         * in previous snapshots, but also in the stage area.  */
        showFiles(CWD.listFiles());
        Collections.sort(untrackedFiles);
        for (String name : untrackedFiles) {
            System.out.println(name);
        }
    }

    private static void showFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                showFiles(file.listFiles());
            } else {
                String name = sha1(readContents(file));
                File inBlob = join(BLOB_DIR, name);
                File inStage = join(STAGE_DIR, name);
                if (!inBlob.exists() || !inStage.exists()) {
//                    System.out.println(file.getName());
                    untrackedFiles.add(file.getName());
                }
            }
        }
    }


    public static void checkout(String[] args) {
        readInitial();

        if (args[1].equals("--") && args.length == 3) {
            /** java gitlet.Main checkout -- [file name] */
            // Takes the version of the file as it exists in the head commit and puts it in the working directory,
            // overwriting the version of the file that’s already there if there is one.
            // The new version of the file is not staged.

            Map<String, String> map = head.getMap();
            checkIsExist(map, args[2]);
            stageMap.remove(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            /** java gitlet.Main checkout [commit id] -- [file name] */
            Commit commit = readObject(join(COMMIT_DIR, args[1]), Commit.class);
            if (commit == null) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            checkIsExist(commit.getMap(), args[3]);
            stageMap.remove(args[3]);

        } else if (args.length == 2) {
            /** java gitlet.Main checkout [branch name] */

            for (int i = 0; i < branchList.size(); i++) {
                Branch branch = branchList.get(i);
                if (i == 0 && branch.getName().equals(args[1])) {
                    // if that branch is the current branch
                    System.out.println("No need to checkout the current branch.");
                    System.exit(0);
                } else if (branch.getName().equals(args[1])) {
                    //TODO checkout
                    branchList.remove(i);
                    branchList.add(0, branch); // add the head of the branch
                    Commit commit = branch.getCommit();
                    Map<String, String> map = commit.getMap();
                    //TODO delete
                    for (String filename: map.keySet()) {
                        File file = join(BLOB_DIR, map.get(filename));
                        writeContents(join(CWD, filename), file);
                    }
                    stageMap.clear();
                }
            }
            // doesn't have the branch name
            System.out.println("No such branch exists.");
        }
    }

    private static void checkIsExist(Map<String, String> map, String filename) {
        if (!map.containsKey(filename)) {
            // if the file does not exist in the previous commit, aborting
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            String sha1 = map.get(filename);
            File file = join(BLOB_DIR, sha1); // obtain the file
            writeContents(join(CWD, filename), file);
        }
    }


    /** ======================= branch ============================== */

    public static void branch(String branchName) {
        readInitial();
        // check whether there has been the branch
        for (Branch branch : branchList) {
            if (branch.getName().equals(branchName)) {
                System.out.println("A branch with that name already exists.\n");
                System.exit(0);
            }
        }
        Branch branch = new Branch(branchName, head);
        branchList.add(branch);
        writeInitial();
    }

    /** =================== rm-branch =========================== */
    public static void rm_branch(String branchName) {
        readInitial();

        boolean flag = true;
        for (int i = 0; i < branchList.size(); i++) {
            if (branchList.get(i).getName().equals(branchName)) {
                if (i == 0) {
                    System.out.println("Cannot remove the current branch.");
                    System.exit(0);
                }
                branchList.remove(i);
                flag = false;
            }
        }
        if (flag) {
            System.out.println("A branch with that name does not exist.");
        }
        writeInitial();
    }


    /** ========================= reset =============================== */
    public static void reset(String commitId) {
        readInitial();

        // if
        Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Map<String, String> map = commit.getMap();

        // Removes tracked files that are not present in that commit.
        for (File file: CWD.listFiles()) {
            if (!map.containsKey(file)) {
                file.delete();
            }
        }

        // write
        for (String filename: map.keySet()) {
            writeContents(join(CWD, filename), join(BLOB_DIR, map.get(filename)));
        }

        // The staging area is cleared.
        stageMap.clear();
        for (File file: STAGE_MAP.listFiles()) {
            file.delete();
        }

        // moves the current branch’s head to that commit node
        head = commit;

        writeInitial();
    }

    /**
     * ============================= private Utils ===========================
     */
    private static void readInitial() {
        if (!GITLET_DIR.exists()) {
            /** If a user inputs a command that requires being in an initialized Gitlet working directory,
             * but is not in such a directory, print this error message. */
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(1);
        }
        head = readObject(HEAD, Commit.class);
        stageMap = readObject(STAGE_MAP, TreeMap.class);
        branchList = readObject(BRANCH, ArrayList.class);
    }

    private static void writeInitial() {
        writeObject(HEAD, head); // the current commit
        writeObject(STAGE_MAP, (Serializable) stageMap); // the map used to store the all information of stage area
        writeObject(BRANCH, (Serializable) branchList); // the branch list
    }
}
