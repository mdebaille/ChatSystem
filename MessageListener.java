import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageListener extends Thread{

	private DataInputStream is;						// permet de lire les messages recus
	private ByteArrayOutputStream byteOStream;		// pour concatener des chaines d'octets 
	private ChatController chatController;	
	private MessagesModel messagesModel;
	
	public MessageListener(DataInputStream is, ChatController cc, MessagesModel mm){
		this.is= is;
		this.chatController = cc;
		this.messagesModel = mm;
		this.byteOStream = new ByteArrayOutputStream();
	}
	
	public void run(){
		while(true){ 
			try {
				readData();
				if(!chatController.isChatActive()){
					chatController.notifyNewMessage();
				}
			} catch (IOException e) {
				System.out.println("Deconnexion du destinataire");
				return;
			}
		}
	}
	
	private void readData() throws IOException{
		// lecture des differents champs du message
		boolean typeFile = is.readBoolean();
		int size = is.readInt();
		byte[] data = new byte[size];
		is.read(data, 0, size);
		
		// si le message contient un fichier on l'enregistre, sinon on affiche le texte du message
		if(typeFile){
			String textFileReceived = "File received.";
			messagesModel.addMessage(new Message(typeFile, textFileReceived.getBytes().length, textFileReceived.getBytes()));
			
			// enregistrement du fichier sur la machine
			FileOutputStream fos = new FileOutputStream("recu.txt");
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    bos.write(data, 0, size);
		    bos.flush();
		    bos.close();
		}else{
			// mise en forme "pseudoDest: <message>"
			byte[] messageHeader = (chatController.getInfoDest().getPseudo() + ": ").getBytes();
			byteOStream.write(messageHeader);
			byteOStream.write(data);
			messagesModel.addMessage(new Message(typeFile, byteOStream.toByteArray().length, byteOStream.toByteArray()));
			byteOStream.reset();
		}
	}
}
