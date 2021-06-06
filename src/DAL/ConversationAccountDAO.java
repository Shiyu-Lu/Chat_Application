package DAL;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * id integer , accountName varchar(30)
 */

public class ConversationAccountDAO {
    private Connection conn;
    static private String tableName = "ConversationAccounts";

    public ConversationAccountDAO() {
        conn = ServerDBConnection.getInstance().getConnection();
    }

    public Set<String> queryAccountNameById(long id) {
        return queryAccountName(" where id = "+id);
    }

    public Set<String> queryAccountName(String sWhere) {
        Set<String> set = new HashSet<>();
        String sql = "select accountName from "+tableName;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                set.add(rs.getString("accountName"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }


    public boolean insert(long id, String accountName) {
        String sql = "insert into "+tableName+"(id,accountName) values"
                + "(?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.setString(2, accountName);
            int n = pstmt.executeUpdate();
            System.out.println(n);
            pstmt.close();
            return n>0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean deleteById(long id) {
        return delete(" where id=" + id);
    }

    public boolean deleteByIdAndAccountName(long id,String accountName){
        return delete(" where id ="+ id + " and accountName='"+accountName+"' " );
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
}
