import java.io.BufferedInputStream;
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.ServerSocket;
	import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable{
	
	private Socket s; 
	private DataInputStream dis;
	private DataInputStream user;
	private DataOutputStream dos; 
	private boolean running; 
	private Thread scanner;
	public static String name;
	private GUI gui;

		//Constructor, takes host ip and a port
		public Client(String host, int port, String name)
		{
			running = true;
			try {
				//connects to server
				s = new Socket(host, port);
				//creates input and output streams 
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());

				
				this.name = name;
				//starts listening for messages
				send(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not connect to Server.");
			} 
		}
		
		// Constructor
		// Takes the socket connected to the server
		public Client(Socket socket)
		{
			try {
				//sets socket
				this.s = socket;
				running = true;
				
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
				
				gui = new GUI(name, 15, 60);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//closes a thread 
		public void stop() throws IOException
		{
			if(!s.isClosed()) s.close();
			if(dos!=null)     dos.close();
			if(user!=null)    user.close();
			if(dis!=null)     dis.close();
			
		}
		
		//reads input from user and then sends it
		public void send()
		{
			//creates a thread to receive incoming messages
			scanner = new Thread(new Client(s));
			scanner.start();
			
			String line = "";
			
			try {
				dos.writeUTF(name);
				dos.flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			while(running)
			{
				try {
					if((line = user.readLine())!=null){;
						dos.writeUTF(name+": "+line);
						dos.flush(); 
					}
				} catch (IOException e) {
					System.out.println("Server has closed.");
					try {
						scanner.stop();
						stop();
						running = false;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		}
		
		//run method runs when thread is .start-ed
		//Receives messages from server
		public void run(){
			
			gui.updateUsers(name);
			gui.printGUI();
			
			while(running)
			{
				try {
					String users = dis.readUTF();
					gui.updateUsers(users);
					//System.out.println(users);
					
					String message = dis.readUTF();
					gui.updateMessages(message);
					//System.out.println(message);
					
					gui.printGUI();
					//System.out.println(dis.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					try {
						stop();
						running = false;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
}