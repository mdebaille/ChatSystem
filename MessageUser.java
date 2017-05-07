
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/*
 * Classe représentant les messages qui sont envoyés sur le réseau pour notifier q'un utilisateur est connecté ou qu'il se déconnecte
 */

public class MessageUser implements Serializable {
	
	private  String pseudo;
	private  InetAddress IP;
	private  int port;
	public enum typeConnect {
		  CONNECTED,
		  DISCONNECTED;
	}

	private typeConnect etat; 
	
	public MessageUser(String pseudo, InetAddress iP, int port, typeConnect etat) {
		this.pseudo = pseudo;
		IP = iP;
		this.port = port;
		this.etat=etat;
	}

	
	public String getPseudo() {
		return this.pseudo;
	}


	public InetAddress getIP() {
		return IP;
	}



	public int getPort() {
		return port;
	}


	public typeConnect getEtat() {
		return etat;
	}
	
	public static String serializeMessage(MessageUser messageUser){
		return messageUser.getPseudo() + "#" 
				+ messageUser.getIP().getHostAddress() + "#"
				+ messageUser.getPort() + "#" 
				+ messageUser.getEtat();
	}
	
	public static MessageUser deserializeMessage(String message){
		String[] champs = message.split("#");
		try {
			MessageUser.typeConnect etat;
			if(champs[3].equals("CONNECTED")){
				etat = MessageUser.typeConnect.CONNECTED;
			}else{
				etat = MessageUser.typeConnect.DISCONNECTED;
			}
			return new MessageUser(champs[0], InetAddress.getByName(champs[1]), Integer.parseInt(champs[2]), etat);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return new MessageUser(champs[0], null, Integer.parseInt(champs[2]), MessageUser.typeConnect.DISCONNECTED);
		}
	}
}


