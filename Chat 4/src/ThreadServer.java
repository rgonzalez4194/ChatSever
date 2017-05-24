import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html

public class ThreadServer implements Runnable{
private static ServerSocket ss; 
private DataInputStream dis;
private DataOutputStream dos; 
private boolean running;  

	public ThreadServer(ServerSocket ss)
	{
		try {
			Socket s = ss.accept();
			DataInputStream dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void run(){
		Socket s;
		try {
			//accept a client to the server, opens a new socket
			s = ss.accept();
			run(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
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
	
	// Creates new threads
	public void runServer()
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
		try {
			ss = new ServerSocket(4200);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ThreadServer server = new ThreadServer(ss);
		server.runServer();
	}
	
	
	
	
	
}
