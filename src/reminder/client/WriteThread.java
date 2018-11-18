package reminder.client;

import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private EventClient client;
    private int userId;

	public WriteThread(Socket socket, EventClient client) {
		this.socket = socket;
		this.client = client;
        this.userId = client.getUserId();
		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.err.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

        client.setUserId(userId);
		writer.println(userId);

		String text;
        try (BufferedReader inu =
                     new BufferedReader(new InputStreamReader(System.in))){
            while ((text = inu.readLine())!=null) {
                System.out.print("\n[" + userId + "]: ");
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