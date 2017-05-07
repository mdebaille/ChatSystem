
/*
 * Cette classe repr�sente le contr�leur du MVC avec la classe MessageModel et la fenetre d'un chat
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
			// Cr�ation du mod�le quand le contr�leur est cr��
			this.messagesModel = new MessagesModel();
	}
	
	// M�thode qui varie selon si on envoie un message � un ou plusieurs utilisateurs
	public abstract void sendMessage(Message message);
	
	public void setChatActive(boolean b, ChatIHM ci){
		this.chatActive = b;
		messagesModel.setChatActive(b);
		if (b){
			// si la fen�tre de chat s'ouvre on l'ajoute comme observer du mod�le
			addObservertoModel(ci);
		}else{
			// si la fen�tre de chat se ferme on l'enl�ve de la liste des observers du mod�le
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
