package sten;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/*
 *	Serversidan av spelet kommer använda sig av en port för att
 *  lyssna på en klient. Denna är en aningens hackad för att ge vissa
 *  fördelar.
 *
 * 	@author Philip Ekholm
 *  @author Mia Persson (förmodad)
 *  @author Aron Polner
 * 	2017-03-26
 */

public class GameServer extends JFrame implements ActionListener, Runnable{
	private static final long serialVersionUID = 1L;
	private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private JButton rockBtn = new JButton("Sten");
    private JButton sciBtn = new JButton("Sax");
    private JButton bagBtn = new JButton("Påse"); 
    private JLabel messLbl = new JLabel();
    private JLabel charLbl = new JLabel("R");
    private JPanel buttonPanel = new JPanel();
    private GameProtocol gp = new GameProtocol();
    private int clientMess = -1, response = -1;
    
    private final int SELECTED_ROCK = 0;
    private final int SELECTED_SCISSORS = 1;
    private final int SELECTED_BAG = 2;
    private final int AWAIT_OPONENT = 3;
    private final int CLIENT_WIN = 4;
    private final int SERVER_WIN = 5;
    private final int EQUAL = 6; 
    private final int GAME_OVER = 7;

    /*
    *   Sätt upp en ny server-socket såväl som tråd. Vi kommer även
    *   hantera uppritandet av fönstret och strömmar via privata metoder.
    */

    public GameServer(int port){
    	try{
    		this.serverSocket = new ServerSocket(port);
    	}
    	catch(IOException e1){
    		e1.printStackTrace();
    	}

        setupWidgets();
        handleClient(); 
        
        new Thread(this).start();
    }

    /*
     * Skapar alla grafiska komponenter.
     */
    private void setupWidgets(){
        this.setTitle("Game server");
        this.setBounds(50, 50, 300, 300);
        this.setLayout(null);
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 300, 250);
        mainPanel.setLayout(new BorderLayout());

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
        JPanel p4 = new JPanel();
        p4.add(charLbl);
        p4.setBounds(300, 10, 30, 30);
        

        rockBtn.addActionListener(this);
        sciBtn.addActionListener(this);
        bagBtn.addActionListener(this);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(messLbl, BorderLayout.SOUTH);
        this.add(mainPanel);
        this.add(p4);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void handleClient(){
        /*
         * Använd ServerSocket objektets metod accept till att vänta på
         * att klient-applikationen ska ansluta sig.
         * Metoden accept returnerar ett socket-objekt, koppla socket-
         * objektets in och utmatningsstr�mmar till ett BufferedReader-
         * objekt respektive ett PrintWriter-objekt.
         */
    	
    	try {
			clientSocket = serverSocket.accept();
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void run(){
        String fromClient;

        try {
            /* Läs från clienten så länge svaret inte är null */  
            while ((fromClient = in.readLine()) != null) {
                clientMess = Integer.parseInt(fromClient);
                handle(clientMess);
                /* Behandla meddelandet från klienten och generera svar */
                response = gp.processClientMessage(clientMess);
                /* Skicka svar till clienten */
                out.println(response);
 
                /* Om poängställningen ändrats, uppdatera label */
                if ((response == CLIENT_WIN) || (response == SERVER_WIN)) {
                    messLbl.setText(gp.getScore());
                /* Om svaret till klienten är GAME_OVER, avsluta */
                } else if (response == GAME_OVER) { 
                    messLbl.setText(gp.getScore() + " --GAME OVER--");
                    break; 
                /* Om klienten ska vänta på serverns val behövs denna 
                   information vid knapptryckningar. återställ variabeln 
                   endast om klienten inte kommer att vänta */
                } else if (response != AWAIT_OPONENT) {
                    response = -1;
                }

                gp.resetServerSelection(); /* Återställ */
                enableButtons();
            }
        
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /*
     * Hanterar knapp-händelser.
     */
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == rockBtn) {
            gp.setServerSelection(SELECTED_ROCK);
            handleButtonEvent(SELECTED_ROCK); 
        } else if (e.getSource() == sciBtn) {
            gp.setServerSelection(SELECTED_SCISSORS);
            handleButtonEvent(SELECTED_SCISSORS);
        } else if (e.getSource() == bagBtn) {
            gp.setServerSelection(SELECTED_BAG);
            handleButtonEvent(SELECTED_BAG);
        } 
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

    /*
     * Används till att skicka meddelanden till klienten
     * vid knapptryckningar.
     * Hanterar implementations detaljer vid knapptryckningar.
     */
    public void handleButtonEvent(int selection){
        if (response == AWAIT_OPONENT) {
            out.println(selection);
            response = gp.processClientMessage(clientMess);
            out.println(response);
            if (gp.getScoreIsNotZero())
                messLbl.setText(gp.getScore());
            gp.resetServerSelection();
            clientMess = -1;
            response = -1;
        } else if (response == -1) {
            disableButtons();
        }

    }

    public void disableButtons(){
        rockBtn.setEnabled(false);
        sciBtn.setEnabled(false);
        bagBtn.setEnabled(false);
    }

    public void enableButtons(){
        rockBtn.setEnabled(true);
        sciBtn.setEnabled(true);
        bagBtn.setEnabled(true);
    }

   public static void main(String[] args){
       new GameServer(4444);   
    } 
}
