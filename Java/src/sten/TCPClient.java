package sten;

import java.io.*;
import java.net.*;

class TCPClient{

	public static void main(String args[]) throws Exception{
		while(true){
			String sentence = "";
			String modifiedSentence;

			BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
			Socket clientSocket = new Socket("10.0.0.3", 4444);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence + '\n');
			modifiedSentence = inFromServer.readLine();
				
			System.out.println("FROM SERVER: " + modifiedSentence);
			
			clientSocket.close();
		}
	}
}

