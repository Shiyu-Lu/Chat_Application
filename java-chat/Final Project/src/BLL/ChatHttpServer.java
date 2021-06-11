package BLL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import DAL.Conversation;
import DAL.ConversationMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.RawAccountRecord;

//import sun.net.www.protocol.http.HttpURLConnection;

public class ChatHttpServer {

    Map<String,ChatClientForUser> userMap;

    public ChatHttpServer() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8443), 0);
        userMap = new ConcurrentHashMap<>();
        server.createContext("/api/login", new LoginHandler()); // Done
        server.createContext("/api/signup", new SignUpHandler()); // Done
        server.createContext("/api/searchcontact", new searchFriendHandler()); // Done
        server.createContext("/api/singlemsgstream", new getConversationMessageHandler());
        server.createContext("/api/addfriend", new addFriendHandler()); // Partly Done
        server.createContext("/api/delContact", new deleteFriendHandler()); // Done
        server.createContext("/api/sendmsg",new sendConversationMessageHandler()); // Done
        server.createContext("/api/msglist",new getConversationListHandler()); // Done
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();
    }

    public static void httpAccessControl(HttpExchange t) throws IOException {
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
            t.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
            t.sendResponseHeaders(204, -1);
            return;
        }
    }

    public static void sendJSON(HttpExchange t,String response) throws IOException {
        Headers responseHeaders = t.getResponseHeaders();
        // 以json形式返回，其他还有text/html等等
        responseHeaders.set("Content-Type", "application/json");
        // 设置响应码200和响应body长度，这里我们设0，没有响应体
        t.sendResponseHeaders(200, 0);
        // 获取响应体
        OutputStream responseBody = t.getResponseBody();
        // 获取请求头并打印
        Headers requestHeaders = t.getRequestHeaders();
        Set<String> keySet = requestHeaders.keySet();
        Iterator<String> iter = keySet.iterator();
        String strRequestHeaders = "";
        while (iter.hasNext()) {
            String key = iter.next();
            List values = requestHeaders.get(key);
            strRequestHeaders = key + " = " + values.toString() + "\r\n";
        }
//        System.out.println(strRequestHeaders);
        // 关闭输出流
//        System.out.println(response);
        responseBody.write(response.getBytes());
        responseBody.close();
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(new Result(-4)));
    }

    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                RawAccountRecord rawAccountRecord = JSON.parseObject(s,RawAccountRecord.class);
                String userName = rawAccountRecord.getName();
                String password = rawAccountRecord.getPassword();
                ChatClientForUser chatClientForUserTest = new ChatClientForUser();
                chatClientForUserTest.startConnectHandler();
                int r = chatClientForUserTest.loginHandler(userName,password);
                if(r==200){
                    userMap.put(userName,chatClientForUserTest);
                }
                String jsonOutput= JSON.toJSONString(new Result(r));
                sendJSON(t,jsonOutput);
            }

        }
    }

    class SignUpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                RawAccountRecord rawAccountRecord = JSON.parseObject(s,RawAccountRecord.class);
                String userName = rawAccountRecord.getName();
                String password = rawAccountRecord.getPassword();
                ChatClientForUser chatClientForUserTest = new ChatClientForUser();
                chatClientForUserTest.startConnectHandler();
                int r = chatClientForUserTest.createAccountHandler(userName,password);
                if(r==200){
                    userMap.put(userName,chatClientForUserTest);
                }
                String jsonOutput= JSON.toJSONString(new Result(r));
                sendJSON(t,jsonOutput);
            }

        }
    }

    class searchFriendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                FriendUserMessage friendUserMessage = JSON.parseObject(s,FriendUserMessage.class);
                String userName = friendUserMessage.getOwnerUsername();
                String friendName = friendUserMessage.getSearchUsername();
                ChatClientForUser chatClientForUserTest = userMap.get(userName);
                int r = chatClientForUserTest.searchFriendHandler(friendName);
                String jsonOutput= JSON.toJSONString(new Result(r));
                sendJSON(t,jsonOutput);
            }

        }
    }

    class getConversationMessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                System.out.println(s);
                FriendUserMessage friendUserMessage = JSON.parseObject(s,FriendUserMessage.class);
                String userName = friendUserMessage.getOwnerUsername();
                String friendName = friendUserMessage.getSearchUsername();
                ChatClientForUser chatClientForUserTest = userMap.get(userName);
                List<ConversationMessage> r = chatClientForUserTest.getConversationMessageHandler(friendName);
                List<ConversationMessageForJson> conversationMessageForJsonList = new ArrayList<>();
                for(int i=0;i<r.size();i++){
                    conversationMessageForJsonList.add(new ConversationMessageForJson(r.get(i),i));
                }
                String jsonOutput= JSON.toJSONString(conversationMessageForJsonList);
                System.out.println(jsonOutput);
                sendJSON(t,jsonOutput);
            }

        }
    }

    class getConversationListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
//            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                UsernameMessage usernameMessage = JSON.parseObject(s,UsernameMessage.class);
//                System.out.println(s);
                String userName = usernameMessage.getOwnerUsername();
                ChatClientForUser chatClientForUserTest = userMap.get(userName);
                List<Conversation> r = chatClientForUserTest.getConversationListHandler();
                r.sort(Comparator.naturalOrder());
                List<ConversationLastMessageForJson> conversationMessageForJsonList = new ArrayList<>();
//                System.out.println(r.size());
                for(int i=0;i<r.size();i++){
                    Conversation conversation = r.get(i);
                    ConversationLastMessageForJson recentMessage = new ConversationLastMessageForJson();
                    if(conversation.getMessageList().isEmpty()){
                        recentMessage.setLastMsgContent("");
                    }
                    else{
                        recentMessage.setLastMsgContent(conversation.getMessageList().last().getContent());
                    }
                    recentMessage.setIndex(i);
                    if(conversation.getParticipatorList().size()==2){
                        for(String name:conversation.getParticipatorList()){
                            if(!name.equals(userName)){
                                recentMessage.setConvUserName(name);
                                break;
                            }
                        }
                    }
                    else{
                        recentMessage.setConvUserName(conversation.getName());
                    }
                    conversationMessageForJsonList.add(recentMessage);
                }
                String jsonOutput= JSON.toJSONString(conversationMessageForJsonList);
//                System.out.println(jsonOutput);
                sendJSON(t,jsonOutput);
            }

        }
    }

    class sendConversationMessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                System.out.println(s);
                SendMessage sendMessage = JSON.parseObject(s,SendMessage.class);
                String userName = sendMessage.getOwnerUsername();
                String friendName = sendMessage.getSearchUsername();
                String content = sendMessage.getMsgContent();
                ChatClientForUser chatClientForUserTest = userMap.get(userName);
                // 发消息
                chatClientForUserTest.sendMessageToFriendHandler(content,friendName);
                // 等服务器传回消息
//                try {
//                    Thread.sleep(500L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                // 复用getConversationMessageHandler 获得消息
//                List<ConversationMessage> r = chatClientForUserTest.getConversationMessageHandler(friendName);
//                List<ConversationMessageForJson> conversationMessageForJsonList = new ArrayList<>();
//                for(int i=0;i<r.size();i++){
//                    conversationMessageForJsonList.add(new ConversationMessageForJson(r.get(i),i));
//                }
//                String jsonOutput= JSON.toJSONString(conversationMessageForJsonList);
                String jsonOutput= JSON.toJSONString(new Result(200));
                System.out.println(jsonOutput);
                sendJSON(t,jsonOutput);
            }

        }
    }

    class addFriendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                FriendUserMessage friendUserMessage = JSON.parseObject(s,FriendUserMessage.class);
                String userName = friendUserMessage.getOwnerUsername();
                String friendName = friendUserMessage.getSearchUsername();
                ChatClientForUser chatClientForUserTest = userMap.get(userName);
                int r = chatClientForUserTest.addFriendForceHandler(friendName);
                String jsonOutput= JSON.toJSONString(new Result(r));
                System.out.println(jsonOutput);
                sendJSON(t,jsonOutput);
            }

        }
    }

    class deleteFriendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            /* --------- NEW --------- */
            httpAccessControl(t);

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
	            // 打印输入流
	            InputStream requestBody = t.getRequestBody();
	            String s;
	            try (requestBody) {
	                int n;
	                StringBuilder sb = new StringBuilder();
	                while ((n = requestBody.read()) != -1) {
	                    sb.append((char) n);
	                }
	                s = sb.toString();
	            }
//	            System.out.println(s);
	            FriendUserMessage friendUserMessage = JSON.parseObject(s,FriendUserMessage.class);
	            String userName = friendUserMessage.getOwnerUsername();
	            String friendName = friendUserMessage.getSearchUsername();
	            ChatClientForUser chatClientForUserTest = userMap.get(userName);
	            int r = chatClientForUserTest.deleteFriendHandler(friendName);
	            String jsonOutput= JSON.toJSONString(new Result(r));
	            System.out.println(jsonOutput);
	            sendJSON(t,jsonOutput);
	        }

        }
    }





}



class Result{
    @JSONField(name="code")
    public int code;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public Result(int code){
        this.code = code;
    }
}

class ConversationMessageForJson{
    @JSONField(name="sender")
    private String sender;

    @JSONField(name="msgID")
    private int msgID;

    @JSONField(name="msgContent")
    private String msgContent;

    @JSONField(name="timeStamp")
    private String timeStamp;
    ConversationMessageForJson(){}

    ConversationMessageForJson(ConversationMessage raw,int index){
        sender= raw.getSenderName();
        msgID = index;
        msgContent = raw.getContent();
        timeStamp = new Date(raw.getSendTime()).toString();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}

class ConversationLastMessageForJson{
    @JSONField(name="convUserName")
    private String convUserName;

    @JSONField(name="lastMsgContent")
    private String lastMsgContent;

    @JSONField(name="index")
    private int index;

    public String getConvUserName() {
        return convUserName;
    }

    public void setConvUserName(String convUserName) {
        this.convUserName = convUserName;
    }

    public String getLastMsgContent() {
        return lastMsgContent;
    }

    public void setLastMsgContent(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

class FriendUserMessage{
    @JSONField(name="ownerUsername")
    private String ownerUsername;

    @JSONField(name="searchUsername")
    private String searchUsername;

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getSearchUsername() {
        return searchUsername;
    }

    public void setSearchUsername(String searchUsername) {
        this.searchUsername = searchUsername;
    }
}

class UsernameMessage{
    @JSONField(name="ownerUsername")
    private String ownerUsername;

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

}

class SendMessage{
    @JSONField(name="ownerUsername")
    private String ownerUsername;

    @JSONField(name="searchUsername")
    private String searchUsername;

    @JSONField(name="msgContent")
    private String msgContent;

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getSearchUsername() {
        return searchUsername;
    }

    public void setSearchUsername(String searchUsername) {
        this.searchUsername = searchUsername;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }


}