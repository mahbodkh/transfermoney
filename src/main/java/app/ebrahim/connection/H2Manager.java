package app.ebrahim.connection;

import app.ebrahim.repository.AccountRepositoryImpl;
import app.ebrahim.repository.PartyRepositoryImpl;
import app.ebrahim.util.PropertiesReader;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class H2Manager extends RepositoryFactory {
    private static Logger log = Logger.getLogger(H2Manager.class);

    private static final String h2_driver = PropertiesReader.getStringProperty("h2_driver");
    private static final String h2_connection_url = PropertiesReader.getStringProperty("h2_connection_url");
    private static final String h2_user = PropertiesReader.getStringProperty("h2_user");
    private static final String h2_password = PropertiesReader.getStringProperty("h2_password");

    private final AccountRepositoryImpl accountRepository = new AccountRepositoryImpl();
    private final PartyRepositoryImpl partyRepository = new PartyRepositoryImpl();


    H2Manager() {
        // init: load driver
        DbUtils.loadDriver(h2_driver);
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);
    }

    @Override
    public void populateTestData() {
        log.info("Populating Test User Table and data ..... ");
        Connection conn = null;
        try {
            conn = H2Manager.getConnection();
            RunScript.execute(conn, new FileReader("src/main/resources/demo.sql"));
        } catch (SQLException e) {
            log.error("populateTestData(): Error populating user data: ", e);
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            log.error("populateTestData(): Error finding test script file ", e);
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }


    @Override
    public PartyRepositoryImpl getPartyRepository() {
        return partyRepository;
    }


    @Override
    public AccountRepositoryImpl getAccountRepository() {
        return accountRepository;
    }


}
