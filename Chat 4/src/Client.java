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
		public Client(String host, int port)
		{
			running = true;
			try {
				//connects to server
				s = new Socket(host, port);
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));

				gui = new GUI(name, 20, 60);

				//starts recieving messages
				receive(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not connect to Server.");
			} 
		}
		
		// Constructor
		// Takes the socket connected to the server and the name of the user
		public Client(Socket socket, String name)
		{
			//sets socket
			this.s = socket;
			running = true;
			try {
			//creates input and output streams 
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());
				this.name = name;
				//asks for and sets username
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
			scanner = new Thread(new Client(s, name));
			scanner.start();
			
			while(running)
			{
				try {
					String users = dis.readUTF();
					gui.updateUsers(users);
					//System.out.println(dis.readUTF());
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
		    // Try to set port number, server name, and user name
			try {
				int port = Integer.parseInt(args[1]);
				String host = args[0];
				name = args[2];
				Client client = new Client(host,port); 
			} catch(ArrayIndexOutOfBoundsException a){
				// If these arguments are not provided, tell the user how to use the command
				System.out.println("Usage: \njava Client (hostname) (host port) (username)");
			}
		}
		
}


class GUI {
	
	int               minSize;
	int               width;
	String            username;
	ArrayList<String> users;
	ArrayList<String> messages;
	
	public GUI(String username, int minSize, int width){
		this.username = username;
		this.minSize  = minSize;
		this.width    = width;
	}
	
	public void updateUsers(String users){
		String[] temp = users.split(",");
		this.users.removeAll(this.users);
		for(int i=0; i<temp.length; i++){
			this.users.add(temp[i]);
		}
	}
	
	public void updateMessages(String message){ 
		this.messages.add(message);
	}
	
	public void printGUI(){
		System.out.println(makeGUI());
	}
	
	private String makeGUI(){
		int numUsers = users.size();
		scrollMessages(numUsers);
		
		String gui = "";
		gui += "+-------------------------+---------------------------------------------------------------------+";
		gui += "|          Users          |---------------------------------------------------------------------|";
		gui += "+-------------------------+---------------------------------------------------------------------|";
		
		return gui;
	}
	
	private void scrollMessages(int length){
		length     = (length < minSize) ? minSize : length;
		int remove = (messages.size()-length > 0) ? (messages.size()-length) : 0;
		for(int i=0; i<messages.size(); i++){
			if(i <= remove){
				messages.remove(i);
			}
		}
	}
}