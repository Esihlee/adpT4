
package za.ac.cput.Conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * @author Esihle
 */
public class DBConnect {
    
    private static final Logger LOGGER = Logger.getLogger(DBConnect.class.getName());
    
    private static final String DB_URL = "jdbc:derby://localhost:1527/CarVotes";
    private static final String USERNAME = "administrator";
    private static final String PASSWORD = "admin";

    public Connection derbyConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            LOGGER.info("Connection to the database was successful.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection failed: " + e.getMessage(), e);
            throw new SQLException("Could not connect to the database. Please check your credentials.", e);
        }
        return connection;
    }
}
