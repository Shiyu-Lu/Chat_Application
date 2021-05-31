package DAL;

// used by server to Query data in database
interface ServerQueryDB {
    // make a new Client Account with name and password,name should be unique
    boolean makeAccount(String accountName,String accountPassword);

    // find name-password pair in order to login
    boolean matchAccountPassword(String accountName,String accountPassword);

    // find if certain Account exists
    boolean searchAccount(String accountName);

    // find if certain Account has any offlineMessage, used when that Account login
    OfflineMessage searchAccountOfflineMessage(String accountName);

    //
}

// used by server to Search data in Memory, for example on-line User, socket,...
interface ServerSearch{

}


