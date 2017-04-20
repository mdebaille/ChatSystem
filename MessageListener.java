import java.io.BufferedReader;
import java.io.IOException;

public class MessageListener extends Thread{

	private BufferedReader reader;
	private ChatController chatController;
	private MessagesModel messagesModel;
	
	public MessageListener(BufferedReader br, ChatController cc, MessagesModel mm){
		reader = br;
		chatController = cc;
		messagesModel = mm;
	}
	
	public void run(){
		String lastLine = "";;
		while(true){ 
			try {
				lastLine = reader.readLine();
				if(!chatController.isChatActive()){
					chatController.notifyNewMessage();
				}
				if(lastLine != null){
					messagesModel.addMessage(new Message(chatController.getInfoDest().getPseudo() + ": " + lastLine));
				}
			} catch (IOException e) {
				return;
			}
		}
	}
}
