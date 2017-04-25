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
 * envoie des message user toutes les 2s
 * Met à jour sa liste de user et quand mise à jour -> ajouter dans liste de IHM principale (appel de addUser et removeUser)
 */

public class UsersModel implements ObservableListUsers{
	
	final int refreshDelay = 6000; //6s
	HashMap<InetAddress, InfoUser> listUser;
	ConcurrentHashMap<InetAddress, Integer> listCompteurs;
	Timer timer;
	private ArrayList<ObserverListUsers> listObserver;

	public UsersModel(){
		listUser = new HashMap<InetAddress, InfoUser>();
		listCompteurs = new ConcurrentHashMap<InetAddress, Integer>();
		listObserver = new ArrayList<ObserverListUsers>();
		
		TimerTask update = new TimerTask() {
            public void run() {
            	Iterator<Entry<InetAddress,Integer>> it = listCompteurs.entrySet().iterator();
            	while(it.hasNext()) {
            	      Entry<InetAddress,Integer> entry = it.next();
            	      if(entry.getValue() == 0) {
            	    	  notifyRemoveUser(entry.getKey());
            	    	  it.remove();
            	      }else{
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
			if(!mess.getIP().equals(InetAddress.getLocalHost())){
				if(mess.getEtat() == MessageUser.typeConnect.CONNECTED){
					if(!existInList(mess.getIP())){
						InfoUser info = new InfoUser(mess.getPseudo(), mess.getIP(), mess.getPort());
						notifyNewUser(info);
						listCompteurs.put(mess.getIP(), 1);
					}else{
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
	
}
