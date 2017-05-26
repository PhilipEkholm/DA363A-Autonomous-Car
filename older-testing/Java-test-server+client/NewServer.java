package p1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

/**
 * 
 * @author Johan
 * A test server that Use a different java input stream than the other server. It was made solely for the testing 
 * of the android client as it also use the same data input stream. 
 * The class have but a single purpose and that is to listen to a specific port and then type the message it receives on the monitor the send it out again.
 *
 */
public class NewServer implements Runnable{
	private ServerSocket serverSocket ;
	private Socket clientsocket ;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	
	/**
	 * the constructor is made to create a socket-server and bond it to a specific port. If the port is not the same as the one the client listen to, 
	 * a message is printed in the monitor
	 * If the port is ok a thread is then started 
	 * @param port indicate the port number at with the socket will listen 
	 */
	public NewServer(int port){
		try {
			serverSocket = new ServerSocket(port);

		} catch (IOException e) {
			System.out.println("fel port, no connection");
		}
		handleClient();
		new Thread(this).start();
		
	}
	/**
	 * Connect to the clientsocket to the serversocket 
	 * and create an outputstream and inputstream lines
	 */
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
/**
 * the run method is made to start the thread 
 * 
 */
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
