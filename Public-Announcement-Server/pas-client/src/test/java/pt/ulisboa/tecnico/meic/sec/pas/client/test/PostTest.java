package pt.ulisboa.tecnico.meic.sec.pas.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.KeyPair;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.meic.sec.pas.client.PasClientApp;
import pt.ulisboa.tecnico.meic.sirs.DataUtils;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class PostTest extends BaseTest {

    // private PasClientApp client1;
	// private PasClientApp client2;
	// private PasClientApp client3;

	// @Before
	// public void setup() throws Exception {

    //     client1 = createClient("client1", "server1");
	// 	client2 = createClient("client2", "server1");
	// 	client3 = createClient("client3", "server1");

    //     String name = "John";
    //     String serverPassword = "password";

	// 	client1.register(name, serverPassword);
    //     client2.register(name, serverPassword);
    //     client3.register(name, serverPassword);
	// }

	// @Test
	// public void correctArgumentsCorrectResult() throws Exception {

    //     String serverPassword = "password";

    //     int resp1 = client1.post(serverPassword, "This is Message 1-a", null);
    //     int resp2 = client1.post(serverPassword, "This is Message 1-b", null);
    //     int resp3 = client2.post(serverPassword, "This is Message 2-a", null);
    //     int resp4 = client3.post(serverPassword, "This is Message 3-a", null);

    //     assertEquals(200, resp1);
    //     assertEquals(200, resp2);
    //     assertEquals(200, resp3);
    //     assertEquals(200, resp4);
	// }

	// @After
	// public void tearDown() throws IOException {
	// 	for (ManagedChannel channel : channels) {
	// 		channel.shutdown();
	// 	}
	// }
}
