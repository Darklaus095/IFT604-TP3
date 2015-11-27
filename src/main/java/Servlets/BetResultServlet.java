package Servlets;

import Common.Models.Bet;
import Common.helpers.JsonSerializer;
import Services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Michaël on 11/26/2015.
 */
@WebServlet(name = "BetResultServlet", urlPatterns = "/servlets/betresult")
public class BetResultServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BetResultServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Getting bet result from server");

        Bet bet = ServerService.getInstance().getBetResult(Integer.parseInt(request.getParameter("betID")));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonSerializer.serialize(bet));

        logger.info("Sending back bet result");
    }
}
