import java.io.BufferedInputStream;
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.ServerSocket;
	import java.net.Socket;
public class Client implements Runnable{
	// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
	private Socket s; 
	private DataInputStream dis;
	private DataInputStream user;
	private DataOutputStream dos; 
	private boolean running; 
	private String name;


		public Client(String host, int port)
		{
			running = true;
			try {
				//connects to server
				s = new Socket(host, port);
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
				receive(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public Client(Socket socket)
		{
			this.s = socket;
			running = true;
			try {
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());
				System.out.print("Please enter your username: ");
				name = user.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
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
					e.printStackTrace();
				}
				
			}
		}
		
		public void receive(){
			new Thread(new Client(s)).start();
			while(running)
			{
				try {
					System.out.println(dis.readUTF());
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


