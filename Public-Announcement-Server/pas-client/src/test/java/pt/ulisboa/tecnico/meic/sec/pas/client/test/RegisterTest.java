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
	private ManagedChannel channel1;

	@Before
	public void setup() throws UnknownHostException, IOException {
        String server;

        // connect client1 to server1
        client1 = new PasClientApp();
        server = "server1";
	    channel1 = PasClientApp.createChannel(hosts.get(server), ports.get(server));
	    client1.startConnection(channel1);

	}

	@After
	public void tearDown() throws IOException {
		channel1.shutdown();
	}

	@Test
	public void correctArgumentsCorrectResult() throws Exception {

		KeyPair keys1 = RSA.getKeyPairFromKeyStore("client1", "password", "/client-keystore.jks", "password");
        Key privKey1 = keys1.getPrivate();
		Key pubKey1 = keys1.getPublic();

		KeyPair keys2 = RSA.getKeyPairFromKeyStore("client2", "password", "/client-keystore.jks", "password");
		Key privKey2 = keys2.getPrivate();
		Key pubKey2 = keys2.getPublic();

		KeyPair keys3 = RSA.getKeyPairFromKeyStore("client3", "password", "/client-keystore.jks", "password");
		Key privKey3 = keys3.getPrivate();
		Key pubKey3 = keys3.getPublic();

        Key serverPubKey = RSA.getKeyPairFromKeyStore("server1", "password", "/server-keystore.jks", "password").getPublic();
        String name = "John";
        String serverPassword = "password";

		int resp1 = client1.register(privKey1, pubKey1, serverPubKey, name, serverPassword);
		int resp2 = client1.register(privKey2, pubKey2, serverPubKey, name, serverPassword);
		int resp3 = client1.register(privKey3, pubKey3, serverPubKey, name, serverPassword);
		
		assertEquals(200, resp1);
		assertEquals(200, resp2);
		assertEquals(200, resp3);
	}

	@Test
	public void failure1() throws Exception {

		KeyPair keys1 = RSA.getKeyPairFromKeyStore("client1", "password", "/client-keystore.jks", "password");
		Key privKey1 = keys1.getPrivate();

		KeyPair keys2 = RSA.getKeyPairFromKeyStore("client2", "password", "/client-keystore.jks", "password");
		Key pubKey2 = keys2.getPublic();

		Key serverPubKey = RSA.getKeyPairFromKeyStore("server1", "password", "/server-keystore.jks", "password").getPublic();
		String name = "John";
		String serverPassword = "password";

		int resp1 = client1.register(privKey1, pubKey2, serverPubKey, name, serverPassword);

		assertEquals(400, resp1);
	}
}
