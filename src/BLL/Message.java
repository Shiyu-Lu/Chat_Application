package BLL;

import java.io.Serializable;

public class Message implements Serializable {

    public final MessageType messageType;
    Object content;

    public Message(MessageType messageType,Object content) {
        this.messageType = messageType;
        this.content = content;
    }
}

class NormalMessage implements Serializable{
    public String content;
    public String receiverName;
    NormalMessage(String content,String receiverName){
        this.content = content;
        this.receiverName = receiverName;
    }
}

class LoginRequest implements  Serializable{
    public String account;
    public String password;

    public LoginRequest(String account,String password) {
        this.account = account;
        this.password = password;
    }
}

class CreateAccountRequest implements  Serializable{
    public String account;
    public String password;

    public CreateAccountRequest(String account,String password) {
        this.account = account;
        this.password = password;
    }
}
