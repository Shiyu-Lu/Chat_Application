package model;

import java.io.Serializable;

public class GeneralMessage implements Serializable {

    public final MessageType messageType;
    public final String senderName;
    public final long sendTime;
    public final Object specificMessage;


    public GeneralMessage(MessageType messageType, String senderName, long snedTime, Object specificMessage) {
        this.messageType = messageType;
        this.senderName = senderName;
        this.sendTime = snedTime;
        this.specificMessage = specificMessage;
    }


    public enum MessageType {
        NORMAL_MESSAGE, // 普通消息
        ADD_FRIEND_REQUEST, // 加好友
        AGREED_ADD_FRIEND, // 同意成为好友
        DELETE_FRIEND_REQUEST, // 删好友
        LOGIN_REQUEST, // 登录请求
        CREATE_ACCOUNT_REQUEST, //新建账户请求
        LOGIN_SUCCESS, //登陆成功
        LOGIN_FAILED_ACCOUNT_NOT_FOUND,
        LOGIN_FAILED_PASSWORD_INCORRECT,
        CREATE_ACCOUNT_SUCCESS,
        CREATE_ACCOUNT_FAILED_ACCOUNT_EXIST,
        CREATE_ACCOUNT_FAILED_ERROR,
        ASK_FOR_FRIEND_LIST_REQUEST, // 请求得到好友列表
        REPLY_FOR_FRIEND_LIST_REQUEST, // 回复好友列表请求
        ACCOUNT_NOT_EXIST,//账户不存在，可能是发消息、加朋友等等不存在
        ASK_FOR_PARTICIPATOR_LIST_REQUEST, // 请求得到某个conversation的账户列表
        REPLY_FOR_PARTICIPATOR_LIST_REQUEST, // 回复conversation列表请求
        CREATE_CONVERSATION_REQUEST, // 申请新创一个conversation,主要是获得id
        CREATE_CONVERSATION_REPLY, // 回复创建conversation
        ASK_FOR_OFFLINE_MESSAGE, // 申请获得离线消息
        REPLY_FOR_OFFLINE_MESSAGE,
    }

}






