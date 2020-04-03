package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.security.Key;

public class User {

    private AnnouncementBoard personalBoard;
    private String name;
    private long passwordHash;

    public User(Key publicKey, String name, String password) {
        this.personalBoard = new AnnouncementBoard();
        this.name = name;
        this.passwordHash = password.hashCode();
    }

    public AnnouncementBoard getPersonalBoard() {
        return this.personalBoard;
    }

    public String getName() {
        return this.name;
    }

    public Long getPasswordHash() {
        return this.passwordHash;
    }
    
}