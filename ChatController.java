import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;



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

public class ChatController {

	private BufferedWriter writer;
	private BufferedReader reader;
	private boolean chatActive;	//indique si la fenetre de chat est ouverte ou non
	public ConcurrentLinkedQueue<String> messages; // enregistre les messages qu'on recoit 
	private MessageListener messageListener; // gere la reception des messages => enregistrement dans la file de messages
	
	public ChatController(BufferedReader reader, BufferedWriter writer){
		this.reader = reader;
		this.writer = writer;
		messages = new ConcurrentLinkedQueue<String>();
		messageListener = new MessageListener(reader, this);
		messageListener.start();
	}
	
	public synchronized void sendMessage(String msg){
		try{
			writer.write(msg);
			writer.flush();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public String getLastLine(){
		//on attend de recevoir un message
		while(isChatActive() && messages.isEmpty()){}
		if(!messages.isEmpty() && isChatActive()){
			return messages.poll();
		}else{
			return "";
		}
	}
	
	//m굨ode pour dire ࡍainIHM de notifier l'utilisateur si un nouveau message a 굩 recu (par changement de couleur du bouton ou autre)
	public boolean hasNewMessage(){
		try{
			return reader.ready();
		}catch(IOException e){
			return false;
		}
	}
	
	public synchronized void addMessage(String msg){
		messages.add(msg);
	}
	
	public void setChatActive(boolean b){
		this.chatActive = b;
	}
	
	public boolean isChatActive(){
		return chatActive;
	}
	
}
