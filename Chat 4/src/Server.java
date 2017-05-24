import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html

public class Server implements Runnable{
private Socket s; 
private ServerSocket ss; 
private DataInputStream dis;
private DataOutputStream dos; 
private boolean running;  

	public Server(int port)
	{
		try {
			
			ss = new ServerSocket(port);
			//accept a client to the server, opens a new socket
			s = ss.accept();
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
		
		dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
		dos = new DataOutputStream(s.getOutputStream()); 
		
	}
	
	public void run()
	{
		Thread thread = new Thread(this); 
		running = true;
		while(running)
		{
			try {
				String line = dis.readUTF();
				dos.writeUTF(line);
				dos.flush(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args)
	{
	Server server = new Server(4200); 
	}
	
	
	
	
	
}
