package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementBoard {

    List<Announcement> announcements;
    
    public AnnouncementBoard() {
        this.announcements = new ArrayList<>();
    }

    public List<Announcement> getAnnouncements() {
        return this.announcements;
    }
    
}