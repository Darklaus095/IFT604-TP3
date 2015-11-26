package Listeners;

import Server.Server;
import Services.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by Michaël on 11/24/2015.
 */
@WebListener
public class StartupListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    Server server;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        server = new Server();
        ServerService.initialize(server);
        ServerService.getInstance().start();

        logger.info("Service initialized & started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        ServerService.getInstance().stop();
        logger.info("Service & server stopped");
    }
}
