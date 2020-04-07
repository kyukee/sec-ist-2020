package pt.ulisboa.tecnico.meic.sec.pas.server.domain;

import java.security.Key;
import java.security.PublicKey;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Database {

    AnnouncementBoard generalBoard;
    Map<Long,User> users = new ConcurrentHashMap<Long,User>();

    public int register(Key publicKey, String name, String password){

        // check if the received fields are valid
        long pubKeyHash = publicKey.hashCode();
        if (users.containsKey(pubKeyHash)) {
            return 400;
        }

        User newUser = new User(publicKey, name, password);
        users.put(pubKeyHash, newUser);

        return 200;
    }


    public int post(Key publicKey, String message, List<Long> references, long receivedEpoch, String password) {

        // check user exists
        long pubKeyHash = publicKey.hashCode();
        if (! users.containsKey(pubKeyHash)) {
            return 400;
        }

        // check password is correct
        User user = users.get(pubKeyHash);
        long passHash = password.hashCode();
        if (! user.getPasswordHash().equals(passHash)){
            return 400;
        }

        Announcement announcement = new Announcement(pubKeyHash, receivedEpoch, references, message);
        user.getPersonalBoard().addAnnouncement(announcement);

        return 200;
    }

    public List<Announcement> read(PublicKey announcementKey, int number) {

        // check user exists
        long pubKeyHash = announcementKey.hashCode();
        if (!users.containsKey(pubKeyHash)) {
            return null;
        }

        User user = users.get(pubKeyHash);
        List<Announcement> list = new ArrayList<Announcement>(user.getPersonalBoard().getAnnouncementMap().values());

        List<Announcement> sortedList = list.stream()
            .sorted(Comparator.comparing(Announcement::getCreationTime)).
            collect(Collectors.toList());

        if ((number == 0)) {
            return sortedList;
        }

        return sortedList.stream().limit(number).collect(Collectors.toList());
    }

}
