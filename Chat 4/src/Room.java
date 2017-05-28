import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Room implements Runnable{

	private ArrayList<Room> rooms;
	private ArrayList<Thread> users;
	
	private DataInputStream  dis;
	private DataOutputStream dos;
	
	private static ServerSocket ss; 
	private	Socket s;
	
	public boolean running; 
	public String  name;

	//Constructor, creates new array of threads 
	public Room(){
		rooms   = new ArrayList<Room>();
		users = new ArrayList<Thread>();
	}

	//Super Constructor, Sets socket, tells us our room is running, and passes in current array of other rooms, and users in this room
	public Room(Socket s, ArrayList<Room> rooms, ArrayList<Thread> users)
	{
		this.s = s;
		this.running = true;
		this.rooms = rooms;
		this.users = users;
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
		Iterator<Room> hostIter   = rooms.iterator();
		Iterator<Thread>       threadIter = users.iterator();
		while(hostIter.hasNext() && threadIter.hasNext()){
			Room host   = hostIter.next();
			Thread       thread = threadIter.next();
			if(thread.isAlive() && !host.running){
				System.out.println("User: "+host.name+" has left the Server!");
				rooms.remove(host);
				try {
					host.stop();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				users.remove(thread);
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
		for(int i=0; i<this.rooms.size(); i++){
			Room user = this.rooms.get(i);
			user.sendMessage(getUsers());
			user.sendMessage(message);
		}
	}
	
	private String getUsers() {
		String users  = "";
		boolean first = true;
		
		Iterator<Room> iter = rooms.iterator();
		while(iter.hasNext()){
			Room temp = iter.next();
			if(first){
				users = temp.name;
				first = false;
			} else {
				users = users + "," +temp.name;
			}
		}
		
		return users;
	}
	
	//runs Room
	//accepts clients into room
	public void runRoom()
	{
		running = true;
		System.out.println("The room has been created!");
		while(running){
			try {
				//creates new thread 
				//ss.accept() listens for a connection to the server socket and then returns the socket connected to
				//also passes in the current array of threads
				rooms.add(new Room(ss.accept(), rooms, users));
				//starts the newly created thread
				users.add(new Thread(rooms.get(rooms.size()-1)));
				System.out.println("Clients running: "+users.size());
				users.get(users.size()-1).start();
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
