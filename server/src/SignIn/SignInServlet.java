package SignIn;

import database.Storage;
import database.UserContentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by chan on 15-9-6.
 */
public class SignInServlet extends javax.servlet.http.HttpServlet {

    protected void doPost(javax.servlet.http.HttpServletRequest request,
                          javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        try {
            doResponse(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request,
                         javax.servlet.http.HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {}

    /**
     * @param request
     * @param response
     */
    private void doResponse(HttpServletRequest request,HttpServletResponse response)
            throws IOException, SQLException {


        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(request.getInputStream())
        );

        //获得参数
        final String userName = bufferedReader.readLine();
        final String passWord = bufferedReader.readLine();
        PrintWriter writer = response.getWriter();

        //这里要判断是否为空
        if (userName == null ||
                passWord == null) {
            writer.write("false");
            return;
        }

        UserContentResolver contentResolver = new UserContentResolver();
        ResultSet resultSet = contentResolver.query(
                new String[]{Storage.UserColumns.PASS_WORD},
                Storage.UserColumns.USER_NAME + "=?", new String[]{ userName });

        //如果获得了结果
        if (resultSet != null && resultSet.next()) {

            //并且密码是正确的 那么返回true
            String value = resultSet.getString(Storage.UserColumns.PASS_WORD);
            if(passWord.equals(value)){
                writer.write("true");
                return;
            }
        }

        //否则返回false
        writer.write("false");
    }
}
