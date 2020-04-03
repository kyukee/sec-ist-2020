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

public class RegisterTest extends BaseTest {

    private PasClientApp client1;
	private PasClientApp client2;
	private PasClientApp client3;

	@Before
	public void setup() throws UnknownHostException, IOException {

        client1 = createClient("client1", "server1");		
		client2 = createClient("client2", "server1");
		client3 = createClient("client3", "server1");

	}

	@Test
	public void correctArgumentsCorrectResult() throws Exception {

        String name = "John";
        String serverPassword = "password";

		int resp1 = client1.register(name, serverPassword);
		int resp2 = client2.register(name, serverPassword);
		int resp3 = client3.register(name, serverPassword);
		
		assertEquals(200, resp1);
		assertEquals(200, resp2);
		assertEquals(200, resp3);
	}

	@Test
	public void failure1() throws Exception {

		KeyPair keys2 = RSA.getKeyPairFromKeyStore("client2", "password", "/client-keystore.jks", "password");
		Key pubKey2 = keys2.getPublic();
		client1.setPubKey(pubKey2);

		String name = "John";
		String serverPassword = "password";

		int resp1 = client1.register(name, serverPassword);

		assertEquals(400, resp1);
	}

	@After
	public void tearDown() throws IOException {
		for (ManagedChannel channel : channels) {
			channel.shutdown();
		}
	}
}
