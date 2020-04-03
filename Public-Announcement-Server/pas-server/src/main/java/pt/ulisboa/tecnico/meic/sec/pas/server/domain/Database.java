package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public class Database {

    AnnouncementBoard generalBoard;
    List<User> users = new ArrayList<User>();

    private boolean containsKey(final List<User> list, final Long keyHash) {
        return list.stream().filter(o -> o.getId().equals(keyHash)).findFirst().isPresent();
    }

    public int register(Key publicKey, String name, String password){

        // check if the received fields are valid
        long pubKeyHash = publicKey.hashCode();
        if (containsKey(users, pubKeyHash)) {
            return 400;
        }

        User newUser = new User(publicKey, name, password);
        users.add(newUser);

        return 200;
    }



    // public void post(Key publicKey, String message, List<Announcement> references, long creationTime, String password, byte[] signature, byte[] encryptedAESkey)



}