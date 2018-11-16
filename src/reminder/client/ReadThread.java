package reminder.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

public class ReadThread extends Thread {
	private BufferedReader reader;
	private Socket socket;
	private EventClient client;
    private String userName;

	public ReadThread(Socket socket, EventClient client) {
		this.socket = socket;
		this.client = client;
        this.userName = client.getUserName();

		try {
			InputStream input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
        } catch(ConnectException ex) {
            System.err.println("Socket error: " + ex.getMessage());
        }
        catch (IOException ex) {
			System.err.println("Error getting input stream: " + ex.getMessage());
			//ex.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				String response = reader.readLine();
				System.out.println("\n" + response);

				if (client.getUserName() != null) {
					//System.out.print("\n[" + client.getUserName() + "]: ");
				}
			} catch(ConnectException ex) {
                System.err.println("Socket error: " + ex.getMessage());
            }
            catch (IOException ex) {
//				System.out.println("Error reading from server: " + ex.getMessage());
//				ex.printStackTrace();
                System.err.println("Connection close.");
                break;
			}
		}
	}
}