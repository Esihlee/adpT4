package za.ac.cput.mybackimage;


/**
 *
 * @author henzley
 */
import java.io.IOException;
import java.net.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;


public class ClientSide {

    private Socket clientSocket;
    protected static ObjectOutputStream out;
    protected static ObjectInputStream in;

    public ClientSide() {
        //establish connection to server
        try {
            clientSocket = new Socket("localhost", 6666);
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
        
//        getStreams();
    }

    public void getStreams() {
        try {
            //construct stream objects for data transfer
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
    }

    public void communicate(String newCarName) {
        try {
            // Send the new car name to the server
            out.writeObject(newCarName);
            out.flush();

            // Wait for the server's response
            String serverResponse = (String) in.readObject();

            // Check if the insertion was successful
            if (serverResponse.equals("Success")) {
                // Notify the user
                System.out.println("Car added successfully!");
            } else {
                System.out.println("Error: " + serverResponse);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Communication Error: " + e.getMessage());
        }
    }
    
    public void closeConnection() throws IOException{
        if (out != null) out.close();
        if (in != null) in.close();
        if (clientSocket != null) clientSocket.close();
        
    }

}
