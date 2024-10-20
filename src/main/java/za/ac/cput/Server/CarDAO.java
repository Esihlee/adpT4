package za.ac.cput.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import za.ac.cput.Conn.DBConnect;
import za.ac.cput.Domain.CarVote;

/**
 *
 * @author Esihle
 */
public class CarDAO {
    private static final Logger LOGGER = Logger.getLogger(CarDAO.class.getName());
    private DBConnect dbcon;
    private Connection con;
    private PreparedStatement pstmt;

    public CarDAO() {
        try {
            LOGGER.info("Attempting to establish a database connection...");
            dbcon = new DBConnect();
            this.con = dbcon.derbyConnection();
            LOGGER.info("Database connection established successfully.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database connection error: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, "ERROR: " + ex.getMessage());
        }
    }
    
    public void voteForExistingCar(String carName) throws SQLException {
        String command = "UPDATE CARVOTE SET CAR_VOTE = CAR_VOTE + 1 WHERE CAR_NAME = ?";
        try (Connection con = dbcon.derbyConnection(); 
             PreparedStatement pstmt = con.prepareStatement(command)) {
            pstmt.setString(1, carName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vote updated for: " + carName);
            } else {
                System.out.println("No car found with name: " + carName);
            }
        } catch (SQLException e) {
            System.out.println("Error updating votes: " + e.getMessage());
            throw e;
        }
    }

    
    public void addCar(CarVote cv) throws SQLException {
        try {
            String command = "INSERT INTO CARVOTE (CAR_NAME, CAR_VOTE) VALUES (?, ?)";
            con = dbcon.derbyConnection();
            pstmt = con.prepareStatement(command);
            pstmt.setString(1, cv.getCarName());
            pstmt.setInt(2, 0);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully inserted car: " + cv.getCarName());
            } else {
                System.out.println("No rows affected for car: " + cv.getCarName());
            }
        } catch (SQLException e) {
            System.out.println("Error inserting car: " + e.getMessage());
            throw new SQLException("Error inserting car: " + e.getMessage());
        } finally {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        }
    }
    
    public List<CarVote> getCarVotesFromDatabase() throws SQLException {
        List<CarVote> carVotes = new ArrayList<>();
        String query = "SELECT CAR_NAME, CAR_VOTE FROM CARVOTE";
        PreparedStatement pstmt = con.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        System.out.println("Retrieving car votes from database...");
        while (rs.next()) {
            String carName = rs.getString("CAR_NAME");
            int votes = rs.getInt("CAR_VOTE"); 
            carVotes.add(new CarVote(carName, votes));
            System.out.println("Retrieved: " + carName + " with " + votes + " votes.");
        }

        System.out.println("Total car votes retrieved: " + carVotes.size());
        return carVotes;
    }

}
