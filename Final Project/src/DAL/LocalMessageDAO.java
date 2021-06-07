package DAL;

import java.sql.*;
import java.util.*;

/**
 * ConversationMessage( conversationID integer , senderName varchar(30), message nvarchar(200),sendTime integer)
 */


public class LocalMessageDAO {
    private Connection conn;
    static private String tableName = "ConversationMessage";

    public LocalMessageDAO(String userName){
        conn = ClientDBConnection.getInstance(userName).getConnection();
    }

    public SortedSet<ConversationMessage> query(String sWhere){
        SortedSet<ConversationMessage> set = new TreeSet<>();
        String sql = "select * from " + tableName ;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ConversationMessage temp = Record2Entity(rs);
                set.add(temp);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    public SortedSet<ConversationMessage> queryByID(Long id){
        return query(" where conversationID ="+id);
    }

    public boolean insert(ConversationMessage c) {
        String sql = "insert into "+tableName+"(conversationID,senderName,message,sendTime) values"
                + "(?,?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,c.getConersationID());
            pstmt.setString(2, c.getSenderName());
            pstmt.setString(3,c.getContent());
            pstmt.setLong(4,c.getSendTime());
            int n = pstmt.executeUpdate();
            pstmt.close();
            return n>0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean delete(String sWhere) {
        String sql = "delete from "+tableName;
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

    public boolean deleteById(long id) {
        return delete(" where conversationID=" + id);
    }

    public boolean delete(ConversationMessage c) {
        return delete(" where conversationID="+c.getConersationID()+" and senderName='"+c.getSenderName()+
                "' and sendTime="+c.getSendTime());
    }

    private ConversationMessage Record2Entity(ResultSet rs) throws SQLException {
        ConversationMessage temp = new ConversationMessage();
        temp.setConersationID(rs.getLong("conversationID"));
        temp.setSenderName(rs.getString("senderName"));
        temp.setContent(rs.getString("message"));
        temp.setSendTime(rs.getLong("sendTime"));
        return temp;
    }
}
