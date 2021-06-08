package DAL;


import model.OfflineMessageRecord;
import model.RawConversationRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *   receiverName varchar(30) , senderName varchar(30),conversationID integer ,message nvarchar(200),sendTime integer,messageType interger
 */
public class OfflineMessageDAO {
    private Connection conn;

    static private String tableName = "OfflineMessage";

    public OfflineMessageDAO() {
        conn = ServerDBConnection.getInstance().getConnection();
    }

    public List<OfflineMessageRecord> query(String sWhere) {
        List<OfflineMessageRecord> list = new ArrayList<>();
        String sql = "select * from " + tableName ;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                OfflineMessageRecord temp = Record2Entity(rs);
                list.add(temp);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OfflineMessageRecord> queryByReceiverName(String receiverName){
        return query(" where receiverName ='"+receiverName+"' ");
    }

    public boolean insert(OfflineMessageRecord r) {
        String sql = "insert into "+tableName+"(receiverName, senderName ,conversationID ,message ,sendTime ,messageType) values"
                + "(?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,r.getReceiverName());
            pstmt.setString(2, r.getSenderName());
            pstmt.setLong(3,r.getConversationID());
            pstmt.setString(4,r.getMessage());
            pstmt.setLong(5,r.getSendTime());
            pstmt.setInt(6,r.getMessageType());
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

    public boolean deleteByReceiverName(String receiverName) {

        return delete(" where receiverName='" +receiverName+"' " );
    }

    public boolean deleteByConversationId(long conversationId){
        return delete(" where conversationID="+conversationId);
    }

    private OfflineMessageRecord Record2Entity(ResultSet rs) throws SQLException {
        OfflineMessageRecord temp = new OfflineMessageRecord();
        temp.setReceiverName(rs.getString("receiverName"));
        temp.setSenderName(rs.getString("senderName"));
        temp.setConversationID(rs.getLong("conversationID"));
        temp.setMessage(rs.getString("message"));
        temp.setSendTime(rs.getLong("sendTime"));
        temp.setMessageType(rs.getInt("messageType"));
        return temp;
    }

}
