package zohan;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private InnerServerCar car;
	private InnerServerWindow window;
	
	
	public Server(int carport, int windowport){	
		this.car= new InnerServerCar(carport);
		this.window= new InnerServerWindow(windowport);
		
	}
	// fixa threads s� man kan aktivera inreklasserna nadnjsndn
	
	public void handleCarMess(int mess){
	}
	
	public synchronized void windowToCar(String mess){
		car.toCar(mess);
		System.out.println(mess);
	}
	
	public class InnerServerCar implements Runnable {
		private Socket clientSocket = null;
		private ServerSocket serverSocket = null;
		private PrintWriter outCar = null;
		private BufferedReader inCar = null;

		public InnerServerCar(int port) {

			try {
				serverSocket = new ServerSocket(port);

			} catch (IOException e) {
				System.out.println("fel ip bil");
			}
			handleClient();
			new Thread(this).start();

		}

		public void handleClient() {
			try {
				clientSocket = serverSocket.accept();
				outCar = new PrintWriter(clientSocket.getOutputStream(), true);
				inCar = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			} catch (IOException e) {
				System.out.println("homo bil");
			}
		}

		@Override
		public void run() {
			try {
				String mess;
				while((mess=inCar.readLine())!=null){
					System.out.print(mess);
				}
				//handleCarMess(mess);
				outCar.print(mess);
			} catch (IOException e) {
				System.out.println("n�got med window porten");
			}

		}
		public void toCar(String mess){
			outCar.println(mess);
			System.out.println(mess+"outCar.println(mess)");
			
			
			try {
				clientSocket.close();
				inCar.close();
				outCar.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		

	}
	 public class InnerServerWindow implements Runnable{
		 private Socket clientSocket = null;
			private ServerSocket serverSocket = null;
			private PrintWriter outCar = null;
			private BufferedReader inCar = null;
			
			
			
			public InnerServerWindow(int port) {
				try {
					serverSocket = new ServerSocket(port);

				} catch (IOException e) {
					System.out.println("fel ip window");
				}
				handleClient();
				new Thread(this).start();
				
			}
			public void handleClient(){
				try {
					clientSocket= serverSocket.accept();
					outCar= new PrintWriter(clientSocket.getOutputStream(),true);
					inCar= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				} catch (IOException e) {
					System.out.println("n�got med window porten");
				}
			}
			public void run() {
				try {
					String mess;
					while ((mess=inCar.readLine()) !=null ) {
							System.out.println(mess+"jag vet inte");
						windowToCar((mess));
					
					}
				} 
				catch (IOException e) {	
					e.printStackTrace();
				}
				try {
					inCar.close();
					clientSocket.close();
					outCar.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		 
	 }
	 public static void main(String[] args) {
		new Server(4433, 4444);
	}
	
	
	
	
	
}
