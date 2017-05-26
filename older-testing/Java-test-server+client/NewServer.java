package p1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class NewServer implements Runnable{
	private ServerSocket serverSocket ;
	private Socket clientsocket ;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	
	
	public NewServer(int port){
		try {
			serverSocket = new ServerSocket(port);

		} catch (IOException e) {
			System.out.println("fel port, no connection");
		}
		handleClient();
		new Thread(this).start();
		
	}
	public void handleClient() {
		try {
			clientsocket = serverSocket.accept();
			System.out.println("Connection is made");
			outputStream = new DataOutputStream(clientsocket.getOutputStream());
			inputStream = new DataInputStream(clientsocket.getInputStream());
			System.out.println("efter connection");
		} catch (IOException e) {
			System.out.println("homo bil");
		}
	}

	public void run() {
		
		try {
			
			while (true) {
				System.out.println("here now"); 
				String reString= inputStream.readUTF(); 
                System.out.println("not here now"+reString);
                outputStream.writeUTF(reString);

            }
		} catch (IOException e) {
			System.out.println("fel med readutf");
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args) {
		new NewServer(4444);
	}

}
