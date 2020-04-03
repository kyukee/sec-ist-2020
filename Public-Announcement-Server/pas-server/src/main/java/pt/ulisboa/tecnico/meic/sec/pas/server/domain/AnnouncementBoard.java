package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnouncementBoard {

    private Map<Long, Announcement> announcements;
    
    public AnnouncementBoard() {
        this.announcements = new ConcurrentHashMap<Long, Announcement>();
    }

    public Map<Long, Announcement> getAnnouncements() {
        return this.announcements;
    }

    public void addAnnouncement(Announcement announcement){
        long hash = announcement.hashCode();
        this.announcements.put(hash, announcement);
    }
    
}