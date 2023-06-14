package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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

    /**
     * record file removed from the gitlet
     */
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
    private static final String AUTHOR = "muqi";

    /**
     * used for stage mapping from the name to SHA1
     */
    private static Map<String, String> stageMap = new TreeMap<>();

    /**
     * used to store the whole commit operations
     */
    private static List<Branch> branchList = new LinkedList<>();

    private static final File BRANCH = join(GITLET_DIR, "branch");


    /**
     * store the untracked files
     */
    private static final List<String> UNTRACKEDFILES = new ArrayList<>();


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

        /** make the initial commit */
        head = new Commit("initial commit", null, new Date(0), AUTHOR);
        branchList.add(new Branch("master", head));

        // commit
        writeObject(join(COMMIT_DIR, head.getId()), head);
        // head
        writeObject(HEAD, head);
        // stageMap
        writeObject(STAGE_MAP, (Serializable) stageMap);
        // branchList
        writeObject(BRANCH, (Serializable) branchList);
        // removalList
        writeObject(STAGE_REMOVAL, (Serializable) removalFileList);
    }

    /**
     * 1. add a copy of the file as it currently exists to the staging area
     * 2. if staging an already-staged file, overwrite the previous version
     * 3. if the file to be staged is identical to current commit Version,
     * don't stage and remove it if it exists in stage area
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
        } else {
            removalFileList.remove(fileName);
        }
        // if these two versions are identical, do nothing
        writeEnd();
    }

    /**
     * 1. only update the contents of files in the stage area
     * 2. saves a snapshot of tracked files in the current commit and staging area
     * 3. the staging area is cleared after a commit
     *
     * @param message the commit message
     */
    public static void commit(String message, Commit secondParent) {

        readInitial();

        /** if no files have been staged, abort */
        if (stageMap.size() == 0 && removalFileList.isEmpty()) {
            System.out.println("No changes added to the commit");
            System.exit(0);
        }

        /** every commit must have a non-blank message */
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        // 1. create a commit object whose parent is head
        Commit commit = new Commit(message, head, AUTHOR);
        if (secondParent != null) {
            commit.setSecondParent(secondParent);
        }

        Map<String, String> newMap = new TreeMap<>(head.getMap());

        // 2. update from stage area and write the files into blob directory
        for (String fileName : stageMap.keySet()) {
            String sha1 = stageMap.get(fileName);
            newMap.put(fileName, sha1);
            writeContents(join(BLOB_DIR, sha1), readContents(join(CWD, fileName)));
        }

        /** remove the file in gitlet depending on the rm operation */
        for (String removedName : removalFileList) {
            newMap.remove(removedName);
        }
        commit.setMap(newMap);

        // 3. clear the staging area
        stageMap.clear();
        for (File file : Objects.requireNonNull(STAGE_DIR.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        removalFileList.clear();

        // 4. write this commit object into commit directory
        writeObject(join(COMMIT_DIR, commit.getId()), commit);

        // 5. set head to the new commit
        head = commit;
        Branch branch = branchList.get(0);
        branch.setCommit(head);

        writeEnd();
    }

    /**
     * @param fileName the file name you want to remove
     */
    public static void rm(String fileName) {
        readInitial();

        Map<String, String> map = head.getMap();
        String currentVersion = stageMap.getOrDefault(fileName, null);

        if (!map.containsKey(fileName) && !stageMap.containsKey(fileName)) {
            /** if the file is neither staged nor tracked by the head commit,
             *  print the error message */
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        // unstage the file if it is currently staged for addition
        if (currentVersion != null) {
            stageMap.remove(fileName);
            join(STAGE_DIR, currentVersion).delete();
        }

        // if the file is tracked in the current commit, stage it for removal
        // and remove the file from the working directory
        if (map.containsKey(fileName)) {
            /** don't remove the file unless it is tracked in the current commit */
            join(CWD, fileName).delete();
            removalFileList.add(fileName);
        }
        writeEnd();
    }

    /**
     * Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the commit
     */
    public static void log() {
        readInitial();
        Commit commit = head;
        while (commit != null) {
            printInformation(commit);
            commit = commit.getParent();
        }
    }


    /**
     * Like log, except displays information about all commits ever made
     * The order of the commits does not matter
     */
    public static void globalLog() {
        /** iterate over files within the COMMIT_DIR */
        for (String fileName
                : Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR))) {
            Commit commit = readObject(join(COMMIT_DIR, fileName), Commit.class);
            printInformation(commit);
        }
    }

    private static void printInformation(Commit commit) {

        System.out.println("===");
        // System.out.println("log :" + commit); // used for testing
        System.out.println("commit " + commit.getId());

        if (commit.getSecondParent() != null) {
            System.out.printf("Merge: %.7s %.7s", commit.getParent().getId(), commit.getSecondParent().getId());
            System.out.println();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyy Z", Locale.ENGLISH);
        System.out.println("Date: " + sdf.format(commit.getTimestamp()));

        System.out.println(commit.getMessage());
        System.out.println();
    }


    /**
     * prints out the ids of all commits that have the given commit message
     *
     * @param message the message of commit
     */
    public static void find(String message) {
        readInitial();

        boolean flag = false;
        for (String name
                : Objects.requireNonNull(plainFilenamesIn(COMMIT_DIR))) {
            Commit commit = readObject(join(COMMIT_DIR, name), Commit.class);
            if (commit.getMessage().equals(message)) {
                System.out.println(commit.getId());
                flag = true;
            }
        }
        if (!flag) {
            /** if no such commit exists, prints the error message */
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * -------------------- status -------------------------
     */
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

        System.out.println("=== Removed Files ===");
        for (String name : removalFileList) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");

        // Tracked in the current commit, changed in the working directory, but not staged;
        // or Staged for addition, but with different contents than in the working directory;
        // or Staged for addition, but deleted in the working directory;
        // or Not staged for removal, but tracked in the current commit and deleted from the working directory.

        System.out.println();

        System.out.println("=== Untracked Files ===");
        /** the untracked files refer to those that not only don't exist
         * in previous snapshots, but also in the stage area.  */
        showFiles(CWD.listFiles());
        Collections.sort(UNTRACKEDFILES);
        for (String name : UNTRACKEDFILES) {
            System.out.println(name);
        }
        System.out.println();
        UNTRACKEDFILES.clear();
    }

    private static void showFiles(File[] files) {
        for (File file : files) {
            if (!file.isDirectory()) {
                if (!head.getMap().containsKey(file.getName()) && !stageMap.containsKey(file.getName())) {
                    // System.out.println(file.getName());
                    UNTRACKEDFILES.add(file.getName());
                }
            }
        }
    }

    /**
     * ----------------------- checkout --------------------------
     */
    public static void checkout(String[] args) {
        readInitial();

        if (args[1].equals("--") && args.length == 3) {
            /** java gitlet.Main checkout -- [file name] */
            // Takes the version of the file as it exists in the head commit and puts it in the working directory,
            // overwriting the version of the file that’s already there if there is one.
            // The new version of the file is not staged.

            Map<String, String> map = head.getMap();
            checkIsExist(map, args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            /** java gitlet.Main checkout [commit id] -- [file name] */
            if (!(join(COMMIT_DIR, args[1]).exists())) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            Commit commit = readObject(join(COMMIT_DIR, args[1]), Commit.class);
            checkIsExist(commit.getMap(), args[3]);
        } else if (args.length == 2) {
            /** java gitlet.Main checkout [branch name] */
            int i;
            for (i = 0; i < branchList.size(); i++) {
                Branch branch = branchList.get(i);
                String branchName = branch.getName();
                if (i == 0 && branchName.equals(args[1])) {
                    // if that branch is the current branch
                    System.out.println("No need to checkout the current branch.");
                    System.exit(0);
                } else if (branchName.equals(args[1])) {
                    Commit commit = branch.getCommit();

                    checkCommit(commit);

                    branchList.remove(i);
                    branchList.add(0, branch); // add the head of the branch
                    head = commit;

                    break;
                }
            }
            if (i == branchList.size()) {
                // doesn't have the branch name
                System.out.println("No such branch exists.");
            }
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        // persistence
        writeEnd();
    }

    private static void checkCommit(Commit commit) {
        Map<String, String> currentMap = head.getMap();
        Map<String, String> checkoutBranchMap = commit.getMap();

        // check
        for (String fileName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!currentMap.containsKey(fileName) && checkoutBranchMap.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        // put the files in the commit at the head of the given branch in the working directory
        for (String filename : checkoutBranchMap.keySet()) {
            /**
             * overwriting the versions of the files
             */
            File file = join(BLOB_DIR, checkoutBranchMap.get(filename));
            writeContents(join(CWD, filename), readContents(file));
        }

        for (String fileName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            /** Any files that tracked in the current branch
             * but are not present in the checked-out branch
             * are deleted
             * */
            if (!checkoutBranchMap.containsKey(fileName)) {
                join(CWD, fileName).delete();
            }
        }
        stageMap.clear();
        for (String fileName : stageMap.values()) {
            join(STAGE_DIR, fileName).delete();
        }
        removalFileList.clear();
    }

    /**
     * if file is in the map, check out the file into the CWD
     * and then clear the stage with respect to the file
     *
     * @param map      map from file name to sha1
     * @param filename filename
     */
    private static void checkIsExist(Map<String, String> map, String filename) {
        // get the filename in the stage area
        String sha1 = map.getOrDefault(filename, null);
        if (sha1 == null) {
            // if the file does not exist in the previous commit, aborting
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            /** 1. overwrite the file */
            // obtain the file
            File file = join(BLOB_DIR, sha1);
            writeContents(join(CWD, filename), readContents(file));

            /** 2. clear the stage with respect to the new version */
            String sha1name = stageMap.getOrDefault(filename, null);
            if (sha1name != null) {
                // if the filename is in stage area, unstage the file
                stageMap.remove(filename);
                join(STAGE_DIR, sha1name).delete();
            }
        }
    }


    /**
     * ======================= branch ==============================
     */

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
        writeEnd();
    }

    /**
     * =================== rm-branch ===========================
     */
    public static void rmBranch(String branchName) {
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
        writeEnd();
    }


    /**
     * ========================= reset ===============================
     * Checks out all the files tracked by the given commit
     * Removes tracked files that are not present in that commit
     * move the current branch's head to that commit node
     * The staging area is cleared
     */
    public static void reset(String commitId) {
        readInitial();
        // find the commit
        Commit commit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        checkCommit(commit);
        // moves the current branch’s head to that commit node
        head = commit;
        Branch branch = branchList.get(0);
        branch.setCommit(head);
        writeEnd();
    }

    /**
     * == MERGE files from the given branch into the current branch
     * if the split point is the same commit as the given branch,
     * then we do nothing ("Given branch is an ancestor of the current branch")
     * if the split point is the current branch,
     * then the effect is to check out the given branch ("Current branch fast-forward.")
     * else (the more common situation):
     * 1. MODIFIED in OTHER but not HEAD -> OTHER
     * 2. MODIFIED in HEAD but not OTHER -> HEAD
     * 3. MODIFIED in BOTH:
     * if changed in the same way(either deleted or changed): -> HEAD
     * if changed in the different way: -> CONFLICT
     * 4. NOT in SPLIT nor OTHER but in HEAD -> HEAD
     * 5. NOT in SPLIT nor HEAD but in OTHER -> OTHER
     * 6. UNMODIFIED in HEAD and PRESENT in SPLIT but NOT in OTHER -> REMOVE
     * 7. UNMODIFIED in OTHER and PRESENT in SPLIT but NOT in HEAD -> STAY ABSENT
     */
    public static void merge(String branchName) {
        readInitial();

        boolean isConflict = false;

        // check whether there are staged additions or removals present
        if (stageMap.size() != 0 || removalFileList.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }

        if (branchList.get(0).getName().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself");
            System.exit(0);
        }

        // check whether the branch exists
        Branch other = null;
        boolean isFind = false;
        for (Branch branch : branchList) {
            if (branch.getName().equals(branchName)) {
                isFind = true;
                other = branch;
            }
        }
        if (!isFind) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        // find the split Commit
        Commit split = findCommonAncestor(head, other.getCommit());
//        System.out.println("==check==");

//        System.out.println(split);
//        System.out.println(split == null);
//        printInformation(head.getParent());
//        System.out.println(head.getParent());
//        System.out.println(head.getParent().hashCode());
//
//        printInformation(other.getCommit().getParent());
//        System.out.println(other.getCommit().getParent());
//        System.out.println(other.getCommit().getParent().hashCode());

        if (split == null) {
            System.out.println("These two branches don't have a common ancestor");
            System.exit(0);
        }

        if (split.equals(other.getCommit())) {
            /** If the split point is the same commit as the given branch,
             *  then we do nothing; the merge is complete,
             *  and the operation ends with the message */
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (split.equals(head)) {
            /** If the split point is the current branch,
             * then the effect is to check out the given branch,
             * and the operation ends after printing the message  */
            checkout(new String[]{"checkout", other.getName()});
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        Map<String, String> splitMap = split.getMap();
        Map<String, String> headMap = head.getMap();
        Map<String, String> otherMap = other.getCommit().getMap();

        /** traverse in split Commit */
        for (String fileName : splitMap.keySet()) {
            String splitVersion = splitMap.get(fileName);
            String headVersion = headMap.getOrDefault(fileName, null);
            String otherVersion = otherMap.getOrDefault(fileName, null);

            /** modify */
            if (headVersion != null && otherVersion != null) {
                if (!splitVersion.equals(otherVersion) && splitVersion.equals(headVersion)) {
                    writeContents(join(CWD, fileName), readContents(join(BLOB_DIR, otherVersion)));
                    putStage(fileName, join(CWD, fileName));
                }
            }

            /**
             * Conflict 1
             * */
            if (headVersion == null && otherVersion != null && !splitVersion.equals(otherVersion)) {
                File file = join(BLOB_DIR, otherVersion);
                writeContents(join(CWD, fileName), "<<<<<<< HEAD\n=======\n", readContents(file), "\n>>>>>>>");
                putStage(fileName, file);
                isConflict = true;
            }

            /** conflict 2 */
            if (otherVersion == null && headVersion != null && !headVersion.equals(splitVersion)) {
                File file = join(BLOB_DIR, headVersion);
                writeContents(join(CWD, fileName), "<<<<<<< HEAD\n", readContents(join(file)), "\n=======\n>>>>>>>");
                putStage(fileName, file);
                isConflict = true;
            }

            /**
             * conflict 3
             */
            if (headVersion != null && otherVersion != null && !otherVersion.equals(splitVersion) && !headVersion.equals(splitVersion) && !headVersion.equals(otherVersion)) {
                File file1 = join(BLOB_DIR, headVersion);
                File file2 = join(BLOB_DIR, otherVersion);

                writeContents(join(CWD, fileName), "<<<<<<< HEAD\n", readContents(file1),
                        "=======\n", readContents(file2), "\n>>>>>>>");
                putStage(fileName, join(CWD, fileName));
                isConflict = true;
            }

            /**
             * delete 1
             */
            if (otherVersion == null && headVersion != null && headVersion.equals(splitVersion)) {
                join(CWD, fileName).delete();
                removalFileList.add(fileName);
            }
        }

        /** up here we have solved the files in the split */

        // 5. NOT in SPLIT nor HEAD but in OTHER -> OTHER
        for (String fileName : otherMap.keySet()) {
            if (!splitMap.containsKey(fileName)) {
                String otherVersion = otherMap.get(fileName);
                String headVersion = headMap.getOrDefault(fileName, null);
                if (headVersion == null) {
                    /** change */
                    File file = join(BLOB_DIR, otherVersion);
                    writeContents(join(CWD, fileName), readContents(file));
                    putStage(fileName, join(CWD, fileName));
                } else if (!headVersion.equals(otherVersion)) {
                    /** Conflict 4 */
                    File file1 = join(BLOB_DIR, headVersion);
                    File file2 = join(BLOB_DIR, otherVersion);
                    writeContents(join(CWD, fileName), "<<<<<<< HEAD\n", readContents(file1),
                            "=======\n", readContents(file2), "\n>>>>>>>");
                    putStage(fileName, join(CWD, fileName));
                    isConflict = true;
                }
            }
        }

        if (!isConflict) {
            writeEnd();
            commit("Merged " + branchName + " into " + branchList.get(0).getName(), other.getCommit());
        } else {
            System.out.println("Encountered a merge conflict.");
            writeEnd();
        }
    }

    private static void putStage(String fileName, File file) {
        String shaName = sha1(readContents(file));
        stageMap.put(fileName, shaName);
        writeContents(join(STAGE_DIR, shaName), readContents(file));
    }

    private static Commit findCommonAncestor(Commit commit1, Commit commit2) {
        HashSet<String> ancestors = new HashSet<>();
        while (commit1 != null) {
            ancestors.add(commit1.getId());
            commit1 = commit1.getParent();
        }

        while (commit2 != null) {
            if (ancestors.contains(commit2.getId())) {
                return readObject(join(COMMIT_DIR, commit2.getId()), Commit.class);
            }
            commit2 = commit2.getParent();
        }
        return null;
    }

    /**
     * ============================= private Utils ===========================
     */
    private static void readInitial() {
        if (!GITLET_DIR.exists()) {
            /** If a user inputs a command that requires being in an initialized Gitlet working directory,
             * but is not in such a directory, print this error message. */
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        removalFileList = readObject(STAGE_REMOVAL, ArrayList.class);
        head = readObject(HEAD, Commit.class);
        stageMap = readObject(STAGE_MAP, TreeMap.class);
        branchList = readObject(BRANCH, LinkedList.class);
    }

    private static void writeEnd() {
        writeObject(HEAD, head); // the current commit
        writeObject(STAGE_MAP, (Serializable) stageMap); // the map used to store the all information of stage area
        writeObject(BRANCH, (Serializable) branchList); // the branch list
        writeObject(STAGE_REMOVAL, (Serializable) removalFileList); //
    }
}
