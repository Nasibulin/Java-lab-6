package aphorism;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    static final int PORT = 8080;
    static final String HOST = "localhost";
    static String fserver = "";
    static String fuser = "";
    static String msg = "hello";

    public static void main(String[] args) throws IOException {

        try (
                Socket server = new Socket(HOST, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                BufferedReader inu = new BufferedReader(new InputStreamReader(System.in));
        ) {
            while ((fuser = inu.readLine()) != null) {
                out.println(msg);
                fserver = in.readLine();
                if (fuser.equalsIgnoreCase("quit"))
                    break;
                if (fuser.equalsIgnoreCase("exit"))
                    break;
                System.out.println(fserver);
            }
        }
    }
}
