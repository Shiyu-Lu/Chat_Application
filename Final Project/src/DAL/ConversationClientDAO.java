package DAL;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.RawConversationRecord;

/**
 * A Conversation entry has id, conversationName stored in Table Conversations
 * */

public class ConversationClientDAO {
    private Connection conn;

    static private String tableName = "Conversations";

    public ConversationClientDAO(String userName) {
        conn = ClientDBConnection.getInstance(userName).getConnection();
    }

    public List<RawConversationRecord> query(String sWhere) {
        List<RawConversationRecord> list = new ArrayList<RawConversationRecord>();
        String sql = "select * from " + tableName ;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                RawConversationRecord temp = Record2Entity(rs);
                list.add(temp);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RawConversationRecord> queryAll() {
        return query("");
    }


    public boolean insert(RawConversationRecord r) {
        String sql = "insert into "+tableName+"(id,conversationName) values"
                + "(?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,r.getId());
            pstmt.setString(2, r.getConversationName());
            int n = pstmt.executeUpdate();
            pstmt.close();
            return n>0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean insert(long id,String conversationName){
        RawConversationRecord rawConversationRecord = new RawConversationRecord();
        rawConversationRecord.setConversationName(conversationName);
        rawConversationRecord.setId(id);
        return insert(rawConversationRecord);
    }

    public boolean update(RawConversationRecord c, String sWhere) {
        String sql = "update "+tableName+" set id = ?, conversationName = ?";
        sql += sWhere;

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, c.getId());
            pstmt.setString(2, c.getConversationName());
            int n = pstmt.executeUpdate();
            pstmt.close();
            return n > 0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean updateById(RawConversationRecord newConversation, long id) {
        return update(newConversation, " where id=" + id);
    }

    public boolean update(RawConversationRecord r) {
        return updateById(r, r.getId());
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
        return delete(" where id=" + id);
    }

    public boolean delete(RawConversationRecord r) {
        return deleteById(r.getId());
    }

    private RawConversationRecord Record2Entity(ResultSet rs) throws SQLException {
        RawConversationRecord temp = new RawConversationRecord();
        temp.setId(rs.getLong("id"));
        temp.setConversationName(rs.getString("conversationName"));
        return temp;
    }

}