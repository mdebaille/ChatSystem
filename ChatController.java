import java.io.BufferedWriter;



/*
 * envoie des messages quand bouton send cliqué
 * affichage des messages reçu (listener en lien avec ChatSystem)
 */

public class ChatController {

	private BufferedWriter writer;
	private ListenSocket ls;
	
	public ChatController(ListenSocket ls, BufferedWriter writer){
		this.ls = ls;
		this.writer = writer;
	}
	
}
