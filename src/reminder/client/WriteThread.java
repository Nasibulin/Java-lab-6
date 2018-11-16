package reminder.client;

import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private EventClient client;
    private String userName;

	public WriteThread(Socket socket, EventClient client) {
		this.socket = socket;
		this.client = client;
        this.userName = client.getUserName();
		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.err.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

        client.setUserName(userName);
		writer.println(userName);

		String text;
        try (BufferedReader inu =
                     new BufferedReader(new InputStreamReader(System.in))){
            while ((text = inu.readLine())!=null) {
                System.out.print("\n[" + userName + "]: ");
                writer.println(text);
                if (text.equalsIgnoreCase("exit"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		try {
			socket.close();
		} catch (IOException ex) {
            System.err.println("Error writing to server: " + ex.getMessage());
		}
	}
}