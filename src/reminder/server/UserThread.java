package reminder.server;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 *
 * @author www.codejava.net
 */
public class UserThread extends Thread {
	private Socket socket;
	private ChatServer server;
	private PrintWriter writer;
    public Timer timer;
    static int i;
    static int seconds = 3;

	public UserThread(Socket socket, ChatServer server) {
		this.socket = socket;
		this.server = server;
        	}

    class RemindTask extends TimerTask {
        int id=i;
        RemindTask(){
            i++;
        }
        @Override
        public void run() {
            System.out.println(LocalDateTime.now()+" ReminderTask#"+id+" is completed by Java timer");

            server.send(LocalDateTime.now()+" ReminderTask#"+id+" is completed by Java timer", UserThread.this);
            timer.cancel(); //Not necessary because we call System.exit
        }
    }

    public UserThread getServer(){
        return this;
    }


	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			writer = new PrintWriter(output, true);

			printUsers();

			String userName = reader.readLine();
			server.addUserName(userName);

			String serverMessage = "New user connected: " + userName;
			server.broadcast(serverMessage, this);

			String clientMessage;

            timer = new Timer();  //At this line a new Thread will be created
            //timer.schedule(new RemindTask(), seconds*1000); //delay in milliseconds
            RemindTask remindTask = new RemindTask();
            timer.schedule(remindTask, Date.from(
                    Instant.from(LocalDateTime.now().plusSeconds(seconds).atZone(ZoneId.systemDefault())))); //delay in seconds

            do {
				clientMessage = reader.readLine();
				serverMessage = "[" + userName + "]: " + clientMessage;
				server.broadcast(serverMessage, this);

			} while (!clientMessage.equals("exit"));

			server.removeUser(userName, this);
			socket.close();

			serverMessage = userName + " has quitted.";
			server.broadcast(serverMessage, this);

		} catch (IOException ex) {
			System.out.println("Error in UserThread: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Sends a list of online users to the newly connected user.
	 */
	void printUsers() {
		if (server.hasUsers()) {
			writer.println("Connected users: " + server.getUserNames());
		} else {
			writer.println("No other users connected");
		}
	}

	/**
	 * Sends a message to the client.
	 */
	void sendMessage(String message) {
		writer.println(message);
	}
}