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
	HashMap<UserId, InfoUser> listUser;
	ConcurrentHashMap<UserId, Integer> listCompteurs;
	Timer timer;
	private ArrayList<ObserverListUsers> listObserver;

	public UsersModel(){
		listUser = new HashMap<UserId, InfoUser>();
		listCompteurs = new ConcurrentHashMap<UserId, Integer>();
		listObserver = new ArrayList<ObserverListUsers>();
		
		TimerTask update = new TimerTask() {
            public void run() {
            	Iterator<Entry<UserId,Integer>> it = listCompteurs.entrySet().iterator();
            	while(it.hasNext()) {
            	      Entry<UserId,Integer> entry = it.next();
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
	
	public boolean existInList(InetAddress IP){
		return listUser.containsKey(IP);
	}
	
	public void receivedMessageUser(final MessageUser mess){
		try {
			if(!mess.getIP().equals(InetAddress.getLocalHost())){
				if(mess.getEtat() == MessageUser.typeConnect.CONNECTED){
					if(!existInList(mess.getIP())){
						InfoUser info = new InfoUser(mess.getPseudo(), mess.getIP(), mess.getPort());
						notifyNewUser(info);
						listCompteurs.put(new UserId(mess.getIP(), mess.getPort()), 1);
					}else{
			        	listCompteurs.put(new UserId(mess.getIP(), mess.getPort()), listCompteurs.get(mess.getIP())+1);
					}
				}else{
					if(existInList(mess.getIP())){
						notifyRemoveUser(new UserId(mess.getIP(), mess.getPort()));
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void notifyRemoveUser(UserId id){
		InfoUser info = listUser.remove(id);
    	System.out.println("Suppression de l'utilisateur " + info.getPseudo());
    	//mainController.removeChatController(new UserId(info.getIP(), info.getPort()));
    	for(ObserverListUsers obs: listObserver){
    		obs.removeUser(id);
    	}
	}
	
	public void notifyNewUser(InfoUser info){
		listUser.put(new UserId(info.getIP(), info.getPort()), info);
		for(ObserverListUsers obs: listObserver){
    		obs.addUser(info);
    	}
	}
	
	public InfoUser getUser(UserId id){
		return listUser.get(id);
	}
	/*
	public void setMainController(MainController mc){
		this.mainController = mc;
	}
	*/
	public void notifyNewMessage(UserId id){
		for(ObserverListUsers obs: listObserver){
    		obs.newMessage(id);
    	}
	}
	
	public void addObserver(ObserverListUsers obs){
		this.listObserver.add(obs);
	}
	
	public void removeObserver(ObserverListUsers obs){
		this.listObserver.remove(obs);
	}
	
}
