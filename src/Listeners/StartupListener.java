package Listeners;

import Server.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by Michaël on 11/24/2015.
 */
@WebListener
public class StartupListener implements ServletContextListener {

    Server server;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        server = new Server();
        server.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        server.stop();
    }
}
