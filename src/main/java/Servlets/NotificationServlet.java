package Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by Benoit on 2015-11-24.
 */
@WebServlet(name = "NotificationServlet", urlPatterns = "/servlets/notification")
public class NotificationServlet extends HttpServlet {
    static Date timer = new Date();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //content type must be set to text/event-stream
        response.setContentType("text/event-stream");

        //encoding must be set to UTF-8
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        Date newTimer = new Date();

        //writer.write("data: "+ Math.abs(newTimer.getSeconds() + 60 - timer.getSeconds()) % 60 +"\n\n");
        writer.close();

        timer = newTimer;
    }
}
