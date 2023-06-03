package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author  Muqi
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** the parent of this commit */
    private Commit parent;

    /** the time of commit */
    //TODO format: 00:00:00 UTC, Thursday, 1 January 1970
    private Date timestamp;

    /** metadata: the author of this commit  */
    private String author;

    /** map from fileName to sha-1 */
    private Map<String, String> map = new TreeMap<>();

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

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    /** get SHA-1 id, which must include the file references of its files, parent reference, log message, and commit time */
    public String getId() {
        StringBuilder valuesInBlob = new StringBuilder();
        for (String value: map.values()) {
            valuesInBlob.append(value);
        }
        if (parent == null) {
            return Utils.sha1(message, timestamp.toString(), valuesInBlob.toString());
        }
        return Utils.sha1(message, parent.toString(), timestamp.toString(), valuesInBlob.toString());
    }
}
