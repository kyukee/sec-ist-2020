package pt.ulisboa.tecnico.meic.sec.pas.client;

import java.net.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.*;

import pt.ulisboa.tecnico.meic.sec.pas.grpc.PasServiceGrpc;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.HelloRequest;
import pt.ulisboa.tecnico.meic.sec.pas.grpc.Protocol.HelloResponse;
import pt.ulisboa.tecnico.meic.sec.pas.server.dto.Student;

public class PasClientApp {
//	private Socket clientSocket;
//    private PrintWriter outString;
//    private BufferedReader inString;
//    private ObjectOutputStream outObject;
//    private ObjectInputStream inObject;
//
//    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
//        clientSocket = new Socket(ip, port);
//        outString = new PrintWriter(clientSocket.getOutputStream(), true);
//        inString = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//        outObject = new ObjectOutputStream(clientSocket.getOutputStream());
//        inObject = new ObjectInputStream(clientSocket.getInputStream());
//    }
//
//    public void sendMessage(String msg) throws IOException {
//        outString.println(msg);
//    }
//
//    public String receiveMessage() throws IOException {
//        String resp = inString.readLine();
//        return resp;
//    }
//
//    public void sendObject(Object obj) throws IOException {
//    	System.out.println("Object to be written = " + obj);
//    	outObject.writeObject(obj);
//    }
//
//    public Object receiveObject() throws IOException, ClassNotFoundException {
//      Object obj = inObject.readObject();
//      System.out.println("Object received = " + obj);
//      return inObject.readObject();
//    }
//
//    public void stopConnection() throws IOException {
//        inString.close();
//        outString.close();
//        inObject.close();
//        outObject.close();
//        clientSocket.close();
//    }
    
    
    public static void main(String[] args) {
    	// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", PasClientApp.class.getName());
			return;
		}

		// host and port
		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
    	
		// gRPC provides a channel construct which abstracts out the underlying details like connection, connection pooling, load balancing, etc.
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
          .usePlaintext()
          .build();
 
        // The stub is the primary way for clients to interacts with the server.
        // When using auto generate stubs, the stub class will have constructors for wrapping the channel.
        PasServiceGrpc.PasServiceBlockingStub stub1 = PasServiceGrpc.newBlockingStub(channel);
 
        HelloRequest request1 = HelloRequest.newBuilder()
//      .setFirstName("John")
      .build();
        
        HelloRequest request2 = HelloRequest.getDefaultInstance();
        
        HelloResponse helloResponse = stub1.currentBoard(request1);
 
        channel.shutdown();
    }
    
    
    
}
