package model;

import java.io.Serializable;

public class FriendConversationRecord implements Serializable {
    private String friendName;
    private long conversationId;

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public long getConversationId() {
        return conversationId;
    }

    public String getFriendName() {
        return friendName;
    }
}
