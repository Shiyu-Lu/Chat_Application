package BLL;

import DAL.ConversationMessage;
import Service.ChatClientForUserService;
import model.*;

import java.util.Map;
import java.util.Set;


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
        if(result==0){
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
        if(result==0){
            //创建账户成功
            isLoggedIn = true;
            chatClientForServer = new ChatClientForServer(chatClientForUserService.input,this);
            chatClientForUserService.initAccount(password);
            chatClientForUserService.offLineMessageService();
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


    public int deleteFriendHandler(String friendName) {
        return chatClientForUserService.deleteFriendService(friendName);
    }

    public void sendMessageToConversationHandler(String message, long conversationID) {
        chatClientForUserService.sendMessageToConversationService(message,conversationID);
        //TODO:

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



