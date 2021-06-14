package model;

import java.io.Serializable;

public class NormalMessage implements Serializable {
    public String content;
    public long conversationID;
    public NormalMessage(String content, long conversationID){
        this.content = content;
        this.conversationID = conversationID;
    }
}