package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ClientDBConnection {
    static private ClientDBConnection instance = null;
    private Connection conn = null;
    private final String userName;
    public Connection getConnection() {
        return conn;
    }

    private ClientDBConnection(String userName)
    {
        this.userName = userName;
        connect();
    }
    private void connect() {
        try {
            // db parameters
            String url = "jdbc:sqlite:d:/ChatClient_"+userName+".db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Client Connection to SQLite has been established.");
            conn.setAutoCommit(true);

            // create table first
            String sqlCreateTableConversations = "create table if not exists Conversations( id integer  , conversationName varchar(30))";
            conn.createStatement().execute(sqlCreateTableConversations);
            String sqlCreateTableConversationMessage = "create table if not exists ConversationMessage( conversationID integer , senderName varchar(30)," +
                    " message nvarchar(200),sendTime integer)";
            conn.createStatement().execute(sqlCreateTableConversationMessage);


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }

    public static ClientDBConnection getInstance(String userName) {
        if (instance == null) {
            instance = new ClientDBConnection(userName);
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


}