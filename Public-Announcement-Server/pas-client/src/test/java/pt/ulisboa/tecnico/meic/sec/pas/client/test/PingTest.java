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
	public void givenClient_whenServerEchosMessage_thenCorrect() throws IOException {
	    String resp1 = client1.ping("hello");
	    System.out.println(resp1);

	    assertEquals(resp1, "hello");
	}

}
