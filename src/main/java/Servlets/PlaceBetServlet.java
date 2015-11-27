package Servlets;

import Common.Models.Bet;
import Common.Models.GameInfo;
import Services.ServerService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Utilisateur on 2015-11-25.
 */
@WebServlet(name = "PlaceBetServlet", urlPatterns = "/servlets/placebet")
public class PlaceBetServlet extends HttpServlet{
    private static final Logger logger = LoggerFactory.getLogger(PlaceBetServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Placing bet in server");

        int id = Integer.parseInt(request.getParameter("gameID"));
        String team = request.getParameter("betOn");
        double amount = Double.parseDouble(request.getParameter("amount"));

        ServerService.getInstance().putBet(new Bet(amount, team, id));
    }
}
