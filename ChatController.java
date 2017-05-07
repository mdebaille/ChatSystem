
/*
 * Cette classe représente le contrôleur du MVC avec la classe MessageModel et la fenetre d'un chat
 */

public abstract class ChatController {

	protected MainController mainController;
	protected String myPseudo;
	// indique si la fenetre de chat est ouverte ou non
	protected boolean chatActive;					
	protected MessagesModel messagesModel;
	
	public ChatController(MainController mainController, String myPseudo){
			this.mainController = mainController;
			this.myPseudo = myPseudo;
			this.chatActive = false;
			// Création du modèle quand le contrôleur est créé
			this.messagesModel = new MessagesModel();
	}
	
	// Méthode qui varie selon si on envoie un message à un ou plusieurs utilisateurs
	public abstract void sendMessage(Message message);
	
	public void setChatActive(boolean b, ChatIHM ci){
		this.chatActive = b;
		messagesModel.setChatActive(b);
		if (b){
			// si la fenêtre de chat s'ouvre on l'ajoute comme observer du modèle
			addObservertoModel(ci);
		}else{
			// si la fenêtre de chat se ferme on l'enlève de la liste des observers du modèle
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
