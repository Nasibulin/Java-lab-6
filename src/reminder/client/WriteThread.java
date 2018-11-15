package reminder.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * This thread is responsible for reading user's input and send it
 * to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 *
 * @author www.codejava.net
 */
public class WriteThread extends Thread {
	private PrintWriter writer;
	private Socket socket;
	private ChatClient client;

	public WriteThread(Socket socket, ChatClient client) {
		this.socket = socket;
		this.client = client;

		try {
			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);
		} catch (IOException ex) {
			System.out.println("Error getting output stream: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void run() {

		//Console console = System.console();

		//String userName = console.readLine("\nEnter your name: ");

        System.out.print("Enter your name: ");
        String userName;
        Scanner in = new Scanner(System.in);
        userName=in.nextLine();
        System.out.print("[" + userName + "]: ");

        client.setUserName(userName);
		writer.println(userName);

		String text;
        try (BufferedReader inu =
                     new BufferedReader(new InputStreamReader(System.in))){
            while ((text = inu.readLine())!=null) {
                System.out.print("[" + userName + "]: ");
                writer.println(text);
                if (text.equalsIgnoreCase("exit"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//		do {
//		text = console.readLine("[" + userName + "]: ");
//			writer.println(text);
//
//		} while (!text.equals("bye"));

		try {
			socket.close();
		} catch (IOException ex) {

			System.out.println("Error writing to server: " + ex.getMessage());
		}
	}
}