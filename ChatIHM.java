import java.awt.GridLayout; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

/*
 * Fenêtre pour communiquer avec un utilisateur
 */

public class ChatIHM {

	String destUsername;
	
	public ChatIHM(String destUsername){
		this.destUsername = destUsername;
		initComponents();
	}

	private void initComponents(){
		
	}
}
