package reminder.client;

import java.net.*;
import java.io.*;

public class EventClient implements Runnable{
	private String hostname;
	private int port;
	private volatile String userName;

	public EventClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
    public EventClient(String hostname, int port, String userName) {
		this.hostname = hostname;
		this.port = port;
        this.userName = userName;
	}

	public void run() {
		try {
			Socket socket = new Socket(hostname, port);

			System.out.println("["+userName+"]:"+"Connected to the event server");

			new ReadThread(socket, this).start();
			new WriteThread(socket, this).start();

		} catch (UnknownHostException ex) {
			System.err.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.err.println("I/O Error: " + ex.getMessage());
			//ex.printStackTrace();
		}

	}

	void setUserName(String userName) {
		this.userName = userName;
	}

	String getUserName() {
		return this.userName;
	}


	public static void main(String[] args) {
		if (args.length < 2) return;

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);

		EventClient client = new EventClient(hostname, port);
		client.run();
	}
}