package DAL;

import model.FriendConversationRecord;
import model.RawConversationRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client use this class for itself.
 * */

public class Account {
    private String userName;
    private String password;
    private Map<String,Long> friendList = new ConcurrentHashMap<>();
    private Map<Long,Conversation> conversationList = new ConcurrentHashMap<>();


    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setConversationList(Map<Long, Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public void setFriendList(Map<String, Long> friendList) {
        this.friendList = friendList;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public Map<Long, Conversation> getConversationList() {
        return conversationList;
    }

    public Map<String, Long> getFriendList() {
        return friendList;
    }

    public boolean hasFriend(String friendName){

        return friendList.containsKey(friendName);
    }

    public boolean hasConversation(long conversationId){
        return conversationList.containsKey(conversationId);
    }

    public void addFriend(FriendConversationRecord friendConversationRecord){

        friendList.put(friendConversationRecord.getFriendName(),friendConversationRecord.getConversationId());
    }

    public void deleteFriend(String friendName){
        friendList.remove(friendName);
    }

    public void addConversation(Conversation conversation){
        conversationList.put(conversation.id,conversation);
    }

    public void deleteConversationById(long conversationID){
        conversationList.remove(conversationID);
    }
}
