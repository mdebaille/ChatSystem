import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.ArrayList;
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
	
	final int delay = 2000; //2s
	//ArrayList<InfoUser> listUser;
	HashMap<InetAddress, InfoUser> listUser;
	MainIHM ihm;
	ConcurrentHashMap<InetAddress, Integer> listCompteurs;
	Timer timer;
	Chatsystem chatSystem;
	
	public UsersModel(final Chatsystem chatSystem){
		//listUser = new ArrayList<InfoUser>();
		listUser = new HashMap<InetAddress, InfoUser>();
		this.chatSystem = chatSystem;
		this.ihm = chatSystem.getMainIHM();
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
        
        TimerTask send = new TimerTask() {
            public void run() {
            	chatSystem.sendMessageUser();
            }
        };
        
        timer = new Timer();
        timer.schedule(update, 3*delay, 3*delay);
        timer.schedule(send, delay, delay);
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
		if(mess.getEtat() == MessageUser.typeConnect.CONNECTED){
			if(!existInList(mess.getIP())){
				System.out.println("Ajout de l'utilisateur " + mess.getPseudo());
				InfoUser info = new InfoUser(mess.getPseudo(), mess.getIP(), mess.getPort());
				addUser(info);
				listCompteurs.put(mess.getIP(), 1);
			}else{
				for(Entry<InetAddress, Integer> entry : listCompteurs.entrySet()){
            		if(entry.getKey().equals(mess.getIP())){
            			listCompteurs.put(entry.getKey(), entry.getValue()+1);
            		}
            	}
			}
		}else{
			if(existInList(mess.getIP())){
				removeUser(mess.getIP());
			}
		}
	}
	
	public void removeUser(InetAddress ip){
		/*
		Iterator<InfoUser> iter = listUser.iterator();
		Boolean found = false;
		InfoUser info = null;
		while (iter.hasNext() && !found) {
		    info = iter.next();
		    if (info.getIP().equals(ip)){
		    	found = true;
		    }
		}*/
		InfoUser info = listUser.remove(ip);
    	System.out.println("Suppression de l'utilisateur " + info.getPseudo());
    	ihm.removeUser(info.getPseudo());
    	chatSystem.removeChannel(info);
	}
	
	public void addUser(InfoUser info){
		listUser.put(info.getIP(), info);
		ihm.addUser(info);
	}
	
	public InfoUser getUser(InetAddress ip){
		return listUser.get(ip);
	}
	
}
