package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author Muqi
 */
public class Commit implements Serializable {

    /**
     * The message of this Commit.
     */
    private final String message;

    /**
     * the parent of this commit
     */
    private final Commit parent;

    /**
     * the time of commit
     */
    //TODO format: 00:00:00 UTC, Thursday, 1 January 1970
    private final Date timestamp;

    /**
     * the author of this commit
     */
    private final String author;

    /**
     * map from fileName to sha-1
     */
    private Map<String, String> map = new TreeMap<>();


    /**
     * used to record the second parent when merged
     */
    private Commit secondParent;

    public Commit(String message, Commit parent, String author) {
        this.author = author;
        this.parent = parent;
        this.message = message;
        this.timestamp = new Date();
    }

    public Commit(String message, Commit parent, Date timestamp, String author) {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        return parent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    /**
     * get SHA-1 id, which must include the file references of its files, parent reference, log message, and commit time
     */
    public String getId() {
        StringBuilder valuesInBlob = new StringBuilder();
        for (String value : map.values()) {
            valuesInBlob.append(value);
        }
        if (parent == null && secondParent == null) {
            return Utils.sha1(message, timestamp.toString(), valuesInBlob.toString());
        }else if (parent != null && secondParent == null) {
            return Utils.sha1(message, parent.toString(), timestamp.toString(), valuesInBlob.toString());
        }else if (parent != null && secondParent != null) {
            return Utils.sha1(message, parent.toString(), secondParent.toString(), timestamp.toString(), valuesInBlob.toString());
        }
        return null;
    }

    public Commit getSecondParent() {
        return secondParent;
    }

    public void setSecondParent(Commit secondParent) {
        this.secondParent = secondParent;
    }

    /**
     * used to debug
     */
    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", parent=" + parent +
                ", timestamp=" + timestamp +
                ", author='" + author + '\'' +
                ", map=" + map +
                ", secondParent=" + secondParent +
                '}';
    }
}
