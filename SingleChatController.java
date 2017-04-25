import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class SingleChatController extends ChatController{

	private Socket socketDest;
	private OutputStream os;					// permet d'envoyer les messages
	private InfoUser infoDest;
	private MessageListener messageListener;	// gere la reception des messages => enregistrement dans la file de messages
	
	public SingleChatController(MainController mc, InfoUser infoDest, Socket socketDest, String myPseudo){
		super(mc, myPseudo);
		try {
			this.socketDest = socketDest;
			this.os = socketDest.getOutputStream();
			this.infoDest = infoDest;
			this.messageListener = new MessageListener(new DataInputStream(socketDest.getInputStream()), this, messagesModel);
			messageListener.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Message message){
		try{
			// serialisation et envoi du message
			byte[] serializedMessage = Message.serializeMessage(message);
			os.write(serializedMessage, 0, serializedMessage.length);
			
			// mise a jour du MessagesModel (ajout du message envoye)
			String messageToSave;
			if(message.isTypeFile()){
				messageToSave = "File sent.";
			}else{
				messageToSave =  myPseudo + ": " + new String(message.getData(), "UTF-8");
			}
			messagesModel.addMessage(new Message(message.isTypeFile(), messageToSave.length(), messageToSave.getBytes()));
		}catch(IOException e){
			String messageToSave = "Echec de l'envoi: " + infoDest.getPseudo() + " est d�connect�(e).";
			messagesModel.addMessage(new Message(false, messageToSave.getBytes().length, messageToSave.getBytes()));
			System.out.println(e.getMessage());
		}
	}
	
	public void notifyNewMessage(){
		mainController.notifyNewMessage(new UserId(infoDest.getIP(), infoDest.getPort()));
	}
	
	public InfoUser getInfoDest(){
		return infoDest;
	}
	
	public Socket getSocketDest(){
		return socketDest;
	}
}
