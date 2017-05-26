package p1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class is a server that uses two socketserver. One for a the car-client and the other is for a control-client.  
 * @author johan
 *
 */
public class Server {
	private InnerServerCar car;
	private InnerServerWindow window;
	
	
	
	
	
	public Server(int carport, int windowport){	
		this.car= new InnerServerCar(carport);
		this.window= new InnerServerWindow(windowport);
		
		
		
	}
	/**	
	 * the method work as tunnel as it transport the message from Window(control client) to car-client
	 * @param mess the message it will transport
	 */
	public  void windowToCar(String mess){
		car.toCar(mess);
		System.out.println(mess);
	}
	/**
	 * An inner class connected to a socket-client (car-client). It is made to communicate with the car-client.
	 * @author johan
	 *
	 */
	public class InnerServerCar implements Runnable {
		private Socket clientSocket = null;
		private ServerSocket serverSocket = null;
		private PrintWriter outCar = null;
		private BufferedReader inCar = null;

		/**
		 * create a socket and bond it to to the specific port
		 * @param port the number of the port 
		 */
		public InnerServerCar(int port) {

			try {
				serverSocket = new ServerSocket(port);

			} catch (IOException e) {
				System.out.println("fel ip bil");
			}
			handleClient();
			new Thread(this).start();

		}
		/**
		 * connect clientsocket to serversocket and create outputstream (PrintWriter) and inpustream( BufferReader)
		 */
		public void handleClient() {
			try {
				clientSocket = serverSocket.accept();
				outCar = new PrintWriter(clientSocket.getOutputStream(), true);
				inCar = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
				System.out.println("homo bil");
			}
		}

		/**
		 * the method run start runing the thread 
		 * listen to incoming stream from carclient and print it o the monitor
		 */
		public void run() {
			try {
				String mess;
				while((mess=inCar.readLine())!=null){
					System.out.print(mess);
//					outCar.print(mess);
				}
				//handleCarMess(mess);
				
			} catch (IOException e) {
				System.out.println("n�got med window porten");
			}

		}
		/**
		 * Send stream to carclient.
		 * @param mess String mess is the message to be send send as a stream to carclient 
		 */
		public void toCar(String mess){
			outCar.println(mess);
			System.out.println(mess+"outCar.println(mess)"+"to car");
			
			
//			try {
//				clientSocket.close();
//				inCar.close();
//				outCar.close();
//			} catch (IOException e) {
//				
//				e.printStackTrace();
//			}
		}
		

	}
	/**
	 * 
	 * An inner class connected to a socket-client (Window-client/ controlclient). It is made to communicate with the control-client.
	 * @author Johan
	 *
	 *
	 */
	 public class InnerServerWindow implements Runnable{
		 private Socket clientSocketW = null;
			private ServerSocket serverSocketW = null;
			private PrintWriter outCarW = null;
			private BufferedReader inCarW = null;
			
			
			/**
			 * create a socket and bond it to to the specific port
			 * @param port the number of the port 
			 */
			public InnerServerWindow(int port) {
				try {
					serverSocketW = new ServerSocket(port);

				} catch (IOException e) {
					System.out.println("fel ip window");
				}
				handleClient();
				new Thread(this).start();
				
			}
			/**
			 * connect clientsocket to serversocket and create outputstream (PrintWriter) and inpustream( BufferReader)
			 */
			public void handleClient(){
				try {
					clientSocketW= serverSocketW.accept();
					System.out.print("window e kopplad");
					outCarW= new PrintWriter(clientSocketW.getOutputStream(),true);
					inCarW= new BufferedReader(new InputStreamReader(clientSocketW.getInputStream()));
					
				} catch (IOException e) {
					System.out.println("n�got med window porten");
				}
			}
			/**
			 * the method run start runing the thread 
			 * listen to incoming stream from Controlclient and print it o the monitor before sending it to WindowToCar method
			 * witch transport to carsocket
			 */
			public void run() {
				try {
					String mess;
					while ((mess=inCarW.readLine()) !=null ) {
							System.out.println(mess+"jag vet inte");
						windowToCar((mess));
					
					}
				} 
				catch (IOException e) {	
					e.printStackTrace();
				}
				try {
					inCarW.close();
					clientSocketW.close();
					outCarW.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		 
	 }
	 public static void main(String[] args) {
		new Server(4433, 4444);
	}
	
	
	
	
	
}
