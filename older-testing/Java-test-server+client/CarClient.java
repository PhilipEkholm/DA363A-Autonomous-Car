package p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 
 * @author Johan
 * 
 * 
 * A test client that will imitate a car-client to see whether the communication with the server is functional or not.
 * The class have but a single purpose and that is to listen to a specific port and the type the message on the monitor. 
 *
 */
public class CarClient implements Runnable{

	private Socket gSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	
	/**
	 * the constructor is made to creates a stream socket and connects it to the specified port number on the named host.
	 * create to stream handlers, one to receive stream( BufferReader) and the other is to send stream (PrintWriter)
	 * 
	 * @param ipadress the name or ip adress of the host
	 * @param whitch port to connect to
	 */
	public CarClient( String ipadress, int port){
		try {
			gSocket = new Socket(ipadress, port);
			out= new PrintWriter(gSocket.getOutputStream(),true);
			in= new BufferedReader( new InputStreamReader(gSocket.getInputStream()));
			
			out.println("Testing");
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		new Thread(this).start();
	}
	/**
	 * the run method is made to start the thread 
	 * 
	 */
	public void run() {
			String readCar;
			try {
				while ((readCar=in.readLine())!=null) {
					
						System.out.println(readCar);
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	
	
	public static void main(String[] args) {
		new CarClient("127.0.0.1", 4433);
	}
}
