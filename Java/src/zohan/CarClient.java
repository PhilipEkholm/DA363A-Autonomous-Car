package zohan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CarClient {
	private Socket g2Socket = null;
	private PrintWriter out2 = null;
	private BufferedReader in2 = null;
	private Socket gSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	public CarClient(String ipadress1, String ipadress, int port, int port2){
		try {
			gSocket = new Socket(ipadress1, port);
			g2Socket = new Socket(ipadress, port2);
			out= new PrintWriter(gSocket.getOutputStream(),true);
			in= new BufferedReader( new InputStreamReader(gSocket.getInputStream()));
			out2= new PrintWriter(g2Socket.getOutputStream(),true);
			in2= new BufferedReader( new InputStreamReader(g2Socket.getInputStream()));
			
			out.println("Testing");
			out2.println("Test 2");
			connectToCarS();
			connectToS();
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	public void connectToCarS(){
		String mess;
		try {
			while((mess=in.readLine())!=null){
				out2.println(mess);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void connectToS(){
		String mess;
		try {
			while((mess=in2.readLine())!=null){
				out.println(mess);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new CarClient("127.0.0.1","192.168.1.7", 4433, 5555);
	}
}
