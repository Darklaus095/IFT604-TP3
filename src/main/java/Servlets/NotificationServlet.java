package Servlets;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benoit on 2015-11-24.
 */
@WebServlet(name = "NotificationServlet", urlPatterns = "/servlets/notification")
public class NotificationServlet extends HttpServlet {

    private static List<String> events = new ArrayList<>();

    public static void addEvent(String event) {
        synchronized (events) {
            events.add(event);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //content type must be set to text/event-stream
        response.setContentType("text/event-stream");
        //encoding must be set to UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        PrintWriter writer = response.getWriter();

        String lastEventID = request.getHeader("Last-Event-ID");
        int last = lastEventID != null ? Integer.parseInt(lastEventID) : -1;

        synchronized (events) {
            for (int i = last+1; i < events.size(); ++i) {
                writer.write(events.get(i) + "\n");
                writer.flush();
            }
        }

        writer.close();
    }
}
