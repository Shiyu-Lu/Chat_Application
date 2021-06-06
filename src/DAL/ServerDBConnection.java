package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServerDBConnection {
    static private ServerDBConnection instance = null;
    private  Connection conn = null;

    public Connection getConnection() {
        return conn;
    }

    private ServerDBConnection(){
        connect();
    }
    private void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:d:/ChatServer.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Server Connection to SQLite has been established.");
            conn.setAutoCommit(true);

            // create table first
            String sqlCreateTableAccounts = "create table if not exists Accounts( name varchar(30) PRIMARY KEY, password varchar(30) )";
            conn.createStatement().execute(sqlCreateTableAccounts);
            String sqlCreateTableConversations = "create table if not exists Conversations( id integer PRIMARY KEY AUTOINCREMENT , conversationName varchar(30))";
            conn.createStatement().execute(sqlCreateTableConversations);
            String sqlCreateTableConversationAccounts = "create table if not exists ConversationAccounts( id integer , accountName varchar(30),FOREIGN KEY(id) REFERENCES Conversations(id),FOREIGN KEY(accountName) REFERENCES Accounts(name))";
            conn.createStatement().execute(sqlCreateTableConversationAccounts);
            String sqlCreateTableOfflineMessage = "create table if not exists OfflineMessage( receiverName varchar(30) , senderName varchar(30)," +
                    " conversationID integer ,message nvarchar(200),sendTime integer,messageType interger,FOREIGN KEY(receiverName) REFERENCES Accounts(name),FOREIGN KEY(senderName) REFERENCES Accounts(name))";
            conn.createStatement().execute(sqlCreateTableOfflineMessage);
            String sqlCreateTableAccountFriends = "create table if not exists AccountFriends( name varchar(30) , friendName varchar(30), id integer,FOREIGN KEY(id) REFERENCES Conversations(id),FOREIGN KEY(name) REFERENCES Accounts(name),FOREIGN KEY(friendName) REFERENCES Accounts(name))";
            conn.createStatement().execute(sqlCreateTableAccountFriends);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static ServerDBConnection getInstance() {
        if (instance == null) {
            instance = new ServerDBConnection();
        }
        return instance;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerDBConnection.getInstance().getConnection();
    }
}