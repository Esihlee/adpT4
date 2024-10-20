/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.cput.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import za.ac.cput.Domain.CarVote;

/**
 *
 * @author ethan
 */

public class CarServer  extends JFrame {
    private ServerSocket listener;
    private String msg = "";
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String response = "";
    private Socket client;
    private Object receivedObject;
    private CarDAO dao;
    
    private JButton exitBtn;
    private JTextArea clientTxtArea;
    private JPanel topPanel;
    private JPanel centerPanel;
    
    public CarServer() throws IOException{
        listener = new ServerSocket(6666,10);
        dao = new CarDAO();
        exitBtn = new JButton("EXIT");
        clientTxtArea = new JTextArea(10,30);
        response = "";
        topPanel = new JPanel();
        centerPanel = new JPanel();

        setGui();
    }
    
    public void setGui(){
        clientTxtArea.setEditable(false);
        centerPanel.add(clientTxtArea);
        topPanel.add(exitBtn);
        this.add(topPanel);
        this.add(centerPanel);
    }
    
    private void listenForClients() throws IOException
    {
        clientTxtArea.append("Listening for Clients...");
        System.out.println("Listening for Clients...");
        
        client = listener.accept();
        
        clientTxtArea.append("\n"+client.getInetAddress() + " is connected.");
        System.out.println(client.getInetAddress() + " is connected.");
    }
    
    private void getStreams() throws IOException
    {   
        out = new ObjectOutputStream(client.getOutputStream());
        out.flush();
        in = new ObjectInputStream(client.getInputStream());
        
    }
    
    private void sendData(Object myMsg) throws IOException {
        out.writeObject(myMsg);
        out.flush();
    }
    
    public void closeStuff() throws IOException{
        in.close();
        out.close();
        this.dispose();
    }
    
public void processClient() throws IOException, ClassNotFoundException, SQLException {
    clientTxtArea.append("\nProcessing the Client...");
    System.out.println("Processing the Client...");
    while (true) {
        Object msg = in.readObject();

        clientTxtArea.append("\nFrom CLIENT>> " + msg);
        System.out.println("From CLIENT>> " + msg);

        // Checking if the message is a string
        if (msg instanceof String) {
            String strMsg = (String) msg;

            if (strMsg.equalsIgnoreCase("exit")) {
                clientTxtArea.append("\nClient has terminated the connection.");
                System.out.println("Client has terminated the connection.");
                break;
            }

            // Handling view request
            if (strMsg.equalsIgnoreCase("view")) {
                try {
                    List<CarVote> carVotes = dao.getCarVotesFromDatabase(); // Fetch car votes
                    sendData(carVotes); // Send the list of votes back to the client
                    clientTxtArea.append("\nSent car votes to client.");
                    System.out.println("Sent car votes to client.");
                } catch (SQLException e) {
                    clientTxtArea.append("\nError retrieving car votes: " + e.getMessage());
                    System.out.println("Error retrieving car votes: " + e.getMessage());
                }
            }

            // Handling new car addition
            else {
                try {
                    createNewCar(strMsg);
                    clientTxtArea.append("\nCar added successfully: " + strMsg);
                    System.out.println("Car added successfully: " + strMsg);
                } catch (SQLException e) {
                    clientTxtArea.append("\nError adding car: " + e.getMessage());
                    System.out.println("Error adding car: " + e.getMessage());
                }
            }
        }

        // Checking if the message is a CarVote object
        if (msg instanceof CarVote) {
            CarVote vote = (CarVote) msg;
            try {
                voteForCar(vote.getCarName());
                clientTxtArea.append("\nVote received for: " + vote.getCarName());
                System.out.println("Vote received for: " + vote.getCarName());
            } catch (SQLException e) {
                clientTxtArea.append("\nError processing vote: " + e.getMessage());
                System.out.println("Error processing vote: " + e.getMessage());
            }
        }
    }
    closeStuff();
}



    void voteForCar(String carName) throws SQLException{
        dao.voteForExistingCar(carName);
    
        clientTxtArea.append("Voted for " + carName + " successfully");
        System.out.println("Voted for " + carName + " successfully");
    }
    
    void createNewCar(String name) throws SQLException, IOException {
        CarVote cv = new CarVote(name, 0);
        CarDAO dao = new CarDAO();
        dao.addCar(cv);

        clientTxtArea.append("\nAdded new car " + name + " successfully.");
        System.out.println("Added new car " + name + " successfully.");

        // Send success response to the client
        sendData("Success");
    }

    
    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException
    {
        CarServer cs = new CarServer();
        cs.setDefaultCloseOperation(EXIT_ON_CLOSE);
        cs.setSize(500, 500);
        cs.setVisible(true);
        
        cs.listenForClients();
        cs.getStreams();
        cs.sendData("Its the client Yo");
//        cs.voteForCar("Audi Q8");
        cs.processClient();
    }
}
