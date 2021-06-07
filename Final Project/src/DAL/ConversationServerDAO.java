package DAL;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.RawConversationRecord;

/**
 * A Conversation entry has id(Primary Key), conversationName stored in Table Conversations
 * */

public class ConversationServerDAO {
    private Connection conn;

    static private String tableName = "Conversations";

    public ConversationServerDAO() {
        conn = ServerDBConnection.getInstance().getConnection();
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

    /**
     * return the global unique (long) id>0 for conversation ;if return 0, then insert fails
     */

    public long insert(RawConversationRecord r) {
        String sql = "insert into "+tableName+"(conversationName) values"
                + "(?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, r.getConversationName());
            int n = pstmt.executeUpdate();
            //System.out.println(n);

            if (n > 0) {
                // get the autoincreament id
                ResultSet rsKey = pstmt.getGeneratedKeys();
                if (rsKey.next()) {
                    long autoId = rsKey.getLong(1);
                    r.setId(autoId);
                    return autoId;
                }
            }
            pstmt.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return 0;
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

