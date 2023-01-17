import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));

        try (ServerSocket serverSocket = new ServerSocket(9095);) { // стартуем сервер один(!) раз
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    // обработка одного подключения
                    System.out.println("Start");

                    String stringFromSocket = in.readLine();
                    System.out.println(stringFromSocket);

                    Gson gson = new Gson();
                    //String json = gson.toJson(stringFromSocket);
                    List<PageEntry> entries = engine.search(stringFromSocket);
                    out.println(gson.toJson(entries));

                } catch (IOException e) {
                    System.out.println("Не могу стартовать сервер");
                    e.printStackTrace();
                }
            }
        }
    }
}
