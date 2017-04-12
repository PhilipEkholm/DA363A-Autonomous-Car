package sten;

import java.io.*;
import java.net.*;

class TCPServer{
	private Socket connectionSocket;
	private Socket clientSocket;
	
	public static void main(String args[]) throws Exception{
		new TCPServer();
	}
	
	public TCPServer() throws Exception{
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(4443);
		
		new Thread(new SendArduino()).start();
		
		Thread.sleep(1000);

		while(true){
			connectionSocket = welcomeSocket.accept();
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			DataOutputStream outToArduino = new DataOutputStream(clientSocket.getOutputStream());
			clientSentence = inFromUser.readLine();
			
			System.out.println("Received: " + clientSentence);
			
			outToArduino.writeBytes(clientSentence);
			outToClient.writeBytes("trying anything by now");
		}
	}
	
	private class SendArduino implements Runnable{
		@Override
		public void run(){
			try {
				this.startComms();
			} catch (Exception e) {
				
			}
		}
		
		public void startComms() throws Exception{
			TCPServer outerRef = TCPServer.this;
			
			while(true){
				String sentence = "";
				String modifiedSentence = "";

				BufferedReader inFromMainFrame = new BufferedReader( new InputStreamReader(outerRef.connectionSocket.getInputStream()));
				clientSocket = new Socket("192.168.0.19", 4444);
				
				DataOutputStream outToServer;
				outToServer = new DataOutputStream(clientSocket.getOutputStream());
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
				sentence = inFromMainFrame.readLine();
				outToServer.writeBytes(sentence + '\n');
				modifiedSentence = inFromServer.readLine();
				
					
				System.out.println("FROM SERVER: " + modifiedSentence);
				
				clientSocket.close();
			}
		}
		
	}
}