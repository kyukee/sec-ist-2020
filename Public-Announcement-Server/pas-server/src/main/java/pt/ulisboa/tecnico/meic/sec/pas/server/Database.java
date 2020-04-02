package pt.ulisboa.tecnico.meic.sec.pas.server;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.meic.sec.pas.server.domain.*;

public class Database {

    AnnouncementBoard generalBoard;
    List<User> users = new ArrayList<User>();

    // TODO check if the received fields are valid
    public int register(Key publicKey, String name, String password){

        int status = 200;


        return status;
    }

    


}