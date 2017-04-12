package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class GameClient extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	private Socket gSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private JButton rockBtn = new JButton("Sten");
	private JButton sciBtn = new JButton("Sax");
	private JButton bagBtn = new JButton("P�se"); 
	private JLabel messLbl = new JLabel();
	private JPanel buttonPanel = new JPanel();
	private int clientScore = 0, serverScore = 0;

    /*
    *   Setup a new socket and out/in-streams. We will also
    *   start a new thread for this.
    */

    public GameClient(String ipAddress, int port){
        
        try {
			gSocket = new Socket(ipAddress, port);
			out = new PrintWriter(gSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(gSocket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        setupWidgets();
        
        new Thread(this).start();
    }

    /*
     * Skapar alla grafiska komponenter
     */
    private void setupWidgets()
    {
        this.setTitle("Game client");
        this.setBounds(50, 50, 300, 250);
        this.setLayout(new BorderLayout());

        buttonPanel.setLayout(new GridLayout(3, 1)); 
        rockBtn.setPreferredSize(new Dimension(200, 40)); 
        sciBtn.setPreferredSize(new Dimension(200, 40)); 
        bagBtn.setPreferredSize(new Dimension(200, 40)); 
        messLbl.setPreferredSize(new Dimension(
                                buttonPanel.getSize().width, 40));
        
        JPanel p1 = new JPanel();
        p1.add(rockBtn);
        buttonPanel.add(p1);  
        JPanel p2 = new JPanel();
        p2.add(sciBtn);
        buttonPanel.add(p2);
        JPanel p3 = new JPanel();
        p3.add(bagBtn);
        buttonPanel.add(p3);
        
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(messLbl, BorderLayout.SOUTH);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void run()
    {
        String message;
        String fromServer;
        
        message = JOptionPane.showInputDialog("Hej");
        out.println(message);

        try {
            /* Löser meddelanden från serverapplikationen */
            while ((fromServer = in.readLine()) != null) {
                
            }
            
            gSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void disableButtons(){
        rockBtn.setEnabled(false);
        sciBtn.setEnabled(false);
        bagBtn.setEnabled(false);
    }

    public void enableButtons()
    {
        rockBtn.setEnabled(true);
        sciBtn.setEnabled(true);
        bagBtn.setEnabled(true);
    }

    public String getScore(){
        return "Client: " + clientScore + ", Server: " + serverScore;
    }

    /*
    *   Då vi inte har sysslat med att manipulera args[] arrayen så 
    *   har vi hårdkodat in dem härifrån, hoppas det inte 
    *   spelar någon större roll.
    */

    public static void main(String[] args) throws SocketException{
    	String address = "127.0.0.1";
    	int port = 4444;
    	
        new GameClient(address, port);
    } 
}
