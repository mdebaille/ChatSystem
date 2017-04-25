
public class Main {
	
	public static void main(String[] args) {
		UsersModel usersModel = new UsersModel();
		MainController mainController = new MainController(usersModel);
		MainIHM mainIHM = new MainIHM(mainController);
		usersModel.addObserver(mainIHM);
	}

}
