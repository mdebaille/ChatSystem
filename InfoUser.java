import java.net.InetAddress;
	
/* 
 * Classe contenant toutes les informations utiles à propos d'un utilisateur
 */

public class InfoUser {
	
		private  String pseudo;
		private  InetAddress IP;
		private  int port;

		public InfoUser(String pseudo, InetAddress iP, int port) {
			this.pseudo = pseudo;
			IP = iP;
			this.port = port;
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

}
