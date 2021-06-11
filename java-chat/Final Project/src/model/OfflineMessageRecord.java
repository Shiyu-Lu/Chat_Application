package model;

import java.io.Serializable;

public class OfflineMessageRecord implements Serializable {
    private String receiverName;
    private String senderName;
    private long conversationID;
    private String message;
    private long sendTime;
    private int messageType;

    public static final int NORMAL_MESSAGE = 1;
    public static final int ADD_FRIEND_REQUEST = 2;
    public static final int AGREED_ADD_FRIEND = 3;



    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getConversationID() {
        return conversationID;
    }

    public void setConversationID(long conversationID) {
        this.conversationID = conversationID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public static OfflineMessageRecord GeneralMessageToOfflineMessage(GeneralMessage generalMessage,String receiverName){
        OfflineMessageRecord offlineMessageRecord = new OfflineMessageRecord();
        offlineMessageRecord.setReceiverName(receiverName);
        offlineMessageRecord.setSenderName(generalMessage.senderName);
        offlineMessageRecord.setConversationID(0);
        offlineMessageRecord.setSendTime(generalMessage.sendTime);
        switch (generalMessage.messageType){
            case NORMAL_MESSAGE:
                NormalMessage normalMessage = (NormalMessage) generalMessage.specificMessage;
                offlineMessageRecord.setConversationID(normalMessage.conversationID);
                offlineMessageRecord.setMessage(normalMessage.content);
                offlineMessageRecord.setMessageType(NORMAL_MESSAGE);
                break;
            case ADD_FRIEND_REQUEST:
                offlineMessageRecord.setMessageType(ADD_FRIEND_REQUEST);
                break;
            case AGREED_ADD_FRIEND:
                offlineMessageRecord.setMessageType(AGREED_ADD_FRIEND);
                break;
        }
        return offlineMessageRecord;
    }

    public static GeneralMessage OfflineMessageToGeneralMessage(OfflineMessageRecord offlineMessageRecord) {
        String senderName = offlineMessageRecord.senderName;
        long sendTime = offlineMessageRecord.sendTime;
        int messageType = offlineMessageRecord.getMessageType();
        switch (messageType) {
            case NORMAL_MESSAGE:
                NormalMessage normalMessage = new NormalMessage(offlineMessageRecord.message, offlineMessageRecord.conversationID);
                GeneralMessage generalMessage = new GeneralMessage(GeneralMessage.MessageType.NORMAL_MESSAGE, senderName, sendTime, normalMessage);
                return generalMessage;
            case ADD_FRIEND_REQUEST:
                GeneralMessage generalMessage2 = new GeneralMessage(GeneralMessage.MessageType.ADD_FRIEND_REQUEST, senderName, sendTime, offlineMessageRecord.receiverName);
                return generalMessage2;
            case AGREED_ADD_FRIEND:
                FriendConversationRecord friendConversationRecord = new FriendConversationRecord();
                friendConversationRecord.setFriendName(offlineMessageRecord.receiverName);
                friendConversationRecord.setConversationId(offlineMessageRecord.conversationID);
                GeneralMessage generalMessage3 = new GeneralMessage(GeneralMessage.MessageType.AGREED_ADD_FRIEND, senderName, sendTime, friendConversationRecord);
                return generalMessage3;
            default:
                return null;
        }
    }
}
