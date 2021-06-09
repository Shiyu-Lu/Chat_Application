package DAL;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**This class is used for a conversation in ****Client****.
 * It could be a private conversation or a group talk.*/
public class Conversation implements Comparable {
    public static final int PRIVATE_CONVERSATION = 0;
    public static final int GROUP_CONVERSATION = 1;
    public  long id;
    private int type;
    private String name; // 对话的名字
    private Set<String> participatorList;
    private SortedSet<ConversationMessage> messageList;

    public Conversation(){
        participatorList = new HashSet<>();
        messageList = new TreeSet<>();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setParticipatorList(Set<String> participatorList) {
        this.participatorList = participatorList;
    }

    public void setMessageList(SortedSet<ConversationMessage> messageList) {
        this.messageList = messageList;
    }


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Set<String> getParticipatorList() {
        return participatorList;
    }

    public SortedSet<ConversationMessage> getMessageList() {
        return messageList;
    }

    public boolean addParticipator(String name){
        return participatorList.add(name);
    }

    public boolean removeParticipator(String name){
        return participatorList.remove(name);
    }

    public boolean hasParticipator(String name){
        return participatorList.contains(name);
    }

    public boolean addMessage(ConversationMessage message){
        return messageList.add(message);
    }

    public boolean remove(ConversationMessage message){
        return messageList.remove(message);
    }

    @Override
    public int compareTo(Object o) {
        long myLastSendTime,otherLastSendTime;
        if(messageList.isEmpty())
            myLastSendTime = 0;
        else
            myLastSendTime = messageList.last().getSendTime();
        if(((Conversation)o).messageList.isEmpty())
            otherLastSendTime = 0;
        else
            otherLastSendTime = ((Conversation)o).messageList.last().getSendTime();


        if(myLastSendTime!=otherLastSendTime){
            return (int) (otherLastSendTime - myLastSendTime);
        }
        else{
            return (int) (id-((Conversation) o).id);
        }
    }
}
