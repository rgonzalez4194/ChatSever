import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.Port;

// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html

public class ThreadServer implements Runnable{

	private ArrayList<ThreadServer> hosts;
	private ArrayList<Thread>       threads;
	private ArrayList<String>       colors; 
	
	private DataInputStream  dis;
	private DataOutputStream dos;
	
	private static ServerSocket ss; 
	private        Socket       s;
	
	public boolean running; 
	public String  name;

	//Constructor, creates new array of threads 
	public ThreadServer(){
		hosts   = new ArrayList<ThreadServer>();
		threads = new ArrayList<Thread>();
		colors  = new ArrayList<String>();
	}

	//Super Constructor, Sets socket, tells us our server is running, and passes in current array of threads
	public ThreadServer(Socket s, ArrayList<ThreadServer> hosts, ArrayList<Thread> threads, ArrayList<String> colors)
	{
		this.s = s;
		this.running = true;
		this.hosts = hosts;
		this.threads = threads;
		this.colors = colors;
	}
	
	//run method runs when you .start a thread
	public void run(){
		try { 
			//Creates new data input and output streams that grab from the socket we have established
			//input data is buffered
			dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
			dos = new DataOutputStream(s.getOutputStream());
			
			//while loop reads messages and sends them to all clients as long as our server is running
			this.name = dis.readUTF();
			String color = dis.readUTF();
			this.colors.add(color);
			System.out.println("User: "+name+" has joined the Server!");
			while(running)
			{
				try {
					String line = dis.readUTF();
					killThreads();
					System.out.println(line);
					sendAll(line);
				} catch (IOException e) {
					running = false;
					killThreads();
					stop();
				} 
				
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				running = false;
				stop();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		
		
	}
	
	private void killThreads() {
		Iterator<ThreadServer> hostIter   = hosts.iterator();
		Iterator<Thread>       threadIter = threads.iterator();
		while(hostIter.hasNext() && threadIter.hasNext()){
			ThreadServer host   = hostIter.next();
			Thread       thread = threadIter.next();
			if(thread.isAlive() && !host.running){
				System.out.println("User: "+host.name+" has left the Server!");
				hosts.remove(host);
				try {
					host.stop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				threads.remove(thread);
				thread.stop();
			}
		}
	}

	//closes a thread 
	public void stop() throws IOException
	{
		if (dos!=null) dos.close();
		if (dis!=null) dis.close();
		if (s!=null)   s.close();
	}
	
	
	//sends message, writes to the output stream and then flushes
	public void sendMessage(String line) throws IOException{
		dos.writeUTF(line);
		dos.flush();
	}
	
	//Sends a message to all created threads in the array of threads
	public void sendAll(String message) throws IOException{
		for(int i=0; i<this.hosts.size(); i++){
			ThreadServer thread = this.hosts.get(i);
			thread.sendMessage(getColors()); 
			thread.sendMessage(getUsers());
			thread.sendMessage(message);
		}
	}
	
	
	private String getColors()
	{
		String colors  = "";
		boolean first = true;
		for(int i = 0; i > colors.length()-1; i++)
		{	String temp = this.colors.get(i);
			if(first){
				colors = temp;
				first = false;
			} else {
				colors += "," +temp;
			}
		}
		
		return colors; 
	}
	
	private String getUsers() {
		String users  = "";
		boolean first = true;
		
		Iterator<ThreadServer> iter = hosts.iterator();
		while(iter.hasNext()){
			ThreadServer temp = iter.next();
			if(first){
				users = temp.name;
				first = false;
			} else {
				users = users + "," +temp.name;
			}
		}
		
		return users;
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
				hosts.add(new ThreadServer(ss.accept(), hosts, threads, colors));
				//starts the newly created thread
				threads.add(new Thread(hosts.get(hosts.size()-1)));
				System.out.println("Clients running: "+threads.size());
				threads.get(threads.size()-1).start();
			} catch (IOException e) {
				running = false;
				//e.printStackTrace();
			} 
		}
	}
	
	//Main method
	public static void main(String[] args)
	{
		try {
			// set the port to be used
			int port = Integer.parseInt(args[0]);
			//creates a server socket using that port 
			ss = new ServerSocket(port);
			ThreadServer server = new ThreadServer();
			server.runServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch(ArrayIndexOutOfBoundsException a){
			System.out.println("Usage: \njava ThreadServer (port number)");
		}
	}
	
}
