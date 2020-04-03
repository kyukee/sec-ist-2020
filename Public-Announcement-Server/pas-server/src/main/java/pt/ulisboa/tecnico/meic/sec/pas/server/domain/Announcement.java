package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.util.Comparator;
import java.util.List;

public class Announcement implements Comparator<Announcement> {

    private long creationTime; // as an epoch
    private List<Long> references;
    private String message;
    private long _id;
    private long _userId;

    public Announcement(long userKeyHash, long creationTime, List<Long> references, String message) {
        this._userId = userKeyHash;
        this.creationTime = creationTime;
        this.references = references;
        this.message = message;

        long msg = message.hashCode();
        this._id = msg ^ creationTime;
    }

    public long getUser() {
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