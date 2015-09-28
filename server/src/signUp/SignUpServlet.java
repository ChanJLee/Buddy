package signUp;

import com.mysql.jdbc.Connection;
import database.UserContentResolver;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chan on 15-9-12.
 */
@WebServlet(name = "SignUpServlet")

public class SignUpServlet extends HttpServlet {

    static private UserContentResolver m_userContentResolver;

    static private UserContentResolver getUserContentResolver(){

        if(m_userContentResolver == null)
            m_userContentResolver = new UserContentResolver();
        return  m_userContentResolver;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doResponse(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    private void doResponse(HttpServletRequest request, HttpServletResponse response) {
        try {
            String message = null;
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(request.getInputStream())
            );
            String separate = bufferedReader.readLine();
            for (int i = 0; i < 3; ++i)
                bufferedReader.readLine();
            String info = bufferedReader.readLine();

            for (int i = 0; i < 5; ++i)
                bufferedReader.readLine();
            StringBuffer buffer = new StringBuffer();

            while ((message = bufferedReader.readLine()) != null) {
                buffer.append(message + "\n");
            }

            int index = buffer.indexOf(separate);
            String avatar = buffer.substring(0, index);
            String information[] = info.split(" ");

            PrintWriter printWriter = response.getWriter();
            if (register(information[0], information[1], information[2], avatar)) {
                printWriter.write("true");
            } else {
                printWriter.write("false");
            }
        } catch (Exception e) {}
    }

    private boolean register(String name,String userName,String passWord,String avatar){
        try{
            UserContentResolver contentResolver = getUserContentResolver();
            contentResolver.insert(name, userName, passWord);
            File dir = new File("/home/chan/buddy-server/" + userName );
            dir.mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir,"avatar.png"));
            fileOutputStream.write(avatar.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}