public class STEMChat{
	
	public static void main(String[] args)
	{
		GUI gui = new GUI(15,60);
		
		String[] login = gui.startScreen();
		
	    // Try to set port number, server name, and user name
		int port = Integer.parseInt(login[2]);
		String host = login[1];
		String name = (login[0].length()>12) ? login[0].substring(0, 11) : login[0];
		String color = login[3];
		Client client = new Client(host,port,name,color); 
	}
}