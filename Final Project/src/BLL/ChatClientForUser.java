package BLL;

import DAL.ConversationMessage;
import Service.ChatClientForUserService;
import model.*;

import java.util.*;


// ChatClient 主线程用于监听用户
public class ChatClientForUser {
    protected boolean isLoggedIn;
    protected ChatClientForServer chatClientForServer;
    public ChatClientForUserService chatClientForUserService;


    ChatClientForUser(){
        isLoggedIn = false;
        chatClientForUserService = new ChatClientForUserService();
    }

    public void startConnectHandler(){
        chatClientForUserService.startConnectService();
    }

    public int loginHandler(String account,String password){
        if(isLoggedIn)
            return -1;
        int result = chatClientForUserService.loginService(account,password);
        if(result==200){
           //登录成功
           isLoggedIn = true;
           chatClientForServer = new ChatClientForServer(chatClientForUserService.input,this);
           chatClientForUserService.initAccount(password);
           chatClientForUserService.offLineMessageService();
        }
        return result;
    }

    public void logoutHandler() {
        chatClientForUserService.logoutService();
        isLoggedIn = false;
    }

    public int createAccountHandler(String account,String password){
        if(isLoggedIn)
            return -1;
        int result = chatClientForUserService.createAccountService(account,password);
        if(result==200){
            //创建账户成功
            isLoggedIn = true;
            chatClientForServer = new ChatClientForServer(chatClientForUserService.input,this);
            chatClientForUserService.initAccount(password);
            chatClientForUserService.offLineMessageService();
        }
        return result;
    }

    public int searchFriendHandler(String friendName){
        if(chatClientForUserService.account.hasFriend(friendName)){
            return 200;
        }
        else{
            return -1;
        }
    }

    public List<ConversationMessage> getConversationMessageHandler(String friendName){
        if(!chatClientForUserService.account.hasFriend(friendName)){
            return null;
        }
        long conversationID = chatClientForUserService.account.getFriendList().get(friendName);
        SortedSet<ConversationMessage> messageList = chatClientForUserService.account.getConversationList().get(conversationID).getMessageList();
        List<ConversationMessage> result = new ArrayList<>();
        Iterator iterator = messageList.iterator();
        int id = 1;
        while(iterator.hasNext()){
            ConversationMessage msg = (ConversationMessage) iterator.next();
            result.add(msg);
        }
        return result;
    }

    public Map<String,Long> askFriendListHandler(){
        return chatClientForUserService.account.getFriendList();
    }

    /**
     * 主动发送申请好友请求
     * @param friendName
     * @return
     */
    public void addFriendHandler(String friendName) {
        chatClientForUserService.addFriendService(friendName);
    }

    public void agreeAddFriendHandler(String friendName){
        chatClientForUserService.agreeAddFriendService(friendName);
    }

    public int addFriendForceHandler(String friendName){
        // 强行添加好友
        agreeAddFriendHandler(friendName);
        return 200;
    }

    public int deleteFriendHandler(String friendName) {
        return chatClientForUserService.deleteFriendService(friendName);
    }

    public void sendMessageToConversationHandler(String message, long conversationID) {
        chatClientForUserService.sendMessageToConversationService(message,conversationID);
        //TODO:

    }

    public void sendMessageToFriendHandler(String message,String friendName){
        chatClientForUserService.sendMessageToFriendService(message,friendName);
    }

    public RawConversationRecord createConversationHandler(String conversationName){
        return chatClientForUserService.createConversationService(conversationName);
    }

    public void receiveFriendInvitation(GeneralMessage message){
        //TODO
        System.out.println("receive a friend invitation from "+message.senderName);
    }

    public void receiveConversationMessage(ConversationMessage message) {
        //TODO
        System.out.printf("Conversation %d %s said : %s\n",message.getConersationID(),message.getSenderName(),message.getContent());
    }

    public void showSystemMessage(String message){
        //TODO
        System.out.println(message);
    }


    /**
     * Test!
     * @param args
     */
    public static void main(String[] args) {
        if(args[0].equals("1")){
            System.out.println("Client 1 Test!!");
            ChatClientForUser chatClientForUserTest = new ChatClientForUser();
            chatClientForUserTest.startConnectHandler();
            chatClientForUserTest.loginHandler("H","123");
//            chatClientForUserTest.createAccountHandler("H","123");
//            try {
//                Thread.sleep(1000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            chatClientForUserTest.addFriendHandler("L");
//            try {
//                Thread.sleep(3000L);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            chatClientForUserTest.sendMessageToConversationHandler("Hello22! ",1);
        }
        else{
            System.out.println("Client 2 Test!!");
            ChatClientForUser chatClientForUserTest = new ChatClientForUser();
            chatClientForUserTest.startConnectHandler();
            chatClientForUserTest.loginHandler("L","123");
            //chatClientForUserTest.createAccountHandler("L","123");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //chatClientForUserTest.agreeAddFriendHandler("H");
            chatClientForUserTest.sendMessageToConversationHandler("Hello11! ",1);
        }

        //while(true){}

    }



}



