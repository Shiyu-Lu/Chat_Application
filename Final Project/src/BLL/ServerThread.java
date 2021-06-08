package BLL;
import Service.ServerThreadService;
import model.*;


public class ServerThread extends Thread  {
    public ServerThreadService serverThreadService;
    protected boolean connected;
    protected boolean logged;

    public ServerThread(ServerThreadService serverThreadService) {
        this.serverThreadService = serverThreadService;
        this.connected = true;
        this.logged = false;
        this.start();
    }

    @Override
    public void run() {
        // first login or sign in
        try{
            while(!logged){
                GeneralMessage message = serverThreadService.receiveGernerlMessageService();
                int result;
                switch (message.messageType){
                    case LOGIN_REQUEST:
                        // 收到登录相关信息
                        result = serverThreadService.loginService(message);
                        if(result==0){
                            logged = true;
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.LOGIN_SUCCESS);
                        }
                        else if(result == -1){
                            //TODO 区分密码错误或用户不存在的情况
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.LOGIN_FAILED_ACCOUNT_NOT_FOUND);
                        }
                        else if(result == -2){
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.LOGIN_FAILED_PASSWORD_INCORRECT);
                        }
                        break;
                    case CREATE_ACCOUNT_REQUEST:
                        // 收到创建账户相关信息
                        result = serverThreadService.createAccountService(message);
                        if(result==0){
                            // 创建后自动登录
                            logged = true;
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.CREATE_ACCOUNT_SUCCESS);
                        }
                        else if(result==-1){
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.CREATE_ACCOUNT_FAILED_ACCOUNT_EXIST);
                        }
                        else {
                            serverThreadService.sendBackSystemMessageService(GeneralMessage.MessageType.CREATE_ACCOUNT_FAILED_ERROR);
                        }
                }
            }
        }catch (Exception ex){

        }

        try{
            while(connected){
                GeneralMessage message = serverThreadService.receiveGernerlMessageService();
                switch(message.messageType){
                    case NORMAL_MESSAGE: //服务器收到客户端请求，要把消息发给某一个对话，即发给该对话中的所有成员
                        serverThreadService.sendMessageToConversationService(message);
                        break;
                    case ADD_FRIEND_REQUEST: //服务器收到客户端添加好友请求，把该消息发给被添加好友的客户端
                        serverThreadService.addFriendService(message);
                        break;
                    case AGREED_ADD_FRIEND: //服务器收到客户端同意成为好友请求，通知最初的申请方
                        serverThreadService.agreedAddFriendService(message);
                        break;
                    case DELETE_FRIEND_REQUEST: // 服务器收到客户端删好友请求，删除服务器数据库中FriendList表的相关内容
                        serverThreadService.deleteFriendService(message);
                        break;
                    case ASK_FOR_FRIEND_LIST_REQUEST:
                        serverThreadService.getFriendListService();
                        break;
                    case ASK_FOR_PARTICIPATOR_LIST_REQUEST: // 服务端收到客户端请求，查询某个conversation有哪些account
                        serverThreadService.getConversationParticipatorListService(message);
                        break;
                    case CREATE_CONVERSATION_REQUEST: // 服务器收到客户端请求，分配一个新的conversation ID
                        serverThreadService.createConversationService(message);
                        break;
                    case ASK_FOR_OFFLINE_MESSAGE:
                        serverThreadService.getOfflineMessage();
                        break;
                    default:
                        //TODO
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        shutDown();
    }

    public void shutDown() {
        connected = false;
        logged = false;
        serverThreadService.shutDownService();
    }
}




