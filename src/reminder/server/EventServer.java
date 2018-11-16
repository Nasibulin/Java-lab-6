package reminder.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class EventServer implements Runnable {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    public EventServer(int port) {
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[server]:Event Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[server]:New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.err.println("[server]:Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java EventServer <port-number>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]);

        EventServer server = new EventServer(port);
        server.run();
    }

    void broadcast(String message, UserThread excludeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void send(String message, UserThread includeUser) {
        for (UserThread aUser : userThreads) {
            if (aUser == includeUser) {
                aUser.sendMessage(message);
            }
        }
    }


    void addUserName(String userName) {
        userNames.add(userName);
    }

    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("[server]:The user " + userName + " was disconnected.");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}