package pt.ulisboa.tecnico.meic.sec.pas.client.test;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.meic.sec.pas.client.PasClientApp;
import pt.ulisboa.tecnico.meic.sirs.RSA;

public class BaseTest {

  private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	public static Map<String,String> hosts = new HashMap<String, String>();
	public static Map<String,Integer> ports = new HashMap<String, Integer>();

	public static List<ManagedChannel> channels = new ArrayList<ManagedChannel>();

	public static PasClientApp createClient(String clientName, String serverName){
		java.security.KeyPair keys = RSA.getKeyPairFromKeyStore(clientName, "password", "/client-keystore.jks", "password");
		Key serverPubKey = RSA.getKeyPairFromKeyStore(serverName, "password", "/server-keystore.jks", "password").getPublic();
		PasClientApp client = new PasClientApp(keys.getPrivate(), keys.getPublic(), serverPubKey);
		ManagedChannel channel = PasClientApp.createChannel(hosts.get(serverName), ports.get(serverName));
		channels.add(channel);
		client.startConnection(channel);
		return client;
	}

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseTest.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Loaded test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String server;
		String host;
		int port;

		server = "server1";
		host = testProps.getProperty(server + ".host");
    	port = Integer.parseInt(testProps.getProperty(server + ".port"));
		hosts.put(server, host);
		ports.put(server, port);

		server = "server2";
		host = testProps.getProperty(server + ".host");
    	port = Integer.parseInt(testProps.getProperty(server + ".port"));
		hosts.put(server, host);
		ports.put(server, port);

	}

	@AfterClass
	public static void cleanup() {
	}

}
