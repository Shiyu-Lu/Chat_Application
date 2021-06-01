package BLL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer implements Runnable {
    protected Map<String,ServerThread> serverThreads = new ConcurrentHashMap<>();
    public final static int DEFAULT_PORT = 6543;
    protected ServerSocket listenSocket;
    Thread thread;

    ChatServer(){
        try {
            ServerListen();
        }catch (Exception e){

        }

    }


    public void ServerListen() {
        try {
            listenSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server: listening on port " + DEFAULT_PORT);
            thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Socket clientSocket = listenSocket.accept();
                ServerThread t = new ServerThread(clientSocket, this);// after login is verified, the ServerThread  would put itself in Map
                System.out.println("One Client Comes in");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
