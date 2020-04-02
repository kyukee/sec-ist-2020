package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.util.List;

public class Announcement {

    User user;
    long creationTime; // as an epoch
    List<Announcement> references;
    String message;

    // String cutString = StringUtils.left(string, 256);

}