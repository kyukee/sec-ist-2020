package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.util.Comparator;
import java.util.List;

public class Announcement implements Comparator<Announcement> {

    User user;
    long creationTime; // as an epoch
    List<Announcement> references;
    String message;

    // cut string to 256 characters
    // String cutString = StringUtils.left(string, 256);

    public Announcement(User user, long creationTime, List<Announcement> references, String message) {
        this.user = user;
        this.creationTime = creationTime;
        this.references = references;
        this.message = message;
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

    public int compare(Announcement first, Announcement second) {
        // the more recent one is greater
        return (int) (first.getCreationTime() - second.getCreationTime());
    }
}