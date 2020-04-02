package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.security.Key;

public class User {

    private Key publicKey;
    private AnnouncementBoard personalBoard;
    private String name;
    private String password;
    

    public User(Key publicKey, String name, String password) {
        this.publicKey = publicKey;
        this.personalBoard = new AnnouncementBoard();
        this.name = name;
        this.password = password;
    }

    public Key getPublicKey() {
        return this.publicKey;
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
    
}