package BLL;

interface ClientInterface{
    // log in
    void login();

    // log out
    void logout();

    // add new friend
    boolean addFriend(String friendName);

    // delete friend
    boolean deleteFriend(String friendName);

    // send message to a friend
    int sendMessageToFriend(String message,String friendName);

    int recevieMessageFromFriend();
}

public class Client {
}
