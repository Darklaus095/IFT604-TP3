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

    private static List<JsonObject> messages = new ArrayList<>();

    public static void AddMessage(JsonObject message) {
        synchronized (messages) {
            messages.add(message);
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

        synchronized (messages) {
            for (int i = last+1; i < messages.size(); ++i) {
                writer.write("id: " + i + "\n");
                writer.write("data: " + messages.get(i).toString() + "\n\n");
                writer.flush();
            }
        }

        writer.close();
    }
}
