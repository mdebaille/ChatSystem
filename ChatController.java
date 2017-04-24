
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;



/*
 * envoie des messages quand bouton send cliqué
 * affichage des messages reçu (listener en lien avec ChatSystem)
 * 
 * Notes perso:
 * 
 * Dans ChatIHM: a l'ouverture de la fenetre de chat => lancer la lecture des messages recus (messages recus pendant que chatIHM etait fermee, 
 * et messages qui vont etre recus) => lecture depuis le bufferedreader de chatcontroller avec getLastLine()
 *   Genre:
 *   	String lastline;
 *   	while(true){		 
 *   		lastline = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message ࡬ire
 *   		afficher_le_msg_dans_IHM(lastline); 
 *   	}
 *   (quick note: Tel quel, si on ferme la fenetre de chat et qu'on la re-ouvre plus tard, le chat ne se rappellera pas des messages
 *   qui ont ete affiches avant la fermeture, on re-ouvre une chatIHM vierge ࡣhaque fois. Il y aurait possibilite d'ajouter
 *   une memoire de messages ࡣhaque chatcontroller pour pouvoir suivre les conversations en entier meme apres fermeture des chatIHM.)
 *   
 *  Au moment du clic sur un pseudo de la liste dans MainIHM, appel d'une methode de Chatsystem pour:
 *  => creer et ouvrir une ChatIHM
 *  => creer le socket qui permet de passer le accept() du destinataire, seulement si le ChatController associe ࡣe destinataire n'existe pas deja
 *     (c'est a dire premiere connexion)
 *   		Socket socket = new Socket(ipDest, portDest);
 *   		chatSystem.addChannel(socket);
 *     Ou bien creaton du socket direct apres ajout du user dans la liste ???
 *     
 *  ListenSocket n'est pas utile car ChatController est lui m뮥 le BufferedReader et le thread de ListenSocket sera en realite ChatIHM
 *  
 */

public abstract class ChatController {

	protected MainController mainController;
	protected String myPseudo;
	//private InfoUser infoDest;
	//private OutputStream os; 					// permet d'envoyer les messages
	protected boolean chatActive;					// indique si la fenetre de chat est ouverte ou non
	//private MessageListener messageListener; 	// gere la reception des messages => enregistrement dans la file de messages
	protected MessagesModel messagesModel;
	
	// construit un ChatController pour un chat entre 2 utilisateurs 
	public ChatController(MainController mainController,/*, InfoUser infoDest, Socket socketDest,*/ String myPseudo){
		//try {
			//this.os = socketDest.getOutputStream();
			this.mainController = mainController;
			this.myPseudo = myPseudo;
			//this.infoDest = infoDest;
			this.chatActive = false;
			this.messagesModel = new MessagesModel(this);
			//this.messageListener = new MessageListener(new DataInputStream(socketDest.getInputStream()), this, messagesModel);
			//messageListener.start();
		/*} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	// construit un ChatController pour envoyer des messages a un groupe d'utilisateur
	/*public ChatController(MainController mainController, ArrayList<Socket> listSocket, String myPseudo){
		try {
			for(Socket s: listSocket){
				this.listOs.add(s.getOutputStream());
			}
			this.mainController = mainController;
			this.myPseudo = myPseudo;
			this.infoDest = null;
			this.chatActive = false;
			this.messagesModel = new MessagesModel(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public abstract void sendMessage(Message message);
	
	public void setChatActive(boolean b){
		this.chatActive = b;
	}
	
	public boolean isChatActive(){
		return chatActive;
	}
	
	//public void notifyNewMessage(){}
	
	public void linkModelToIHM(ChatIHM chatIHM){
		messagesModel.setChatIHM(chatIHM);
	}
	/*
	public InfoUser getInfoDest(){
		return infoDest;
	}*/
}
