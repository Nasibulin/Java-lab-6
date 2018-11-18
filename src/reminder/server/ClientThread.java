package reminder.server;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientThread extends Thread {
    private Socket socket;
    private EventServer server;
    private PrintWriter writer;
    private User user;
    private List<Task> tasks;
    private Timer timer;
    private static volatile int i;
    private static int seconds = 3;

    public ClientThread(Socket socket, EventServer server) {
        this.socket = socket;
        this.server = server;
    }

    class EventTask extends TimerTask {
        private volatile int id = i;
        private int time;

        public EventTask(int id, int time) {
            this.id=id;
            this.time=time;
            i++;
        }

        public int getId() {
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        @Override
        public synchronized void run() {


            server.send("[server]:" + LocalDateTime.now() + " ReminderTask#" + id + " is completed by Java timer",
                    ClientThread.this);

            timer.cancel();
        }
    }

    public synchronized void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "[server]:New user connected: " + userName;
            server.send(serverMessage, this);

            String clientMessage;

            timer = new Timer();
            for (Task t : tasks) {
                EventTask remindTask = new EventTask(t.getTaskId(),t.getTime());
                timer.schedule(remindTask, Date.from(
                        Instant.from(LocalDateTime.now().plusSeconds(t.getTime()).atZone(ZoneId.systemDefault()))));
                serverMessage = "[server]:" + LocalDateTime.now() + " ReminderTask#" + t.getTaskId() + " was scheduled by Java timer";
                server.send(serverMessage, this);
            }

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("exit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = "[server]:" + userName + " was disconnected.";
            server.broadcast(serverMessage, this);

        } catch (ConnectException ex) {
            System.err.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error in ClientThread: " + ex.getMessage());
            //ex.printStackTrace();
        }
    }

    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected.");
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}