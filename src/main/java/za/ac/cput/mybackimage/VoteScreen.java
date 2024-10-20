package za.ac.cput.mybackimage;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import za.ac.cput.Server.CarDAO;
import za.ac.cput.Domain.CarVote;

/**
 *
 * @author Esihle Mlinjana (222441712)
 */
public class VoteScreen extends JFrame {
    ClientSide cs;
    private JPanel pnlFront = new JPanel() {
        private Image backImage = new ImageIcon("FM.png").getImage();

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backImage, 0, 0, getWidth(), getHeight(), this);
        }
    };
    private JPanel pnlTable = new JPanel();
    private JLabel lblCar = new JLabel("CAR OF THE YEAR");
    private JLabel lblCars = new JLabel("Luxury Cars");
    private JLabel lblAdd = new JLabel("Add A Car");
    private JTextField txtAdd = new JTextField();
    private String strCars[] = {"Select:"};
    private JComboBox cbCars = new JComboBox(strCars);
    private DefaultTableModel tblModel = new DefaultTableModel();
    private JTable tblCar = new JTable(tblModel);
    private JButton btnAdd = new JButton("Add");
    private JButton btnVote = new JButton("Vote");
    private JButton btnView = new JButton("View");
    private JButton btnExit = new JButton("Exit");

    CarDAO dao = new CarDAO();
    CarVote car;

    public VoteScreen() {
        super("Luxury Vote");
        cs = new ClientSide();
        cs.getStreams();
        
        lblCar.setBounds(400, 50, 300, 200);
        lblCar.setFont(new Font("Serif", Font.BOLD, 30));
        lblCar.setForeground(Color.WHITE);

        lblAdd.setBounds(600, 100, 300, 200);
        lblAdd.setFont(new Font("Serif", Font.BOLD, 20));
        lblAdd.setForeground(Color.WHITE);

        lblCars.setBounds(380, 100, 300, 200);
        lblCars.setFont(new Font("Serif", Font.BOLD, 20));
        lblCars.setForeground(Color.WHITE);

        txtAdd.setBackground(Color.WHITE);
        txtAdd.setBounds(580, 230, 150, 30);
        //txtAdd.setBounds(WIDTH, WIDTH, WIDTH, HEIGHT);
        cbCars.setBackground(Color.WHITE);
        cbCars.setBounds(360, 230, 150, 35);

        pnlTable.setBounds(300, 300, 500, 250);
        tblModel.addColumn("Car Name");
        tblModel.addColumn("Vote");

        tblCar.setBounds(300, 300, 500, 250);
        tblCar.setForeground(Color.BLACK);
        tblCar.setBackground(Color.WHITE);

        btnAdd.setBounds(320, 600, 100, 30);
        btnAdd.setBackground(Color.BLACK);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Serif", Font.BOLD, 25));

        btnVote.setBounds(440, 600, 100, 30);
        btnVote.setBackground(Color.BLACK);
        btnVote.setForeground(Color.WHITE);
        btnVote.setFont(new Font("Serif", Font.BOLD, 25));

        btnView.setBounds(560, 600, 100, 30);
        btnView.setBackground(Color.BLACK);
        btnView.setForeground(Color.WHITE);
        btnView.setFont(new Font("Serif", Font.BOLD, 25));

        btnExit.setBounds(680, 600, 100, 30);
        btnExit.setBackground(Color.BLACK);
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Serif", Font.BOLD, 25));

        add(btnAdd);
        add(btnVote);
        add(btnView);
        add(btnExit);
        add(cbCars);
        add(txtAdd);
        add(lblCars);
        add(lblAdd);
        add(lblCar);
        add(pnlTable);
        pnlTable.add(new JScrollPane(tblCar));
        add(pnlFront);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCarName = txtAdd.getText();
                if (!newCarName.isEmpty()) {
                    try {
                        cs.communicate(newCarName);

                        String[] newStrCars = new String[strCars.length + 1];
                        System.arraycopy(strCars, 0, newStrCars, 0, strCars.length);
                        newStrCars[newStrCars.length - 1] = newCarName;
                        strCars = newStrCars;

                        cbCars.setModel(new DefaultComboBoxModel<>(strCars));
                        txtAdd.setText("");

                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        });

        btnView.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ClientSide.out.writeObject("view");
                    ClientSide.out.flush();

                    Object response = cs.in.readObject();
                    if (response instanceof List) {
                        List<CarVote> cars = (List<CarVote>) response;
                        tblModel.setRowCount(0);
                        for (CarVote car : cars) {
                            tblModel.addRow(new Object[]{car.getCarName(), car.getVote()});
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Error requesting data: " + ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(VoteScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        btnVote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCar = (String) cbCars.getSelectedItem();
                if (selectedCar != null && !selectedCar.equals("Select:")) {
                    try {
                        CarVote vote = new CarVote(selectedCar, 1); // Sending 1 vote
                        ClientSide.out.writeObject(vote);
                        ClientSide.out.flush();

                        JOptionPane.showMessageDialog(null, "Vote cast successfully for: " + selectedCar);
                    } catch (IOException ex) {
                        System.out.println("Error sending vote to server: " + ex.getMessage());
                        JOptionPane.showMessageDialog(null, "Error sending vote. Please try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a car to vote for.");
                }
            }
        });
        
         btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                try {
                    cs.closeConnection();
                } catch (IOException ex) {
                    Logger.getLogger(VoteScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }
}
