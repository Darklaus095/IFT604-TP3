package Servlets;

import Common.Models.Game;
import Services.ServerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Utilisateur on 2015-11-24.
 */
@WebServlet(name = "SampleServlet", urlPatterns = "/servlets/sample")
public class SampleServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SampleServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        logger.info("Getting games from server");
        List<Game> games = ServerService.getInstance().getGames();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(gson.toJson(games));
        out.flush();

        logger.info("Flushed game list");
    }
}
