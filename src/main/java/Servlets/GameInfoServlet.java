package Servlets;

import Common.Models.GameInfo;
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

/**
 * Created by Utilisateur on 2015-11-24.
 */
@WebServlet(name = "GameInfoServlet", urlPatterns = "/servlets/gameinfo")
public class GameInfoServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SampleServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Getting gameInfo from server");

        GameInfo info = ServerService.getInstance().getGameInfo(Integer.parseInt(request.getParameter("GameID")));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(info));

        logger.info("Sending back game info");
    }
}
