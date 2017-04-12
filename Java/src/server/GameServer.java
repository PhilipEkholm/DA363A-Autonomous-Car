package server;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class GameServer extends JFrame implements Runnable{
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
                System.out.println(fromClient);
            }
        
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(e);
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

