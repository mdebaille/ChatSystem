import java.net.InetAddress;
import java.net.UnknownHostException;
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

public class UsersModel{
	
	final int refreshDelay = 6000; //6s
	HashMap<UserId, InfoUser> listUser;
	MainIHM ihm;
	ConcurrentHashMap<UserId, Integer> listCompteurs;
	Timer timer;
	MainController mainController;
	
	public UsersModel(final MainIHM ihm){
		listUser = new HashMap<UserId, InfoUser>();
		this.ihm = ihm;
		listCompteurs = new ConcurrentHashMap<UserId, Integer>();
		
		TimerTask update = new TimerTask() {
            public void run() {
            	Iterator<Entry<UserId,Integer>> it = listCompteurs.entrySet().iterator();
            	while(it.hasNext()) {
            	      Entry<UserId,Integer> entry = it.next();
            	      if(entry.getValue() == 0) {
            	    	  removeUser(entry.getKey());
            	    	  it.remove();
            	      }else{
              			listCompteurs.put(entry.getKey(), 0);
              		} /*
            	    try{
	            	    Thread.sleep(2000);
            	    }catch(InterruptedException e){
            	    	System.out.println(e.getMessage());
            	    }*/
            	}
            }
        };
        
        timer = new Timer();
        timer.schedule(update, refreshDelay, refreshDelay);
	}
	
	public boolean existInList(InetAddress IP){
		/*for(InfoUser info : listUser){
			if(info.getIP().equals(IP)){
				return true;
			}
		}
		return false;*/
		return listUser.containsKey(IP);
	}
	
	public void receivedMessageUser(final MessageUser mess){
		try {
			if(!mess.getIP().equals(InetAddress.getLocalHost())){
				if(mess.getEtat() == MessageUser.typeConnect.CONNECTED){
					if(!existInList(mess.getIP())){
						System.out.println("Ajout de l'utilisateur " + mess.getPseudo());
						InfoUser info = new InfoUser(mess.getPseudo(), mess.getIP(), mess.getPort());
						addUser(info);
						listCompteurs.put(new UserId(mess.getIP(), mess.getPort()), 1);
					}else{
			        	listCompteurs.put(new UserId(mess.getIP(), mess.getPort()), listCompteurs.get(mess.getIP())+1);
					}
				}else{
					if(existInList(mess.getIP())){
						removeUser(new UserId(mess.getIP(), mess.getPort()));
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void removeUser(UserId id){
		InfoUser info = listUser.remove(id);
    	System.out.println("Suppression de l'utilisateur " + info.getPseudo());
    	ihm.removeUser(info);
    	mainController.removeChatController(new UserId(info.getIP(), info.getPort()));
	}
	
	public void addUser(InfoUser info){
		listUser.put(new UserId(info.getIP(), info.getPort()), info);
		ihm.addUser(info);
	}
	
	public InfoUser getUser(UserId id){
		return listUser.get(id);
	}
	
	public void setMainController(MainController mc){
		this.mainController = mc;
	}
	
	public void notifyNewMessage(UserId id){
		ihm.notifyNewMessage(id);
	}
	
}
