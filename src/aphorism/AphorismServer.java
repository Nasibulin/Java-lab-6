package aphorism;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AphorismServer {
    static final int PORT = 8080;
    private final static String QUOTE_FILE = "db/quotes.txt";
    private static List<String> quote = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        loadQuotesFile();
        loadQuotesArray();

        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Мультипоточный сервер стартовал");
        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    System.out.println("Новое соединение установлено");
                    new ServerOne(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }

    private static void loadQuotesFile() {
        Forismatic.Quote quote;
        try (Writer file = new FileWriter(QUOTE_FILE)) {
            for (int i = 0; i < 100; i++) {
                quote = new Forismatic(Forismatic.RUSSIAN).getQuote();
                try {
                    file.write(quote.getQuoteText() + "\u00A9 " + quote.getQuoteAuthor() + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadQuotesArray() {
        File file = new File(QUOTE_FILE);
        try (FileReader reader = new FileReader(file);
             BufferedReader breader = new BufferedReader(reader)) {
            String line;
            while ((line = breader.readLine()) != null) {
                quote.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ServerOne extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String str = "";

        public ServerOne(Socket s) throws IOException {
            socket = s;
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
            start();
        }

        public void run() {
            try {
                while ((str = in.readLine()) != null) {
//              str = in.readLine();
                    if (str.equals("quit"))
                        break;
                    if (str.equals("exit"))
                        break;
                    System.out.println("Получено: " + str);
                    Collections.shuffle(quote);
                    out.println(quote.get(0));
                }
                System.out.println("Соединение закрыто");
            } catch (IOException e) {
                System.err.println("Ошибка чтения/записи");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Сокет не закрыт");
                }
            }
        }
    }


}
