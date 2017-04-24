import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class GroupChatController extends ChatController{
	
	private ArrayList<OutputStream> listOs;
	
	public GroupChatController(MainController mc, ArrayList<Socket> listSocket, String myPseudo){
		super(mc, myPseudo);
		for(Socket s: listSocket){
			try {
				listOs.add(s.getOutputStream());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void sendMessage(Message message){
		try{
			// serialisation et envoi du message
			byte[] serializedMessage = Message.serializeMessage(message);
			for(OutputStream os: listOs){
				os.write(serializedMessage, 0, serializedMessage.length);
			}
			
			// mise a jour du MessagesModel (ajout du message envoye)
			String messageToSave;
			if(message.isTypeFile()){
				messageToSave = "File sent.";
			}else{
				messageToSave =  myPseudo + ": " + new String(message.getData(), "UTF-8");
			}
			messagesModel.addMessage(new Message(message.isTypeFile(), messageToSave.length(), messageToSave.getBytes()));
		}catch(IOException e){
			String messageToSave = "Echec de l'envoi.";
			messagesModel.addMessage(new Message(false, messageToSave.getBytes().length, messageToSave.getBytes()));
			System.out.println(e.getMessage());
		}
	}
}
