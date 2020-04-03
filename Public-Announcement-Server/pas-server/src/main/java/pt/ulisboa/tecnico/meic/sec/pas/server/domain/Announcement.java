package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.List;

import pt.ulisboa.tecnico.meic.sirs.DataUtils;

public class Announcement implements Comparator<Announcement> {

    Long creationTime; // as an epoch
    List<Long> references;
    String message;
    long _id;
    long _userId;

    // cut string to 256 characters
    // String cutString = StringUtils.left(string, 256);

    public Announcement(Long user, long creationTime, List<Long> references, String message) {
        this._userId = user;
        this.creationTime = creationTime;
        this.references = references;
        this.message = message;

        long msg = message.hashCode();
        this._id = msg ^ creationTime;
    }

    public Long getUser() {
        return this._userId;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public List<Long> getReferences() {
        return this.references;
    }

    public String getMessage() {
        return this.message;
    }

    public long getId() {
        return this._id;
    }

    public int compare(Announcement first, Announcement second) {
        return (int) (first.getId() - second.getId());
    }

}