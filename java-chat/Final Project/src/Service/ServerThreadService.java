package Service;


import BLL.ChatServer;
import DAL.*;
import model.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerThreadService {
    final private ChatServer server;
    final private Socket socket;
    final private ObjectInputStream input;
    final private ObjectOutputStream output;
    final private ConversationServerDAO conversationServerDAO; // 查询conversation 获得ID
    final private RawAccountRecordDAO rawAccountRecordDAO; // 查询账号密码
    final private ConversationAccountDAO conversationAccountDAO; // 查询conversation中有哪些Account（用户）
    final private AccountFriendDAO accountFriendDAO; // 初始登陆时查询账户有哪些好友
    final private OfflineMessageDAO offlineMessageDAO; // 查询离线消息
    private  String userName = null;
    private ExecutorService outputs;


    public ServerThreadService(ChatServer server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.conversationServerDAO = new ConversationServerDAO();
        this.rawAccountRecordDAO = new RawAccountRecordDAO();
        this.conversationAccountDAO = new ConversationAccountDAO();
        this.accountFriendDAO = new AccountFriendDAO();
        this.offlineMessageDAO = new OfflineMessageDAO();
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        outputs = Executors.newCachedThreadPool();

    }

    public GeneralMessage receiveGernerlMessageService() {
        try {
            GeneralMessage message = (GeneralMessage) input.readObject();
            return message;
        } catch (IOException ex){

        } catch (ClassNotFoundException ex2){

        }
        return null;
    }

    synchronized public void sendGernerlMessage(GeneralMessage m){
        try {
            output.writeObject(m);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSpecificMessage(GeneralMessage.MessageType messageType, Object content){
        GeneralMessage m = new GeneralMessage(messageType,userName, new Date().getTime(),content);
        sendGernerlMessage(m);
    }

    public void sendMessageToFriendService(GeneralMessage message, String Friend) {
        outputs.submit(()->{
            ServerThreadService target = server.serverThreadServices.get(Friend);
            if(target!=null){
                target.sendGernerlMessage(message);
                return;
            }//TODO offline
            // 首先查询是否有该账户
            List<RawAccountRecord> rawAccountRecords = rawAccountRecordDAO.queryByAccountName(Friend);
            if(rawAccountRecords.isEmpty()){
                sendBackSystemMessageService(GeneralMessage.MessageType.ACCOUNT_NOT_EXIST); // 发一个提示回去
                return ;
            }
            // 离线情况处理
            OfflineMessageRecord offlineMessageRecord = OfflineMessageRecord.GeneralMessageToOfflineMessage(message,Friend);
            offlineMessageDAO.insert(offlineMessageRecord);
            return;

        });
    }

    public void sendMessageToConversationService(GeneralMessage message) {
        // find the Account in this conversation
        long conversationID = ((NormalMessage)(message.specificMessage)).conversationID;
        Set<String> friends = new ConversationAccountDAO().queryAccountNameById(conversationID);
        for(String friend:friends){
        	if (friend.equals(userName)) continue;
            sendMessageToFriendService(message,friend);
        }

    }

    /**
     *
     * @param message
     * @return 0 for login succcess; -1 for account not found; -2 for password incorrect
     */
    public int loginService(GeneralMessage message) {
        LoginRequestMessage m = (LoginRequestMessage) message.specificMessage;
        String accountName = m.account;
        String password = m.password;
        //TODO
        List<RawAccountRecord> result = rawAccountRecordDAO.queryByAccountName(accountName);
        if(result.isEmpty())
            return -1;//账号不存在
        else if(!result.get(0).getPassword().equals(password))
            return -2;
        this.userName = accountName;
        server.serverThreadServices.put(userName,this);
        return 0;
    }

    public int sendBackSystemMessageService(GeneralMessage.MessageType systemMessageType) {
        sendSpecificMessage(systemMessageType,"useless");
        return 0;
    }

    /**
     *
     * @param message
     * @return 0 if successs create account; return -1 already has account; return -2 other sql error
     */
    public int createAccountService(GeneralMessage message)
    {
        CreateAccountRequestMessage m = (CreateAccountRequestMessage) message.specificMessage;
        String accountName = m.account;
        String password = m.password;
        List<RawAccountRecord> result = rawAccountRecordDAO.queryByAccountName(accountName);
        if(!result.isEmpty())
            return -1;
        boolean insertResult = rawAccountRecordDAO.insert(new RawAccountRecord(accountName,password));
        if(!insertResult){
            return -2;
        }
        this.userName = accountName;
        server.serverThreadServices.put(userName,this);
        return 0;
    }

    public int getFriendListService(){
        outputs.submit(()->{
            Set<FriendConversationRecord> friendList = accountFriendDAO.queryFriendNameByAcccountName(userName);
            sendSpecificMessage(GeneralMessage.MessageType.REPLY_FOR_FRIEND_LIST_REQUEST,friendList);
        });

        return 0;
    }

    public void getConversationParticipatorListService(GeneralMessage message){
        outputs.submit(()->{
           Set<String> participatorList = conversationAccountDAO.queryAccountNameById((long)message.specificMessage);
           sendSpecificMessage(GeneralMessage.MessageType.REPLY_FOR_PARTICIPATOR_LIST_REQUEST,participatorList);
        });

    }

    public int addFriendService(GeneralMessage message) {
        String friendName = (String)message.specificMessage;
        sendMessageToFriendService(message,friendName);
        return 0;
    }

    public int deleteFriendService(GeneralMessage message) {
        FriendConversationRecord friendConversationRecord = (FriendConversationRecord) message.specificMessage;
        String friendName = friendConversationRecord.getFriendName();
        long conversationID = friendConversationRecord.getConversationId();
        // 互相删好友
        accountFriendDAO.delete(userName,friendName);
        accountFriendDAO.delete(friendName,userName);
        // 删除整个对话（因为一定是私聊）
        conversationAccountDAO.deleteById(conversationID);
        conversationServerDAO.deleteById(conversationID);
        // 删除未读的离线消息
        offlineMessageDAO.deleteByConversationId(conversationID);
        return 0;
    }

    public void shutDownService(){
        try {
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(userName!=null) {
            server.serverThreadServices.remove(userName);
        }
    }

    public void agreedAddFriendService(GeneralMessage message) {
        FriendConversationRecord friendConversationRecord = (FriendConversationRecord) message.specificMessage;
        sendMessageToFriendService(message,friendConversationRecord.getFriendName());
        // 更新数据库
        accountFriendDAO.insert(userName,friendConversationRecord.getFriendName(),friendConversationRecord.getConversationId());
        accountFriendDAO.insert(friendConversationRecord.getFriendName(),userName,friendConversationRecord.getConversationId());
        conversationAccountDAO.insert(friendConversationRecord.getConversationId(),friendConversationRecord.getFriendName());
    }

    public void createConversationService(GeneralMessage message) {
        outputs.submit(()->{
            RawConversationRecord rawConversationRecord = (RawConversationRecord) message.specificMessage;
            long conversationId = conversationServerDAO.insert(rawConversationRecord);
            if(conversationId == 0){
                // 说明插入失败
                rawConversationRecord = null;
            }
            else{
                rawConversationRecord.setId(conversationId);
                conversationAccountDAO.insert(conversationId,userName);//新成立的conversation至少有创建人自己
            }
            sendSpecificMessage(GeneralMessage.MessageType.CREATE_CONVERSATION_REPLY,rawConversationRecord);
        });



    }

    public void getOfflineMessage() {
        outputs.submit(()->{
            List<OfflineMessageRecord> offlineMessageRecordList = offlineMessageDAO.queryByReceiverName(userName);
            offlineMessageDAO.deleteByReceiverName(userName);//TODO
            sendSpecificMessage(GeneralMessage.MessageType.REPLY_FOR_OFFLINE_MESSAGE,offlineMessageRecordList);
        });
    }
}
