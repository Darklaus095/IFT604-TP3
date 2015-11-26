package Servlets;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        //int id = Integer.getInteger(request.getParameter("GameId"));

        /*
        GameInfo info = service.getGameInfo(id);

         */
        out.write("<h1>This is a test<h1>");
        out.flush();
    }
}
