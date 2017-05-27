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
				//creates input and output streams 
				user = new DataInputStream(System.in);
				dos = new DataOutputStream(s.getOutputStream());

				//starts listening for messages
				send(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not connect to Server.");
			} 
		}
		
		// Constructor
		// Takes the socket connected to the server
		public Client(Socket socket)
		{
			try {
				//sets socket
				this.s = socket;
				running = true;
				
				dis = new DataInputStream(new BufferedInputStream (s.getInputStream()));
				
				gui = new GUI(name, 15, 60);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		//closes a thread 
		public void stop() throws IOException
		{
			if(!s.isClosed()) s.close();
			if(dos!=null)     dos.close();
			if(user!=null)    user.close();
			if(dis!=null)     dis.close();
			
		}
		
		//reads input from user and then sends it
		public void send()
		{
			//creates a thread to receive incoming messages
			scanner = new Thread(new Client(s));
			scanner.start();
			
			String line = "";
			
			try {
				dos.writeUTF(name);
				dos.flush();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			
			while(running)
			{
				try {
					if((line = user.readLine())!=null){;
						dos.writeUTF(name+": "+line);
						dos.flush(); 
					}
				} catch (IOException e) {
					System.out.println("Server has closed.");
					try {
						scanner.stop();
						stop();
						running = false;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		}
		
		//run method runs when thread is .start-ed
		//Receives messages from server
		public void run(){
			
			while(running)
			{
				try {
					String users = dis.readUTF();
					gui.updateUsers(users);
					//System.out.println(users);
					
					String message = dis.readUTF();
					gui.updateMessages(message);
					//System.out.println(message);
					
					gui.printGUI();
					//System.out.println(dis.readUTF());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					try {
						stop();
						running = false;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
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
		this.users    = new ArrayList<String>();
		this.messages = new ArrayList<String>();
	}
	
	public void updateUsers(String users){
		String[] temp = users.split(",");
		for(int i=this.users.size()-1; i>=0; i--){
			this.users.remove(i);
		}
		for(int i=0; i<temp.length; i++){
			if(temp[i].length()>12){
				this.users.add(temp[i].substring(0, 11));
			} else if(temp[i].length()==12){
				this.users.add(temp[i]);
			} else {
				this.users.add(centerString(temp[i], 12));
			}
		}
	}
	
	public void updateMessages(String message){ 
		if(message.length()>width){
			int subdivisions = message.length()/width;
			
			for(int i=0; i<subdivisions; i++){
				this.messages.add(message.substring(i*width, (i+1)*width));
			}
			
			updateMessages(message.substring(subdivisions*width, message.length()-1));
		} else if(message.length()==width){
			this.messages.add(message);
		} else {
			this.messages.add(message+loopChar(' ', width-message.length()));
		}
		
	}
	
	public void printGUI(){
		System.out.print(makeGUI());
	}
	
	private String makeGUI(){
		int length = (users.size()>this.minSize) ? (users.size()):(this.minSize);
		scrollMessages(length);
		
		String gui = "\n";
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		gui =  gui + "|"+ centerString("USERS",12+3) +" |"+centerString("MESSAGES",width+2)+"|\n";
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		for(int i=0; i<length; i++){
			String user    = (i>users.size()-1) ? (loopChar(' ',12)) : (users.get(i));
			String message = (i>messages.size()-1) ? (loopChar(' ',width)) : (messages.get(i));
			gui += "| "+user + "  | " + message+" |\n";
		}
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		gui =  gui + "|"+ centerString("ENTER MESSAGE",12+3) +" | > ";
		
		
		return gui;
	}
	
	private void scrollMessages(int length){
		if(messages.size() > length) {
			int remove = (messages.size()-length > 0) ? (messages.size()-length) : 0;
			for(int i=0; i<remove; i++){
				messages.remove(i);
			}
		}
	}
	
	private String loopChar(char print, int loop){
		String loopChar = "";
		
		for(int i=0; i<loop; i++){
			loopChar = loopChar + print;
		}
		
		return loopChar;
	}
	
	private String centerString(String text, int length){
		boolean even = (text.length()%2==0 && length%2==0);
		if(length>text.length()){
			length = length-text.length();
			String spaces = loopChar(' ', (length-1)/2);
			text = (!even) ? (" "+spaces+text+spaces) : (" "+spaces+text+spaces+" ");

			return text;
		} else {
			return text;
		}
	}
}