package gitlet;

import java.io.Serializable;

/**
 * this class is used to record the information of branch
 */
public class Branch implements Serializable {
    private String name;

    private Commit commit;

    public Branch(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }
}
