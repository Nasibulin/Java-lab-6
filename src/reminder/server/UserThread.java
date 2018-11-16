package reminder.server;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UserThread extends Thread {
    private Socket socket;
    private EventServer server;
    private PrintWriter writer;
    public Timer timer;
    static volatile int i;
    static int seconds = 3;

    public UserThread(Socket socket, EventServer server) {
        this.socket = socket;
        this.server = server;
    }

    class EventTask extends TimerTask {
        volatile int id = i;

        EventTask() {
            i++;
        }

        public int getId(){
            return id;
        }

        @Override
        public synchronized void run() {
            //System.out.println(LocalDateTime.now()+" ReminderTask#"+id+" is completed by Java timer");
            server.send("[server]:" + LocalDateTime.now() + " ReminderTask#" + id + " is completed by Java timer",
                        UserThread.this);
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
            EventTask remindTask = new EventTask();
            timer.schedule(remindTask, Date.from(
                    Instant.from(LocalDateTime.now().plusSeconds(seconds).atZone(ZoneId.systemDefault()))));
            serverMessage = "[server]:" + LocalDateTime.now() + " ReminderTask#" + remindTask.getId() + " was scheduled by Java timer";
            server.send(serverMessage, this);

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("exit"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = "[server]:" + userName + " was disconnected.";
            server.broadcast(serverMessage, this);

        }
        catch(ConnectException ex) {
            System.err.println("Socket error: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.err.println("Error in UserThread: " + ex.getMessage());
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