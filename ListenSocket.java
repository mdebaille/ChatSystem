

import java.io.BufferedReader;
import java.io.IOException;

public class ListenSocket extends Thread{
	
	private BufferedReader reader;
	private String lastLine;
	
	public ListenSocket(BufferedReader reader){
		this.reader = reader;
	}
	
	public void run(){
		try{
			lastLine = reader.readLine();
		}catch(IOException e){
			System.out.println("Erreur lors de la lecture du socket");
		}
	}
	
	public String getLastLine(){
		return lastLine;
	}
}
