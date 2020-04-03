package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.security.Key;
import java.util.Random;

public class User {

    private AnnouncementBoard personalBoard;
    private String name;
    private String password;
    private long _id;

    public User(Key publicKey, String name, String password) {
        this._id = publicKey.hashCode();
        this.personalBoard = new AnnouncementBoard();
        this.name = name;
        this.password = password;
    }

    public AnnouncementBoard getPersonalBoard() {
        return this.personalBoard;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
    
    public Long getId() {
        return this._id;
    }
}