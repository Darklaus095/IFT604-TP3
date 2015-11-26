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

        PrintWriter writer = response.getWriter();

        synchronized (messages) {
            if(!messages.isEmpty()) {
                for (JsonObject message : messages) {
                    writer.write("data: " + message.getAsString() + "\n\n");
                }

                messages.clear();
            }
        }

        writer.close();
    }
}
