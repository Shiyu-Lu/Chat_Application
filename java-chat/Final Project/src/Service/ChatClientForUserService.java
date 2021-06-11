package Service;

import DAL.*;
import model.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClientForUserService {

    private ExecutorService outputs;
    private ObjectOutputStream output;
    public ObjectInputStream input;
    private Socket socket;
    public String userName;
    public Account account; // 内存中的当前用户信息
    public final static int DEFAULT_PORT = 6543;
    public ConversationClientDAO conversationClientDAO;
    public LocalMessageDAO localMessageDAO;
    public ChatClientForServerService chatClientForServerService;

    Set<FriendConversationRecord> friendListRequestResult;
    CountDownLatch friendListRequestLatch;

    Set<String> conversationParticipatorListRequestResult;
    CountDownLatch conversationParticipatorListRequestLatch;

    RawConversationRecord createConversationRequestResult;
    CountDownLatch createConversationRequestLatch;

    List<OfflineMessageRecord> offlineMessageRecordListRequestResult;
    CountDownLatch offlineMessageRecordLatch;

    public ChatClientForUserService(){
        userName = null;
        outputs = Executors.newCachedThreadPool();

        account = new Account();
    }

    public void startConnectService(){
        try {
            socket = new Socket("127.0.0.1", DEFAULT_PORT);
            input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loginService(String account,String password){
        try {
            sendSpecificMessageToServer(GeneralMessage.MessageType.LOGIN_REQUEST,(Object) new LoginRequestMessage(account,password));
            GeneralMessage m = (GeneralMessage) input.readObject();
            if(m.messageType == GeneralMessage.MessageType.LOGIN_SUCCESS){

                userName = account;
                conversationClientDAO = new ConversationClientDAO(userName);
                localMessageDAO = new LocalMessageDAO(userName);
                System.out.println(userName+"login!");
                return 200;
            }
            else if(m.messageType == GeneralMessage.MessageType.LOGIN_FAILED_ACCOUNT_NOT_FOUND){
                return -2;
            }
            else if(m.messageType == GeneralMessage.MessageType.LOGIN_FAILED_PASSWORD_INCORRECT){
                return -3;
            }
            else {
                return -4;
            }
        } catch (IOException  ex) {

        } catch (ClassNotFoundException ex2){

        }
        return -4;
    }

    public void logoutService(){
        outputs.shutdown();
        try {
            input.close();
            output.close();
            socket.close();
            userName = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int createAccountService(String account,String password){
        try {
            sendSpecificMessageToServer(GeneralMessage.MessageType.CREATE_ACCOUNT_REQUEST,(Object) new CreateAccountRequestMessage(account,password));
            GeneralMessage m = (GeneralMessage) input.readObject();
            if(m.messageType == GeneralMessage.MessageType.CREATE_ACCOUNT_SUCCESS){
                userName = account;
                conversationClientDAO = new ConversationClientDAO(userName);
                localMessageDAO = new LocalMessageDAO(userName);
                System.out.println(userName+" create an Account!");
                return 200;
            }
            else if(m.messageType == GeneralMessage.MessageType.CREATE_ACCOUNT_FAILED_ACCOUNT_EXIST) {
                //in.close();
                return 300;
            }
            else{
                //in.close();
                return -2;
            }
        } catch (IOException  ex) {

        } catch (ClassNotFoundException ex2){

        }

        return -2;
    }

    // 发出请求，等待服务器的响应，在ChatClientForServerService中唤醒
    public Set<FriendConversationRecord> askFriendListService(){
        friendListRequestResult = null;
        friendListRequestLatch = new CountDownLatch(1);
        sendSpecificMessageToServer(GeneralMessage.MessageType.ASK_FOR_FRIEND_LIST_REQUEST,"useless");
        try {
            friendListRequestLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return friendListRequestResult;
    }

    public void addFriendService(String friendName){
        if(account.hasFriend(friendName)){
            //已经是好友了
            return;
        }
        sendSpecificMessageToServer(GeneralMessage.MessageType.ADD_FRIEND_REQUEST,friendName);
    }

    public void agreeAddFriendService(String friendName){
        // 申请一个conversationID
        RawConversationRecord rawConversationRecord = createConversationService(friendName+"+"+userName); // 默认conversationName = friendName+userName
        FriendConversationRecord friendConversationRecord = new FriendConversationRecord();
        friendConversationRecord.setFriendName(friendName);
        friendConversationRecord.setConversationId(rawConversationRecord.getId());
        //添加到自己的好友列表
        account.addFriend(friendConversationRecord);
        // 把好友添加到对话的参与名单中，之前申请对话ID时自己已经在名单中了
        account.getConversationList().get(rawConversationRecord.getId()).addParticipator(friendName);
        //通知该好友我们已经是好友了
        sendSpecificMessageToServer(GeneralMessage.MessageType.AGREED_ADD_FRIEND,friendConversationRecord);
    }

    public int deleteFriendService(String friendName){
        //删好友列表
        long conversationID = account.getFriendList().get(friendName);
        FriendConversationRecord friendConversationRecord = new FriendConversationRecord();
        friendConversationRecord.setFriendName(friendName);
        friendConversationRecord.setConversationId(conversationID);
        sendSpecificMessageToServer(GeneralMessage.MessageType.DELETE_FRIEND_REQUEST,friendConversationRecord); // TODO 这里简化处理了 没有等服务器返回结果
        account.deleteFriend(friendName);
        account.deleteConversationById(conversationID);
        // 删除本地数据库
        localMessageDAO.deleteById(conversationID);
        conversationClientDAO.deleteById(conversationID);

        return 200;
    }

    public void sendMessageToConversationService(String message, long conversationID) {
        if(!account.hasConversation(conversationID)){
            System.out.println("you are not in conversation: "+conversationID);
            //TODO
            return;
        }
        sendSpecificMessageToServer(GeneralMessage.MessageType.NORMAL_MESSAGE,(Object) new NormalMessage(message,conversationID));
        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setSenderName(userName);
        conversationMessage.setContent(message);
        conversationMessage.setSendTime(new Date().getTime());
        conversationMessage.setConersationID(conversationID);
        account.getConversationList().get(conversationID).addMessage(conversationMessage);
        localMessageDAO.insert(conversationMessage);
    }


    synchronized public void sendGernerlMessageToServer(GeneralMessage m){
        try {
            output.writeObject(m);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSpecificMessageToServer(GeneralMessage.MessageType messageType, Object specificMessage){
        GeneralMessage m = new GeneralMessage(messageType,this.userName, new Date().getTime(),specificMessage);
        sendGernerlMessageToServer(m);
    }

    public void initAccount(String password){
        account.setUserName(userName);
        account.setPassword(password);
        Set<FriendConversationRecord> friendList = askFriendListService();
        for(FriendConversationRecord friendConversationRecord:friendList){
            account.addFriend(friendConversationRecord);
        }
        //获取所有conversation
        List<RawConversationRecord> rawConversationRecords = conversationClientDAO.queryAll();
        for(RawConversationRecord rawConversationRecord:rawConversationRecords){
            Conversation conversation = new Conversation();
            conversation.setId(rawConversationRecord.getId());
            conversation.setName(rawConversationRecord.getConversationName());
            Set<String> participatorList = askConversationParticipatorListService(conversation.getId()); // 服务器端查找
            if(participatorList.equals(null) || participatorList.size() == 0){
                // 没有参与者了 说明该对话已被删除 很有可能就是被删好友了
                // 本地也删除这个对话
                conversationClientDAO.deleteById(conversation.getId());
                localMessageDAO.deleteById(conversation.getId());
                continue;
            }
            conversation.setParticipatorList(participatorList);
            SortedSet<ConversationMessage> messageList = localMessageDAO.queryByID(conversation.getId()); // 本地查找
            conversation.setMessageList(messageList);
            account.addConversation(conversation);
        }
    }

    private Set<String> askConversationParticipatorListService(long conversationID) {
        conversationParticipatorListRequestResult = null;
        conversationParticipatorListRequestLatch = new CountDownLatch(1);
        sendSpecificMessageToServer(GeneralMessage.MessageType.ASK_FOR_PARTICIPATOR_LIST_REQUEST,conversationID);
        try {
            conversationParticipatorListRequestLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return conversationParticipatorListRequestResult;
    }

    public void offLineMessageService(){
        List<OfflineMessageRecord> offlineMessageRecordList = askOfflineMessageRecordService();
        for(OfflineMessageRecord offlineMessageRecord:offlineMessageRecordList){
            GeneralMessage generalMessage = OfflineMessageRecord.OfflineMessageToGeneralMessage(offlineMessageRecord);
            switch (generalMessage.messageType){
                case NORMAL_MESSAGE:
                    chatClientForServerService.receiveMessageFromFriendService(generalMessage);
                    break;
                case ADD_FRIEND_REQUEST:
                    chatClientForServerService.receiveFriendInvitationService(generalMessage);
                    break;
                case AGREED_ADD_FRIEND:
                    chatClientForServerService.agreedAddFriend(generalMessage);
                    break;
            }
        }
    }

    private List<OfflineMessageRecord> askOfflineMessageRecordService(){
        offlineMessageRecordListRequestResult = null;
        offlineMessageRecordLatch = new CountDownLatch(1);
        sendSpecificMessageToServer(GeneralMessage.MessageType.ASK_FOR_OFFLINE_MESSAGE,"useless");
        try {
            offlineMessageRecordLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return offlineMessageRecordListRequestResult;
    }

    public RawConversationRecord createConversationService(String conversationName){
        createConversationRequestResult = null;
        createConversationRequestLatch = new CountDownLatch(1);
        RawConversationRecord rawConversationRecord = new RawConversationRecord();
        rawConversationRecord.setConversationName(conversationName);
        sendSpecificMessageToServer(GeneralMessage.MessageType.CREATE_CONVERSATION_REQUEST,rawConversationRecord);
        try {
            createConversationRequestLatch.await();;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (createConversationRequestResult.equals(null)) {
            return null;
        }
        conversationClientDAO.insert(createConversationRequestResult);

        Conversation newConversation = new Conversation();
        newConversation.setName(conversationName);
        newConversation.setId(createConversationRequestResult.getId());
        newConversation.addParticipator(userName);
        account.addConversation(newConversation);
        return createConversationRequestResult;
    }


    public void sendMessageToFriendService(String message, String friendName) {
        if(!account.hasFriend(friendName)){
            System.out.println("you don't have this friend!");
            return;
        }
        long conversationID = account.getFriendList().get(friendName);
        sendMessageToConversationService(message,conversationID);
    }

    public List<Conversation> getConversationList() {
        Map<Long,Conversation> map = account.getConversationList();
        List<Conversation> result = new LinkedList<>(map.values());
        return result;
    }
}