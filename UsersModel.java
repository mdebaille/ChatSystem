import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/* 
 * Classe g�rant la liste des utilisateurs connect�s et notifiant MainIHM et MainController d'un changement dans cette liste
 */

public class UsersModel implements ObservableListUsers{
	// d�lai au bout duquel on supprime un utilisateur si on n'a plus re�u de message de connexion de sa part
	final int refreshDelay = 6000; //6s
	HashMap<InetAddress, InfoUser> listUser;
	// Liste des compteurs permettant de supprimer les utilisateurs quand on n'a plus re�u de message de connexion de leur part
	ConcurrentHashMap<InetAddress, Integer> listCompteurs;
	Timer timer;
	private ArrayList<ObserverListUsers> listObserver;

	public UsersModel(){
		listUser = new HashMap<InetAddress, InfoUser>();
		listCompteurs = new ConcurrentHashMap<InetAddress, Integer>();
		listObserver = new ArrayList<ObserverListUsers>();
		
		// Gestion de la suppression des utilisateurs quand ils n'envoient plus de messages de connexion
		TimerTask update = new TimerTask() {
            public void run() {
            	Iterator<Entry<InetAddress,Integer>> it = listCompteurs.entrySet().iterator();
            	while(it.hasNext()) {
            	      Entry<InetAddress,Integer> entry = it.next();
            	      // si on n'a pas re�u de message de connexion de leur part depuis la derni�re ex�cution de ce thread
            	      if(entry.getValue() == 0) {
            	    	  // on supprime l'utilisateur de la liste
            	    	  notifyRemoveUser(entry.getKey());
            	    	  it.remove();
            	      }else{
            	    	 // sinon on remet le nombre de messages re�us � z�ro
              			listCompteurs.put(entry.getKey(), 0);
              		}
            	}
            }
        };
        
        timer = new Timer();
        timer.schedule(update, refreshDelay, refreshDelay);
	}
	
	public boolean existInList(InetAddress ip){
		return listUser.containsKey(ip);
	}
	
	public void receivedMessageUser(final MessageUser mess){
		try {
			// on ne prend en compte les messages que l'on a soit m�me �mis
			if(!mess.getIP().equals(InetAddress.getLocalHost())){
				if(mess.getEtat() == MessageUser.typeConnect.CONNECTED){
					if(!existInList(mess.getIP())){
						// ajout d'un nouvel utilisateur si il n'�tait pas pr�sent dans la liste
						InfoUser info = new InfoUser(mess.getPseudo(), mess.getIP(), mess.getPort());
						notifyNewUser(info);
						listCompteurs.put(mess.getIP(), 1);
					}else{
						// sinon incr�mentation du nombre de messages re�us
			        	listCompteurs.put(mess.getIP(), listCompteurs.get(mess.getIP())+1);
					}
				}else{
					if(existInList(mess.getIP())){
						notifyRemoveUser(mess.getIP());
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyRemoveUser(InetAddress ip){
		InfoUser info = listUser.remove(ip);
		listCompteurs.remove(ip);
    	System.out.println("Suppression de l'utilisateur " + info.getPseudo());
    	for(ObserverListUsers obs: listObserver){
    		obs.removeUser(ip);
    	}
	}
	
	public void notifyNewUser(InfoUser info){
		listUser.put(info.getIP(), info);
		for(ObserverListUsers obs: listObserver){
    		obs.addUser(info);
    	}
	}
	
	public InfoUser getUser(InetAddress ip){
		return listUser.get(ip);
	}

	public void notifyNewMessage(InetAddress ip){
		for(ObserverListUsers obs: listObserver){
    		obs.newMessage(ip);
    	}
	}
	
	public void addObserver(ObserverListUsers obs){
		this.listObserver.add(obs);
	}
	
	public void removeObserver(ObserverListUsers obs){
		this.listObserver.remove(obs);
	}

	public int getModelSize() {
		return listUser.size();
	}
	
}
