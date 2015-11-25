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
@WebServlet(name = "SampleServlet", urlPatterns = "/servlets/sample")
public class SampleServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>This is a sample servlet.<h1>");
        out.println("<h2>Use it to test some stuff and base yourself on it.<h2>");
        out.flush();
    }
}
