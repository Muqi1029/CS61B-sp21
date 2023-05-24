package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
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

    private Commit parent;

    private Date timestamp;

    private String author;


    /** map from fileName to sha-1 */
    private Map<String, String> map = new TreeMap<>();

    public Commit(String author, String message, Commit parent) {
        this.author = author;
        this.parent = parent;
        this.message = message;
        this.timestamp = new Date();
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
}
