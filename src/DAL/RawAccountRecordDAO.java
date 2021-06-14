package DAL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * An Account entry has name(Primary key), password, stored in Accounts Table
 * */

public class RawAccountRecordDAO {
    private Connection conn;

    static private String tableName = "Accounts";

    public RawAccountRecordDAO() {
        conn = ServerDBConnection.getInstance().getConnection();
    }

    public List<RawAccountRecord> query(String sWhere) {
        List<RawAccountRecord> list = new ArrayList<RawAccountRecord>();
        String sql = "select * from " + tableName ;
        sql += sWhere;
        System.out.println(sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                RawAccountRecord temp = Record2Entity(rs);
                list.add(temp);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<RawAccountRecord> queryByAccountName(String accountName){
        return query(" where name = '"+accountName+"' ");
    }

    public List<RawAccountRecord> queryAll() {
        return query("");
    }

    public boolean insert(RawAccountRecord r) {
        String sql = "insert into "+tableName+"(name,password) values"
                + "(?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, r.getName());
            pstmt.setString(2, r.getPassword());
            int n = pstmt.executeUpdate();
            //System.out.println(n);
            pstmt.close();
            return n>0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean update(RawAccountRecord c, String sWhere) {
        String sql = "update "+tableName+" set name = ?, password = ?";
        sql += sWhere;

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getPassword());
            int n = pstmt.executeUpdate();
            pstmt.close();
            return n > 0;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return false;
    }

    public boolean updateByName(RawAccountRecord newAccount, String name) {
        return update(newAccount, " where name= '" + name+"' ");
    }

    public boolean update(RawAccountRecord r) {
        return updateByName(r, r.getName());
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

    public boolean deleteByName(String name) {
        return delete(" where name='" + name+"' ");
    }

    public boolean delete(RawAccountRecord r) {
        return deleteByName(r.getName());
    }

    private RawAccountRecord Record2Entity(ResultSet rs) throws SQLException {
        RawAccountRecord temp = new RawAccountRecord();
        temp.setName(rs.getString("name"));
        temp.setPassword(rs.getString("password"));
        return temp;
    }

}


