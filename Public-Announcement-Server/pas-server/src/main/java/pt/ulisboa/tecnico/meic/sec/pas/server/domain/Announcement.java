package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.DataUtils;

public class Announcement implements Comparator<Announcement> {

    User user;
    long creationTime; // as an epoch
    List<Announcement> references;
    String message;
    long _id;

    // cut string to 256 characters
    // String cutString = StringUtils.left(string, 256);

    public Announcement(User user, long creationTime, List<Announcement> references, String message) {
        this.user = user;
        this.creationTime = creationTime;
        this.references = references;
        this.message = message;

        long msg = message.hashCode();
        this._id = msg ^ creationTime;
    }

    public User getUser() {
        return this.user;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public List<Announcement> getReferences() {
        return this.references;
    }

    public String getMessage() {
        return this.message;
    }

    public long get_id() {
        return this._id;
    }

    public int compare(Announcement first, Announcement second) {
        // the more recent one is greater
        return (int) (first.getCreationTime() - second.getCreationTime());
    }

}