package BLL;

import Service.ChatClientForServerService;
import model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

// use this thread to listen to message from server, only after successful login!
public class ChatClientForServer extends  Thread{
    protected ChatClientForUser chatClientForUser;
    protected ChatClientForServerService chatClientForServerService;
    protected boolean connected;


    public ChatClientForServer(ObjectInputStream input, ChatClientForUser chatClientForUser) {
        try {
            chatClientForServerService = new ChatClientForServerService(input,chatClientForUser);
            this.connected = true;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            while(connected){
                GeneralMessage message = chatClientForServerService.receiveGeneralMessageService();
                switch (message.messageType){
                    // 收到一条别人发给我的消息
                    case NORMAL_MESSAGE:
                        chatClientForServerService.receiveMessageFromFriendService(message);
                        break;
                    // 收到别人的好友邀请
                    case ADD_FRIEND_REQUEST:
                        chatClientForServerService.receiveFriendInvitationService(message);
                        break;
                    case REPLY_FOR_FRIEND_LIST_REQUEST:
                        chatClientForServerService.receiveFriendListReply(message);
                        break;
                    case ACCOUNT_NOT_EXIST:
                        chatClientForServerService.receiveErrorMessage("Account not exists!");
                        break;
                    case REPLY_FOR_PARTICIPATOR_LIST_REQUEST:
                        chatClientForServerService.receiveParticipatorListReply(message);
                        break;
                    case AGREED_ADD_FRIEND:
                        chatClientForServerService.agreedAddFriend(message);
                        break;
                    case CREATE_CONVERSATION_REPLY:
                        chatClientForServerService.receiveCreateConversationReply(message);
                        break;
                    case REPLY_FOR_OFFLINE_MESSAGE:
                        chatClientForServerService.receiveOfflineMessageReply(message);
                        break;
                    default:
                        //TODO
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
