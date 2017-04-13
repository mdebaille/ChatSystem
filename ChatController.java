import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;



/*
 * envoie des messages quand bouton send cliquÃ©
 * affichage des messages reÃ§u (listener en lien avec ChatSystem)
 * 
 * Notes perso:
 * 
 * Dans ChatIHM: a l'ouverture de la fenetre de chat => lancer la lecture des messages recus (messages recus pendant que chatIHM etait fermee, 
 * et messages qui vont etre recus) => lecture depuis le bufferedreader de chatcontroller avec getLastLine()
 *   Genre:
 *   	String lastline;
 *   	while(true){		 
 *   		lastline = chatController.getLastLine(); // getLastLine() bloque tant qu'il n'y a pas de nouveau message à lire
 *   		afficher_le_msg_dans_IHM(lastline); 
 *   	}
 *   (quick note: Tel quel, si on ferme la fenetre de chat et qu'on la re-ouvre plus tard, le chat ne se rappellera pas des messages
 *   qui ont ete affiches avant la fermeture, on re-ouvre une chatIHM vierge à chaque fois. Il y aurait possibilite d'ajouter
 *   une memoire de messages à chaque chatcontroller pour pouvoir suivre les conversations en entier meme apres fermeture des chatIHM.)
 *   
 *  Au moment du clic sur un pseudo de la liste dans MainIHM, appel d'une methode de Chatsystem pour:
 *  => creer et ouvrir une ChatIHM
 *  => creer le socket qui permet de passer le accept() du destinataire, seulement si le ChatController associe à ce destinataire n'existe pas deja
 *     (c'est a dire premiere connexion)
 *   		Socket socket = new Socket(ipDest, portDest);
 *   		chatSystem.addChannel(socket);
 *     Ou bien creaton du socket direct apres ajout du user dans la liste ???
 *     
 *  ListenSocket n'est pas utile car ChatController est lui même le BufferedReader et le thread de ListenSocket sera en realite ChatIHM
 *  
 */

public class ChatController {

	private BufferedWriter writer;
	private BufferedReader reader;
		
	public ChatController(BufferedReader reader, BufferedWriter writer){
		this.reader = reader;
		this.writer = writer;
	}
	
	public void sendMessage(String msg){
		try{
			writer.write(msg);
			writer.flush();
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
	public String getLastLine(){
		try{
			return reader.readLine();
		}catch(IOException e){
			return "";
		}
	}
	
	//méthode pour dire à MainIHM de notifier l'utilisateur si un nouveau message a été recu (par changement de couleur du bouton ou autre)
	public boolean hasNewMessage(){
		try{
			return reader.ready();
		}catch(IOException e){
			return false;
		}
	}
	
}
