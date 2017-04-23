
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessagesModel {
	
	private ConcurrentLinkedQueue<Message> listMessages;
	private ChatIHM chatIHM;
	private ChatController chatController;
	
	public MessagesModel(ChatController cc){
		listMessages = new ConcurrentLinkedQueue<Message>();
		chatIHM = null;
		chatController = cc;
	}
	
	public void setChatIHM(ChatIHM ihm){
		chatIHM = ihm;
		while(!chatController.isChatActive()){}
		Iterator<Message> it = listMessages.iterator();
		while(it.hasNext()){
			Message message = it.next();
			chatIHM.printMessage(new String(message.getData()));
			
		}
	}
	
	public void addMessage(Message message){
		listMessages.add(message);
		if(chatController.isChatActive()){
			chatIHM.printMessage(new String(message.getData()));
		}
	}
	
}
