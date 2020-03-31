package pt.ulisboa.tecnico.meic.sec.pas.server;

import java.io.IOException;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class PasServerApp {
	 public static void main(String[] args) throws IOException, InterruptedException {

    // receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", PasServerApp.class.getName());
			return;
		}

    // parse port
    int port = Integer.parseInt(args[0]);

    BindableService service1 = new PasServiceImpl();

    // bind this server to a port and to any grpc services you want
    Server server = ServerBuilder
      .forPort(port)
      .addService(service1)
      .build();

    // start server
    server.start();
    System.out.println("Server started on port " + port);
    server.awaitTermination();
  }
}

