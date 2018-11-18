package reminder.server;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ClientThread extends Thread {
    private Socket socket;
    private EventServer server;
    private PrintWriter writer;
    private HashMap<Integer, User> idToUser;
    private Timer timer;

    public ClientThread(Socket socket, EventServer server, HashMap<Integer, User> idToUser) {
        this.socket = socket;
        this.server = server;
        this.idToUser = idToUser;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            int userId = Integer.parseInt(reader.readLine());
            server.addUserId(userId);

            String serverMessage = "[server]:New user connected: " + idToUser.get(userId);
            server.send(serverMessage, this);

            String clientMessage;

            timer = new Timer();
            for (Task t : idToUser.get(userId).getTasks()) {
                EventTask remindTask = new EventTask(t.getTaskId(), t.getTime());
                timer.schedule(remindTask, Date.from(
                        Instant.from(LocalDateTime.now().plusSeconds(t.getTime()).atZone(ZoneId.systemDefault()))));
                serverMessage = "["+idToUser.get(userId)+"]:" + LocalDateTime.now() + " " +
                        idToUser.get(userId).toString() + " Task#" +
                        t.getTaskId() + " " + "\"" + t.getDescription() + "\"" + " was scheduled by Java timer";
                server.send(serverMessage, this);
            }

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userId + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals("exit"));

            server.removeUser(userId, this);
            socket.close();

            serverMessage = "[server]:" + userId + " was disconnected.";
            server.broadcast(serverMessage, this);

        } catch (ConnectException ex) {
            System.err.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error in ClientThread: " + ex.getMessage());
            //ex.printStackTrace();
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }

    class EventTask extends TimerTask {
        private int id;
        private int time;

        public EventTask(int id, int time) {
            this.id = id;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            server.send("[server]:" + LocalDateTime.now() + " Task#" + id + " is completed by Java timer",
                    ClientThread.this);
        }
    }
}