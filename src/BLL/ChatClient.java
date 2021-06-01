package BLL;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface ClientInterface{
    // log in
    int login(String account,String password);

    // log out
    void logout();

    int createAccount(String account,String password);

    // add new friend
    boolean addFriend(String friendName);

    // delete friend
    boolean deleteFriend(String friendName);

    // send message to a friend
    int sendMessageToFriend(String message,String friendName);

}

// ChatClient 主线程用于监听用户
public class ChatClient implements ClientInterface{
    protected String userName;
    public final static int DEFAULT_PORT = 6543;
    protected boolean isLoggedIn;
    protected ClientReadThread readThread;
    protected ExecutorService outputs;
    protected Socket socket;

    ChatClient(){
        userName = null;
        outputs = Executors.newCachedThreadPool();
    }

    public void startConnect(){
        try {
            socket = new Socket("127.0.0.1", DEFAULT_PORT);
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    public int login(String account,String password){
        if(isLoggedIn)
            return -1;
        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Message m = new Message(MessageType.LOGIN_REQUEST,(Object) new LoginRequest(account,password));
            out.writeObject((Object) m);
            out.flush();
            m = (Message) in.readObject();
            if(m.messageType == MessageType.LOGIN_SUCCESS){
                isLoggedIn = true;
                userName = account;
                readThread = new ClientReadThread(socket,this);
                return 0;
            }
            else if(m.messageType == MessageType.LOGIN_FAILED_ACCOUNT_NOT_FOUND)
                return -2;
            else if(m.messageType == MessageType.LOGIN_FAILED_PASSWORD_INCORRECT)
                return -3;
            else
                return -4;
        } catch (IOException  ex) {

        } catch (ClassNotFoundException ex2){

        }
        return -4;
    }

    @Override
    public void logout() {

    }

    public int createAccount(String account,String password){
        if(isLoggedIn)
            return -1;
        try {
            ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Message m = new Message(MessageType.CREATE_ACCOUNT_REQUEST,(Object) new CreateAccountRequest(account,password));
            out.writeObject((Object) m);
            out.flush();
            m = (Message) in.readObject();
            if(m.messageType == MessageType.CREATE_ACCOUNT_SUCCESS){
                isLoggedIn = true;
                userName = account;
                readThread = new ClientReadThread(socket,this);
                return 0;
            }
            else if(m.messageType == MessageType.CREATE_ACCOUNT_FAILED_ACCOUNT_EXIST)
                return -2;
            else
                return -4;
        } catch (IOException  ex) {

        } catch (ClassNotFoundException ex2){

        }
        return -4;
    }

    @Override
    public boolean addFriend(String friendName) {
        return false;
    }

    @Override
    public boolean deleteFriend(String friendName) {
        return false;
    }

    @Override
    public int sendMessageToFriend(String message, String friendName) {
        return 0;
    }


}

public class ChatClient2 {
    private String hostname;
    private int port;
    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            System.out.println("Connected to the chat server");

            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
}

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);

                // prints the username after displaying the server's message
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Console console = System.console();

        String userName = console.readLine("\nEnter your name: ");
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            text = console.readLine("[" + userName + "]: ");
            writer.println(text);

        } while (!text.equals("bye"));

        try {
            socket.close();
        } catch (IOException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}
