package p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CarClient implements Runnable{

	private Socket gSocket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
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
