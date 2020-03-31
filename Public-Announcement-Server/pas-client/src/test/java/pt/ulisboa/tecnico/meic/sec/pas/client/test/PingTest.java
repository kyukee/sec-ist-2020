package pt.ulisboa.tecnico.meic.sec.pas.client.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.grpc.ManagedChannel;
import pt.ulisboa.tecnico.meic.sec.pas.client.PasClientApp;

public class PingTest extends BaseTest {

  private static PasClientApp client1;
	ManagedChannel channel;

	@Before
	public void setup() throws UnknownHostException, IOException {
	    client1 = new PasClientApp();
	    channel = PasClientApp.createChannel(hosts.get("server1"), ports.get("server1"));
	    client1.startConnection(channel);
	}

	@After
	public void tearDown() throws IOException {
		channel.shutdown();
	}

	@Test
	public void givenClient_whenServerEchosMessage_thenCorrect() throws IOException {
	    String resp1 = client1.ping("hello");
	    System.out.println(resp1);

	    assertEquals(resp1, "hello");
	}

}
