package app.ebrahim;


import app.ebrahim.connection.RepositoryFactory;
import app.ebrahim.service.AccountService;
import app.ebrahim.service.ExceptionMapperService;
import app.ebrahim.service.PartyService;
import app.ebrahim.service.TransactionService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;


public class Application {
    private static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        log.info("Prepare for demo persistence data ...");
        RepositoryFactory h2Factory = RepositoryFactory.getRepository(RepositoryFactory.H2);
        h2Factory.populateTestData();
        log.info("Initialisation Complete ...");

        startService();
    }

    private static void startService() throws Exception {
        Server server = new Server(8888);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames"
                , PartyService.class.getCanonicalName()
                        + "," + AccountService.class.getCanonicalName()
                        + "," + ExceptionMapperService.class.getCanonicalName()
                        + "," + TransactionService.class.getCanonicalName());
        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}
