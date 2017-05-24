import java.io.BufferedInputStream;
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.ServerSocket;
	import java.net.Socket;
public class Client {
	// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
	private Socket s; 
	private DataInputStream dis;
	private DataInputStream user;
	private DataOutputStream dos; 
	private boolean running; 


		public Client(String host, int port)
		{
			try {
				//connects to server
				s = new Socket(host, port);
				init();
				run(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public void init() throws IOException
		{
			//buffering input and output streams
			
			//takes user input
			user = new DataInputStream(System.in);
			dos = new DataOutputStream(s.getOutputStream()); 
			dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
			
		}
		
		public void run()
		{
			running = true;
			while(running)
			{
				try {
					String line = user.readLine();
					dos.writeUTF(line);
					dos.flush(); 
					System.out.println("echo: "+dis.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		public static void main(String[] args)
		{
		Client client = new Client("localhost",4200); 
		}
		
		
		
		
		
	}


