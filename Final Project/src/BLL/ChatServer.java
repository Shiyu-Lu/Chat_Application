package BLL;

import DAL.RawAccountRecordDAO;
import DAL.ConversationAccountDAO;
import DAL.ConversationServerDAO;
import Service.ServerThreadService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer implements Runnable {
    public static final String SYSTEM_NAME = "CHAT_CHAT_SYSTEM"; // 发送系统消息时，发送者的名字。普通用户名不能取这个名字
    public Map<String,ServerThreadService> serverThreadServices = new ConcurrentHashMap<>();
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
                ServerThread t = new ServerThread(
                        new ServerThreadService(this,clientSocket));// after login is verified, the ServerThreadService  would put itself in Map
                System.out.println("One Client Comes in");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        ChatServer chatServer = new ChatServer();
        ChatHttpServer chatHttpServer = new ChatHttpServer();
    }

}
