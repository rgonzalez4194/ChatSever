import java.util.ArrayList;
import java.util.Scanner;

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
	
	public GUI(int minSize, int width){
		this.minSize  = minSize;
		this.width    = width;
		this.users    = new ArrayList<String>();
		this.messages = new ArrayList<String>();
	}
	
	public String[] startScreen() {
		
		Scanner scan = new Scanner(System.in);
		
		String[] info  = {"NAME", "HOST", "PORT", "CONTINUE"};
		String[] messages  = {"Your nerdy username is: ", "The cool host IP is: ", "The port-al number is: "};
		String[] login = new String[3];
		
		for(int i=0; i<=login.length; i++){
			String gui = "\n\n\n\n\n\n\n\n\n\n";
			gui += "+----------------"+loopChar('-',width+2)+"+\n";
			gui += "|"+centerString("LOGIN SCREEN",width+18)+"|\n";
			gui += "+----------------"+loopChar('-',width+2)+"+\n";
			gui += "|"+centerString("WELCOME TO ...                                      ",width+18)+"|\n";
			gui += "|"+centerString("    ___________________  ___   ________          __ ",width+18)+"|\n";
			gui += "|"+centerString("  / ___/_  __/ ____/  |/  /  / ____/ /_  ____ _/ / ",width+18)+"|\n";
			gui += "|"+centerString("    \\__ \\ / / / __/ / /"+"\\"+"_| /  / /   / __ \\/ __ `/ __/ ",width+18)+"|\n";
			gui += "|"+centerString(" ___/ // / / /___/ /  / /  / /___/ / / / /_/ / /_  ",width+18)+"|\n";
			gui += "|"+centerString("/____//_/ /_____/_/  /_/   \\____/_/ /_/\\__,_/\\__/  ",width+18)+"|\n";
			gui += "|"+centerString(loopChar('/', 55),width+18)+"|\n";
			gui += "|"+loopChar(' ', width+18)+"|\n";
			if(i > 0){
				for(int j=0; j<i; j++){
					String temp = login[j];
					gui += "|"+centerString(messages[j]+temp+loopChar(' ', 48-(temp.length()+messages[j].length())),width+18)+"|\n";
				}	
			}
			gui += "+----------------"+loopChar('-',width+2)+"+\n";
			gui += "["+ centerString(info[i],12+3) +" ] ";
			
			System.out.print(gui);
			if(i<login.length){
				login[i] = scan.nextLine();
				if(login[i].length()==0 || login[i].length()>12){
					i-=1;
				}
				if(i == 2){
					try {
						int test = Integer.parseInt(login[2]);
					} catch(NumberFormatException e){
						i-=1;
					}
				}
			} else {
				scan.nextLine();
			}
		}
		
		return login;
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
		
		String gui = "\n\n\n\n\n";
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		gui =  gui + "|"+ centerString("USERS",12+3) +" |"+centerString("MESSAGES",width+2)+"|\n";
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		for(int i=0; i<length; i++){
			String user    = (i>users.size()-1) ? (loopChar(' ',12)) : (users.get(i));
			String message = (i>messages.size()-1) ? (loopChar(' ',width)) : (messages.get(i));
			gui += "| "+user + "  | " + message+" |\n";
		}
		gui =  gui + "+---------------+"+loopChar('-',width+2)+"+\n";
		gui =  gui + "["+ centerString(username,12+3) +" ] ";
		
		
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
