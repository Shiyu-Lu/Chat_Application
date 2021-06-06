package Service;

import BLL.ChatClientForUser;
import DAL.Conversation;
import DAL.ConversationMessage;
import model.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ChatClientForServerService {
    //final private Socket socket;
    final private ObjectInputStream input;
    final private ChatClientForUser chatClientForUser; // 用于返回显示某些消息


    public ChatClientForServerService(ObjectInputStream input, ChatClientForUser chatClientForUser) throws IOException {
        //this.socket = socket;
        this.input = input;
        this.chatClientForUser = chatClientForUser;
        chatClientForUser.chatClientForUserService.chatClientForServerService = this;
    }

    public GeneralMessage receiveGeneralMessageService() {
        try {
            GeneralMessage message = (GeneralMessage) input.readObject();
            return message;
        } catch (IOException ex){

        } catch (ClassNotFoundException ex2){

        }
        return null;
    }

    public void receiveFriendListReply(GeneralMessage message){
        chatClientForUser.chatClientForUserService.friendListRequestResult = (Set<FriendConversationRecord>)message.specificMessage;
        chatClientForUser.chatClientForUserService.friendListRequestLatch.countDown();//唤醒
    }

    /**
     * 收到任何一种Error Message，最终返回给UI层显示
     * @param message
     */
    public void receiveErrorMessage(String message){
        //TODO
        chatClientForUser.showSystemMessage(message);
    }

    public void receiveMessageFromFriendService(GeneralMessage message) {
        NormalMessage normalMessage = (NormalMessage) message.specificMessage;
        ConversationMessage conversationMessage = new ConversationMessage();
        conversationMessage.setSenderName(message.senderName);
        conversationMessage.setContent(normalMessage.content);
        conversationMessage.setSendTime(message.sendTime);
        conversationMessage.setConersationID(normalMessage.conversationID);
        chatClientForUser.chatClientForUserService.account.getConversationList().get(normalMessage.conversationID).addMessage(conversationMessage);
        chatClientForUser.chatClientForUserService.localMessageDAO.insert(conversationMessage);
        chatClientForUser.receiveConversationMessage(conversationMessage);
    }

    public void receiveFriendInvitationService(GeneralMessage message) {
        //TODO
        chatClientForUser.receiveFriendInvitation(message);
    }

    public void receiveParticipatorListReply(GeneralMessage message) {
        chatClientForUser.chatClientForUserService.conversationParticipatorListRequestResult = (Set<String>) message.specificMessage;
        chatClientForUser.chatClientForUserService.conversationParticipatorListRequestLatch.countDown();
    }

    public void agreedAddFriend(GeneralMessage message) {
        String friendName = message.senderName;
        String userName = chatClientForUser.chatClientForUserService.userName;
        FriendConversationRecord friendConversationRecord = (FriendConversationRecord) message.specificMessage;
        friendConversationRecord.setFriendName(friendName);
        chatClientForUser.chatClientForUserService.account.addFriend(friendConversationRecord);
        Conversation conversation = new Conversation();
        conversation.setId(friendConversationRecord.getConversationId());
        conversation.setName(userName+friendName);
        conversation.addParticipator(userName);
        conversation.addParticipator(friendName);
        chatClientForUser.chatClientForUserService.conversationClientDAO.insert(conversation.getId(),conversation.getName());
        chatClientForUser.chatClientForUserService.account.addConversation(conversation);
        System.out.println("New friend "+friendName+" ! And the conversation id is"+friendConversationRecord.getConversationId());
    }

    public void receiveCreateConversationReply(GeneralMessage message) {
        RawConversationRecord rawConversationRecord = (RawConversationRecord) message.specificMessage;
        chatClientForUser.chatClientForUserService.createConversationRequestResult = rawConversationRecord;
        chatClientForUser.chatClientForUserService.createConversationRequestLatch.countDown();
    }

    public void receiveOfflineMessageReply(GeneralMessage message) {
        List<OfflineMessageRecord> offlineMessageRecordList = (List<OfflineMessageRecord>) message.specificMessage;
        chatClientForUser.chatClientForUserService.offlineMessageRecordListRequestResult = offlineMessageRecordList;
        chatClientForUser.chatClientForUserService.offlineMessageRecordLatch.countDown();
    }
}
