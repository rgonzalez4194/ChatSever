import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html

public class ThreadServer implements Runnable{

	private ArrayList<ThreadServer> threads;
	private static ServerSocket ss; 
	DataInputStream  dis;
	DataOutputStream dos ;
	private boolean running;  
	private Socket s;

	public ThreadServer(){
		threads = new ArrayList<ThreadServer>();
	}

	public ThreadServer(Socket s,ArrayList<ThreadServer> threads)
	{
		this.s = s;
		this.running = true;
		this.threads = threads;
	}
	
	public void run(){
		try { 
			dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
			dos = new DataOutputStream(s.getOutputStream());
		
			while(running)
			{
				try {
					String line = dis.readUTF();
					sendAll(line);
					//sendMessage(line);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					running = false;
				} 
				
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			threads.remove(threads.indexOf(this));
		} 
		
		
	}
	
	public void sendMessage(String line) throws IOException{
		dos.writeUTF(line);
		dos.flush();
	}
	
	public void sendAll(String message) throws IOException{
		for(int i=0; i<this.threads.size(); i++){
			ThreadServer thread = this.threads.get(i);
			thread.sendMessage(message);
		}
	}
	
	// Creates new threads
	public void runServer()
	{
		running = true;
		System.out.println("Server running!");
		while(running){
			try {
				threads.add(new ThreadServer(ss.accept(), threads));
				new Thread(threads.get(threads.size()-1)).start();
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
		ThreadServer server = new ThreadServer();
		server.runServer();
	}
	
}
