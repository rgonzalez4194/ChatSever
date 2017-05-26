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

	//Constructor, creates new array of threads 
	public ThreadServer(){
		threads = new ArrayList<ThreadServer>();
	}

	//Super Constructor, Sets socket, tells us our server is running, and passes in current array of threads
	public ThreadServer(Socket s,ArrayList<ThreadServer> threads)
	{
		this.s = s;
		this.running = true;
		this.threads = threads;
	}
	
	//run method runs when you .start a thread
	public void run(){
		try { 
			//Creates new data input and output streams that grab from the socket we have established
			//input data is buffered
			dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
			dos = new DataOutputStream(s.getOutputStream());
			
			//while loop reads messages and sends them to all clients as long as our server is running
			while(running)
			{
				try {
					String line = dis.readUTF();
					sendAll(line);
					//sendMessage(line);
				} catch (IOException e) {
					stop();
					running = false;
				} 
				
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			stop();
		} 
		
		
	}
	
	//closes a thread 
	public void stop()
	{
		threads.remove(threads.indexOf(this));
		try {
			dis.close();
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//sends message, writes to the output stream and then flushes
	public void sendMessage(String line) throws IOException{
		dos.writeUTF(line);
		dos.flush();
	}
	
	//Sends a message to all created threads in the array of threads
	public void sendAll(String message) throws IOException{
		for(int i=0; i<this.threads.size(); i++){
			ThreadServer thread = this.threads.get(i);
			thread.sendMessage(message);
		}
	}
	
	//runs server
	//accepts clients into server
	public void runServer()
	{
		running = true;
		System.out.println("Server running!");
		while(running){
			try {
				//creates new thread 
				//ss.accept() listens for a connection to the server socket and then returns the socket connected to
				//also passes in the current array of threads
				threads.add(new ThreadServer(ss.accept(), threads));
				//starts the newly created thread
				new Thread(threads.get(threads.size()-1)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	//Main method
	public static void main(String[] args)
	{
		//sets port to be used
		int port = Integer.parseInt(args[0]);
		try {
		//creates a server socket using that port 
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//creates and runs server
		ThreadServer server = new ThreadServer();
		server.runServer();
	}
	
}
