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

	@Before
	public void setup() throws UnknownHostException, IOException {

		// connect client1 to server1
		client1 = createClient("client1", "server1");
	}

	@Test
	public void givenClient_whenServerEchosMessage_thenCorrect() throws IOException {
	    String resp1 = client1.ping("hello");
	    System.out.println(resp1);

	    assertEquals(resp1, "hello");
	}

	@After
	public void tearDown() throws IOException {
		for (ManagedChannel channel : channels) {
			channel.shutdown();
		}
	}

}
