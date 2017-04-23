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
	HashMap<InetAddress, InfoUser> listUser;
	MainIHM ihm;
	ConcurrentHashMap<InetAddress, Integer> listCompteurs;
	Timer timer;
	MainController mainController;
	
	public UsersModel(final MainIHM ihm){
		listUser = new HashMap<InetAddress, InfoUser>();
		this.ihm = ihm;
		listCompteurs = new ConcurrentHashMap<InetAddress, Integer>();
		
		TimerTask update = new TimerTask() {
            public void run() {
            	Iterator<Entry<InetAddress,Integer>> it = listCompteurs.entrySet().iterator();
            	while(it.hasNext()) {
            	      Entry<InetAddress,Integer> entry = it.next();
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
						listCompteurs.put(mess.getIP(), 1);
					}else{
			        	listCompteurs.put(mess.getIP(), listCompteurs.get(mess.getIP())+1);
					}
				}else{
					if(existInList(mess.getIP())){
						removeUser(mess.getIP());
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void removeUser(InetAddress ip){
		InfoUser info = listUser.remove(ip);
    	System.out.println("Suppression de l'utilisateur " + info.getPseudo());
    	ihm.removeUser(info);
    	mainController.removeChatController(info.getIP());
	}
	
	public void addUser(InfoUser info){
		listUser.put(info.getIP(), info);
		ihm.addUser(info);
	}
	
	public InfoUser getUser(InetAddress ip){
		return listUser.get(ip);
	}
	
	public void setMainController(MainController mc){
		this.mainController = mc;
	}
	
	public void notifyNewMessage(InetAddress ip){
		ihm.notifyNewMessage(ip);
	}
	
}
