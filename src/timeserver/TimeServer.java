package timeserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

class Server1 implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Server1(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
        out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);
        run();
    }

    public void run() {
        try {
            while (in.readLine() != null) {
                System.err.println("Получен запрос времени от : " + (socket.getInetAddress()).getHostName());
                out.println(LocalDateTime.now());
            }
            System.err.println("Соединение закрыто");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class TimeServer {
    static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("Мультипоточный сервер стартовал");
        try (ServerSocket s = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = s.accept();
                try {
                    System.out.println("Новое соединение установлено");
                    new Thread(new Server1(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }
}
