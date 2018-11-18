package reminder.client;

import java.net.*;
import java.io.*;

public class EventClient implements Runnable{
	private String hostname;
	private int port;
	private int userId;

	public EventClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}


    public EventClient(String hostname, int port, int userId) {
		this.hostname = hostname;
		this.port = port;
        this.userId = userId;
	}

	public void run() {
		try {
			Socket socket = new Socket(hostname, port);

			System.out.println("["+userId+"]:"+"Connected to the event server");

			new ReadThread(socket, this).start();
			new WriteThread(socket, this).start();

		} catch (UnknownHostException ex) {
			System.err.println("Server not found: " + ex.getMessage());
		} catch (IOException ex) {
			System.err.println("I/O Error: " + ex.getMessage());
			//ex.printStackTrace();
		}

	}

	void setUserId(int userId) {
		this.userId = userId;
	}

	int getUserId() {
		return this.userId;
	}

}