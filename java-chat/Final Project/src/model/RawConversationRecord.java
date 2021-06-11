package model;

import java.io.Serializable;

public class RawConversationRecord implements Serializable {
    private long id;
    private String conversationName;

    public void setId(long id) {
        this.id = id;
    }

    public void setConversationName(String converSationname) {
        this.conversationName = converSationname;
    }

    public long getId() {
        return id;
    }

    public String getConversationName() {
        return conversationName;
    }
}
