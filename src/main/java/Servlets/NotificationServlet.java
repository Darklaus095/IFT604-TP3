package Servlets;

import Common.Models.HockeyEvent;
import Common.helpers.JsonSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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
    private static final Logger logger = LoggerFactory.getLogger(NotificationServlet.class);


    private static List<HockeyEvent> events = new ArrayList<>();

    public static void addEvent(HockeyEvent event) {
        synchronized (events) {
            events.add(event);
            logger.info("Received event to be sent");
        }
    }

    private List<Integer> getBetIDs(HttpServletRequest request) {
        List<Integer> ids = new ArrayList<>();
        Cookie betCookie = null;

        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("bet"))
                betCookie = cookie;
        }

        if(betCookie == null)
            return ids;


        JsonElement element = new JsonParser().parse(betCookie.getValue().replace("%2C", ","));
        JsonArray array = element.getAsJsonArray();
        for (int i = 0; i < array.size(); ++i) {
            ids.add(array.get(i).getAsInt());
        }

        return ids;
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

        List<Integer> betIDs = getBetIDs(request);

        synchronized (events) {
            for (int i = last+1; i < events.size(); ++i) {
                HockeyEvent event = events.get(i);

                if(event.getBetId() < 0) {
                    writer.write("id: " + i + "\n");
                    writer.write("data: " + JsonSerializer.serialize(events.get(i)) + "\n\n");
                    writer.flush();
                    logger.info("Sending event" + JsonSerializer.serialize(events.get(i)));
                } else if(betIDs.contains(event.getBetId())) {
                    writer.write("id: " + i + "\n");
                    writer.write("event: bet-result\n");
                    writer.write("data: " + JsonSerializer.serialize(events.get(i)) + "\n\n");
                    writer.flush();
                    logger.info("Sending event" + JsonSerializer.serialize(events.get(i)));
                }
            }
        }

        writer.close();
    }
}
