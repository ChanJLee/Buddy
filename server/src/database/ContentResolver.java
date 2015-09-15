package database;

import com.mysql.fabric.xmlrpc.base.Data;
import com.mysql.jdbc.Connection;

import java.sql.*;

/**
 * Created by chan on 15-9-9.
 */
public abstract class ContentResolver {
    private static Connection s_connection;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql:///chan_database?user=root&password=19940525&useUnicode=true&characterEncoding=utf-8";

    static private Connection getConnection(){
        if(s_connection == null){
            try{
                Class.forName(DRIVER);
                s_connection = (Connection) DriverManager.getConnection(URL);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return s_connection;
    }

   public ResultSet query(String[] projections,String selectionArg,String[] selectionValues)
           throws SQLException {
        ResultSet cursor = null;
        Connection connection = getConnection();
        if(connection == null) return null;

        try {
            Statement statement = connection.createStatement();
            final StringBuffer sql = new StringBuffer("select ");

            //如果要返回的内容没有指定  那么就返回所有的字段
            if(projections == null)
                sql.append("* ");
            else{

                for(int i = 0;i < projections.length;++i)
                    sql.append(projections[i] + ",");

                //删除最后一个多余的 ','
                sql.deleteCharAt(sql.length() - 1);
            }

            sql.append(" from " + getTables());

            if(selectionArg != null) {
                sql.append(" where ");

                int i = 0;
                int index = -1;
                sql.append(selectionArg);
                while (( index = sql.indexOf("?")) != -1) {
                    sql.replace(index, index + 1, selectionValues[i++]);
                }
            }
            sql.append(";");
            String sqlQuery = sql.toString();
            cursor = statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            if(cursor != null){
                cursor.close();
            }
        }
        return cursor;
    }

    public void insert(String name,String userName,String passWord) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        StringBuffer buffer = new StringBuffer("insert into USER(");
        buffer.append(Storage.UserColumns.NAME + ",");
        buffer.append(Storage.UserColumns.USER_NAME + ",");
        buffer.append(Storage.UserColumns.PASS_WORD + ",");
        buffer.append(Storage.UserColumns.SIGN_UP_DATE + ")");

        Date date = new Date(System.currentTimeMillis());
        buffer.append(" values(\"");
        buffer.append(name + "\",\"");
        buffer.append(userName + "\",\"");
        buffer.append(passWord + "\",\"");
        buffer.append(date.toString() + "\")");

        String sql = buffer.toString();
        statement.execute(sql);

        System.out.println(sql);
    }

    protected abstract String getTables();
}
