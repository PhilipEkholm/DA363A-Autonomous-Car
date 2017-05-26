package p1;


import java.io.*;
import java.net.*;

class Testklient{

	public static void main(String args[]) throws Exception{
		while(true){
			String sentence=" hej";
			String modifiedSentence;
			
			
			DataInputStream fromuser = new DataInputStream(System.in);

			Socket socket = new Socket("127.0.0.1", 4444);
			
			
			DataInputStream infromserver = new DataInputStream(socket.getInputStream());
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			
			
			
			outToServer.writeUTF(sentence + '\n');
			modifiedSentence = infromserver.readUTF();
				
			System.out.println("FROM SERVER: " + modifiedSentence);
			
			socket.close();
		}
	}
}


