import java.io.BufferedInputStream;
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.ServerSocket;
	import java.net.Socket;
public class Client implements Runnable{
	
	private Socket s; 
	private DataInputStream dis;
	private DataInputStream user;
	private DataOutputStream dos; 
	private boolean running; 
	private String name;

		//Constructor, takes host ip and a port
		public Client(String host, int port)
		{
			running = true;
			try {
				//connects to server
				s = new Socket(host, port);
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
				//starts recieving messages
				receive(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not connect to Server.");
			} 
		}
		
		//Constructor, takes socket
		public Client(Socket socket)
		{
			//sets socket
			this.s = socket;
			running = true;
			try {
			//creates input and output streams 
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());
				//asks for and sets username
				System.out.print("Please enter your username: ");
				name = user.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//run method runs when thread is .start-ed
		//reads input from user and then sends it
		public void run()
		{
			while(running)
			{
				try {
					String line = user.readLine();
					dos.writeUTF(name+": "+line);
					dos.flush(); 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
				
			}
		}
		
		
		//Receives messages from server
		public void receive(){
			//creates a thread to receive incoming messages
			new Thread(new Client(s)).start();
			while(running)
			{
				try {
					System.out.println(dis.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Server has closed.");
					running = false;
					}
				}
			}
		
		
		
		//Main method, accepting ip and port number
		public static void main(String[] args)
		{
			//sets port number
			int port = Integer.parseInt(args[1]);
			//sets host ip
			String host = args[0];
			//creates new client 
			Client client = new Client(host,port); 
		}
		
		
		
		
		
	}


