package pt.ulisboa.tecnico.meic.sec.pas.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.meic.sec.pas.client.PasClientApp;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.Announcement;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class ReadTest extends BaseTest {

    private static PasClientApp client1;
    private static PasClientApp client2;
    private static PasClientApp client3;

    @BeforeClass
    public static void testSetup() throws Exception {

        client1 = createClient("client1", "server1");
        client2 = createClient("client2", "server1");
        client3 = createClient("client3", "server1");

        String name = "John";
        String serverPassword = "password";

        client1.register(name, serverPassword);
        client2.register(name, serverPassword);
        client3.register(name, serverPassword);

        client1.post(serverPassword, "This is Message 1-a", null);
        client1.post(serverPassword, "This is Message 1-b", null);
        client2.post(serverPassword, "This is Message 2-a", null);
        client3.post(serverPassword, "This is Message 3-a", null);
    }

    @Test
    public void correctArgumentsCorrectResult1() throws Exception {
        List<Announcement> resp1 = client1.read(client1.getPubKey(), 1);
        List<Announcement> resp2 = client1.read(client2.getPubKey(), 1);
        List<Announcement> resp3 = client1.read(client3.getPubKey(), 1);

        assertEquals(1, resp1.size());
        assertEquals(1, resp2.size());
        assertEquals(1, resp3.size());
    }

    @Test
    public void correctArgumentsCorrectResult2() throws Exception {
        List<Announcement> resp1 = client1.read(client1.getPubKey(), 2);
        List<Announcement> resp2 = client1.read(client1.getPubKey(), 0);

        assertEquals(2, resp1.size());
        assertEquals(2, resp2.size());
    }

    @AfterClass
    public static void finalTearDown() throws IOException {
        for (ManagedChannel channel : channels) {
            channel.shutdown();
        }
    }

    // TODO somehow reset the database between different Tests

}
