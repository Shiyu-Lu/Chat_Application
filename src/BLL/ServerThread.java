package BLL;
import java.io.*;
import java.net.*;
import java.util.*;

// I think the Interface below is per client basis, a thread for a client can run this interface
interface Server4ClientInterface{

    // when a client ask to add someone else as a friend
    int addFriendHandler(String friendName);

    // delete an existing friend
    int deleteFriendHandler(String friendName);

    //
    int sendMessageToFriend(String message,String Friend);

    // send some message made by server
    int sendBackSystemMessage(String message);

    boolean login(String account, String password);

    boolean createAccount(String account, String password);

    Message receiveMessage();

    void shutDown();
}


public class ServerThread extends Thread implements  Server4ClientInterface {
    protected Socket socket;
    protected ChatServer server;
    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    protected boolean connected;
    protected boolean logged;
    private String userName;

    public ServerThread(Socket socket,ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.connected = true;
        this.logged = false;
        this.userName = null;
        try {
            input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // TODO: let the caller know failure and show some hints to the client(on the Frame)
            try {
                socket.close();
                connected = false;
            } catch (IOException e2) {
                ;
            }
            e.printStackTrace();
            return;
        }
        this.start();
    }

    @Override
    public void run() {
        // first login or sign in
        try{
            while(!logged){
                Message message = receiveMessage();
                switch (message.messageType){
                    case LOGIN_REQUEST:
                        // 收到登录相关信息
                        LoginRequest m = (LoginRequest) message.content;
                        if(login(m.account,m.password)){
                            logged = true;
                            server.serverThreads.put(m.account,this);
                            userName = m.account;
                            sendBackSystemMessage("Login successful!");
                        }
                        else{
                            //TODO 区分密码错误或用户不存在的情况
                            sendBackSystemMessage("Login failed!");
                        }
                        break;
                    case DELETE_FRIEND_REQUEST:
                        // 收到创建账户相关信息
                        CreateAccountRequest m2 = (CreateAccountRequest) message.content;
                        if(createAccount(m2.account,m2.password)){
                            // 创建后自动登录
                            logged = true;
                            server.serverThreads.put(m2.account,this);
                            userName = m2.account;
                            sendBackSystemMessage("Create account successful!");
                        }
                        else{
                            sendBackSystemMessage("Create account failed!");
                        }
                }
            }
        }catch (Exception ex){

        }

        try{
            while(connected){
                Message message = receiveMessage();
                switch(message.messageType){

                    case NORMAL_MESSAGE:
                        NormalMessage m = (NormalMessage) message.content;
                        sendMessageToFriend(m.content,m.receiverName);
                        break;
                    case ADD_FRIEND_REQUEST:
                        String addFriendName = (String) message.content;
                        addFriendHandler(addFriendName);
                        break;
                    case DELETE_FRIEND_REQUEST:
                        String deleteFriendName = (String) message.content;
                        deleteFriendHandler(deleteFriendName);
                        break;
                    default:
                        //TODO
                }
            }
        } catch (Exception ex){

        }
    }

    @Override
    public boolean login(String account, String password) {
        return false;
    }

    @Override
    public boolean createAccount(String account, String password) {
        return false;
    }

    @Override
    public int addFriendHandler(String friendName) {
        return 0;
    }

    @Override
    public int deleteFriendHandler(String friendName) {
        return 0;
    }

    @Override
    public int sendMessageToFriend(String message, String Friend) {
        return 0;
    }

    @Override
    public int sendBackSystemMessage(String message) {
        return 0;
    }

    @Override
    public Message receiveMessage() {
        try {
            Message message = (Message) input.readObject();
            return message;
        } catch (IOException ex){

        } catch (ClassNotFoundException ex2){

        }
        return null;
    }

    @Override
    public void shutDown() {

    }
}




