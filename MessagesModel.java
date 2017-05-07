import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * Classe contenant les messages reçus
 * Elle notifie la fenêtre de communication quand celle-ci est ouverte
 */

public class MessagesModel implements ObservableMessages {
	
	private ConcurrentLinkedQueue<Message> listMessages;
	private boolean chatActive;
	private ArrayList<ObserverMessages> listObserver;
	
	public MessagesModel(){
		listMessages = new ConcurrentLinkedQueue<Message>();
		listObserver = new ArrayList<ObserverMessages>();
		chatActive = false;
	}
	
	public void addObserver(ObserverMessages obs){
		listObserver.add(obs);
		recordMessages();
	}
	
	public void removeObserver(ObserverMessages obs){
		listObserver.remove(obs);
	}
	
	public void removeAllObservers() {
		for(ObserverMessages obs: listObserver){
			obs.close();
		}
		listObserver.clear();
	}
	
	public void notifyMessage(byte[] message){
		for(ObserverMessages obs : listObserver){
			obs.updateMessage(message);
		}
	}
	
	private void recordMessages(){
		// on notifie les messages qui ont été enregistrés quand le fenêtre de chat n'était pas ouverte
		while(!chatActive){}
		Iterator<Message> it = listMessages.iterator();
		while(it.hasNext()){
			Message message = it.next();
			notifyMessage(message.getData());
		}
	}
	
	public void addMessage(Message message){
		listMessages.add(message);
		notifyMessage(message.getData());
	}
	
	public void setChatActive(boolean b){
		this.chatActive = b;
	}

	public int getModelSize() {
		return listMessages.size();
	}
}
