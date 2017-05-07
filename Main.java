/*
 * Classe permettant de lancer le chat system
 */


public class Main {
	
	public static void main(String[] args) {
		// Création du modèle contenant la liste des utilisateurs connectés
		UsersModel usersModel = new UsersModel();
		// Création du contôleur sur la fenêtre principale
		MainController mainController = new MainController(usersModel);
		// Création de la fenêtre principale
		MainIHM mainIHM = new MainIHM(mainController);
		// Ajout des observers du modèle
		usersModel.addObserver(mainIHM);
		usersModel.addObserver(mainController);
	}

}
