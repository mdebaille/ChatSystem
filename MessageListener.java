import java.io.BufferedReader;
import java.io.IOException;

public class MessageListener extends Thread{

	private BufferedReader reader;
	private ChatController chatController;
	
	public MessageListener(BufferedReader br, ChatController cc){
		reader = br;
		chatController = cc;
	}
	
	public void run(){
		String lastLine = "";;
		while(true){ //condition a preciser...
			try {
				lastLine = reader.readLine();
				chatController.addMessage(lastLine);
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
	}
}
