/*
 * Classe permettant de lancer le chat system
 */


public class Main {
	
	public static void main(String[] args) {
		// Cr�ation du mod�le contenant la liste des utilisateurs connect�s
		UsersModel usersModel = new UsersModel();
		// Cr�ation du cont�leur sur la fen�tre principale
		MainController mainController = new MainController(usersModel);
		// Cr�ation de la fen�tre principale
		MainIHM mainIHM = new MainIHM(mainController);
		// Ajout des observers du mod�le
		usersModel.addObserver(mainIHM);
		usersModel.addObserver(mainController);
	}

}
