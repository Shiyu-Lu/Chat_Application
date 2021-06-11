package DAL;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

public class ConversationMessage implements Comparable{
    private String senderName;
    private long conersationID; // the consersation that this message belongs to, thus no need for receiver name
    private String content; // 发送内容
    private long sendTime;

    public long getSendTime() {
        return sendTime;
    }

    public String getContent() {
        return content;
    }

    public long getConersationID() {
        return conersationID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setConersationID(long conersationID) {
        this.conersationID = conersationID;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public int compareTo(Object o) {
        ConversationMessage others = (ConversationMessage) o;
        if(others.sendTime != this.sendTime ){
            return (int) (this.sendTime- others.sendTime);
        }
        if(others.equals(this)){
            return 0;
        }
        return senderName.compareTo(others.senderName);
    }

    public Map<String,Object> convertToMap(int messageIndex){
        Map<String,Object> map = new HashMap<>();
        map.put("sender",senderName);
        map.put("msgID",messageIndex);
        map.put("msgContent",content);
        map.put("timestamp",sendTime);
        return map;
    }
}

