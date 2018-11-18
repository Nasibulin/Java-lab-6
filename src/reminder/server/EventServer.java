package reminder.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventServer implements Runnable {
    private static HashMap<Integer, User> idToUser = new HashMap<Integer, User>();
    private int port;
    private Set<Integer> userIds = new HashSet<>();
    private Set<ClientThread> clientThreads = new HashSet<>();


    public EventServer(int port, HashMap<Integer, User> idToUser) {
        this.port = port;
        this.idToUser = idToUser;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("[server]:Event Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[server]:New user connected");

                ClientThread newClient = new ClientThread(socket, this, idToUser);
                clientThreads.add(newClient);
                newClient.start();

            }

        } catch (IOException ex) {
            System.err.println("[server]:Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void broadcast(String message, ClientThread excludeUser) {
        for (ClientThread aUser : clientThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void send(String message, ClientThread includeUser) {
        for (ClientThread aUser : clientThreads) {
            if (aUser == includeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    void addUserId(int userId) {
        userIds.add(userId);
    }

    void removeUser(int userId, ClientThread aUser) {
        boolean removed = userIds.remove(userId);
        if (removed) {
            clientThreads.remove(aUser);
            System.out.println("[server]:The user " + userId + " was disconnected.");
        }
    }

    Set<Integer> getUserIds() {
        return this.userIds;
    }

    boolean hasUsers() {
        return !this.userIds.isEmpty();
    }
}