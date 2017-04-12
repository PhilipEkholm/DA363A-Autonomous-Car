package sten;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 *	Klient sidan av spelet, kommer skicka koder tillbaka
 *  till servern beroende på vilket alternativ som har valts.
 *
 *  @author Mia Persson (förmodad)
 *  @author Aron Polner
 * 	@author Philip Ekholm
 * 	2017-03-26
 */

public class GameClient extends JFrame implements ActionListener, Runnable{
	private static final long serialVersionUID = 1L;
	private Socket gSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private JButton rockBtn = new JButton("Sten");
	private JButton sciBtn = new JButton("Sax");
	private JButton bagBtn = new JButton("P�se"); 
	private JLabel messLbl = new JLabel();
	private JLabel charLbl = new JLabel("R");
	private JPanel buttonPanel = new JPanel();
	private boolean playerSatus = false;
	private int clientScore = 0, serverScore = 0;
	private int clientSelection = -1;

	private final int SELECTED_ROCK = 0;
	private final int SELECTED_SCISSORS = 1;
	private final int SELECTED_BAG = 2;
	private final int AWAIT_OPPONENT = 3;
	private final int CLIENT_WIN = 4;
	private final int SERVER_WIN = 5;
	private final int EQUAL = 6;
	private final int GAME_OVER = 7; 

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

        rockBtn.addActionListener(this);
        sciBtn.addActionListener(this);
        bagBtn.addActionListener(this); 
        
        this.add(buttonPanel, BorderLayout.CENTER);
        this.add(messLbl, BorderLayout.SOUTH);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void run()
    {
        int message = 0;
        String fromServer;

        try {
            /* Löser meddelanden från serverapplikationen */
            while ((fromServer = in.readLine()) != null) {
                message = Integer.parseInt(fromServer);
                /* Läser av meddelandet från servern och  
                   utför en lämplig operation. */
                if (message == AWAIT_OPPONENT) {
                    disableButtons();
                } else if (message == CLIENT_WIN) {
                    clientScore++;
                    messLbl.setText(getScore());
                } else if (message == SERVER_WIN) {
                    serverScore++;
                    messLbl.setText(getScore());
                } else if (message == SELECTED_ROCK) {
                    enableButtons();
                } else if (message == SELECTED_SCISSORS) {
                    enableButtons();
                } else if (message == SELECTED_BAG) {
                    enableButtons();
                } else if (message == GAME_OVER) {
                    messLbl.setText(getScore() + " --GAME OVER--");
                    out.println(GAME_OVER);
                    disableButtons();
                    break;
                }
            }
            
            gSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    /*
     * Hanterar knapp-händelser.
     * Vid knapptryckning skickas ett meddelande till servern.
     */

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == rockBtn) {
            out.println(SELECTED_ROCK);
        } else if (e.getSource() == sciBtn) {
            out.println(SELECTED_SCISSORS);
        } else if (e.getSource() == bagBtn) {
            out.println(SELECTED_BAG);
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
    
    private void handle(int number){
    	charLbl.setText("Funkar");
    	switch(number){
    		case SELECTED_ROCK:
    			charLbl.setText("3");
    		break;
    		case SELECTED_SCISSORS:
    			charLbl.setText("1");
    		break;
    		case SELECTED_BAG:
    			charLbl.setText("2");
    		break;
    	}
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
    	String address = "10.2.8.174";
    	int port = 4444;
    	
        new GameClient(address, port);
    } 
}
