import java.io.BufferedInputStream;
	import java.io.DataInputStream;
	import java.io.DataOutputStream;
	import java.io.IOException;
	import java.net.ServerSocket;
	import java.net.Socket;
import java.util.ArrayList;
public class Client implements Runnable{
	// http://docs.oracle.com/javase/tutorial/networking/sockets/readingWriting.html
	private Socket s; 
	private DataInputStream dis;
	private DataInputStream user;
	private DataOutputStream dos; 
	private boolean running; 
	private Thread scanner;
	public static String name;
	private GUI gui;


		public Client(String host, int port)
		{
			running = true;
			try {
				//connects to server
				s = new Socket(host, port);
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
				gui = new GUI(name, 20, 60);
				receive(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		public Client(Socket socket, String name)
		{
			this.s = socket;
			running = true;
			try {
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());
				this.name = name;
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
					e.printStackTrace();
				}
			}
		}
		
		public static void main(String[] args)
		{
			try {
				int port = Integer.parseInt(args[1]);
				String host = args[0];
				name = args[2];
				Client client = new Client(host,port); 
			} catch(ArrayIndexOutOfBoundsException a){
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