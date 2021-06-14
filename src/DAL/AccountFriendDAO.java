package DAL;

import model.FriendConversationRecord;
import model.RawConversationRecord;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * name varchar(30) , friendName varchar(30), id integer
 */

public class AccountFriendDAO {
    private Connection conn;
    static private String tableName = "AccountFriends";

    public AccountFriendDAO(){
        conn = ServerDBConnection.getInstance().getConnection();
    }

    public Set<FriendConversationRecord> queryFriendNameByAcccountName(String accountName) {

        return queryFriendNames(" where name = '"+accountName+"' ");
    }

    public Set<FriendConversationRecord> queryFriendNames(String sWhere){
        Set<FriendConversationRecord> set = new HashSet<>();
        String sql = "select friendName, id from "+tableName;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                set.add(Record2Entity(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public boolean insert(String accountName, String friendName, long conversationID) {
        String sql = "insert into "+tableName+"(name,friendName,id) values"
                + "(?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, accountName);
            pstmt.setString(2, friendName);
            pstmt.setLong(3,conversationID);
            int n = pstmt.executeUpdate();
            System.out.println(n);
            pstmt.close();
            return n>0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean delete(String accountName,String friendName){
        return delete(" where name = '"+accountName+"' and friendName = '"+friendName+"' ");
    }

    public boolean delete(String sWhere) {
        String sql = "delete from "+tableName+" ";
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            int n = stmt.executeUpdate(sql);
            stmt.close();
            return n > 0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    private FriendConversationRecord Record2Entity(ResultSet rs) throws SQLException {
        FriendConversationRecord temp = new FriendConversationRecord();
        temp.setConversationId(rs.getLong("id"));
        temp.setFriendName(rs.getString("friendName"));
        return temp;
    }
}
