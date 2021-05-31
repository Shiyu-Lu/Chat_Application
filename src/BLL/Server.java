package BLL;

// I think the Interface below is per client basis, a thread for a client can run this interface
interface Server4ClientInterface{
    // when a client sent a connect request
    int connectRequestHandler();

    // when a client ask to add someone else as a friend
    int addFriendHandler(String friendName);

    //
    int deleteFriendHandler(String friendName);

    //
    int sendMessageToFriend(String message,String Friend);
}

public class Server {
}
