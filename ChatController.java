
/*
 * envoie des messages quand bouton send cliqué
 * affichage des messages reçu (listener en lien avec ChatSystem)
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
	protected boolean chatActive;					// indique si la fenetre de chat est ouverte ou non
	protected MessagesModel messagesModel;
	
	// construit un ChatController pour un chat entre 2 utilisateurs 
	public ChatController(MainController mainController, String myPseudo){
			this.mainController = mainController;
			this.myPseudo = myPseudo;
			this.chatActive = false;
			this.messagesModel = new MessagesModel();
	}
	
	public abstract void sendMessage(Message message);
	
	public void setChatActive(boolean b, ChatIHM ci){
		this.chatActive = b;
		messagesModel.setChatActive(b);
		if (b){
			addObservertoModel(ci);
		}else{
			removeObserver(ci);
		}
	}
	
	public boolean getChatActive(){
		return this.chatActive;
	}
	
	public void addObservertoModel(ChatIHM chatIHM){
		messagesModel.addObserver(chatIHM);
	}
	
	public void removeObserver(ChatIHM chatIHM){
		messagesModel.removeObserver(chatIHM);
	}

	public void closeChat(){
		messagesModel.removeAllObservers();
	}
}
