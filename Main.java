
public class Main {
	
	public static void main(String[] args) {
		MainIHM mainIHM = new MainIHM();
		UsersModel usersModel = new UsersModel();
		usersModel.addObserver(mainIHM);
		//Main Controller mainController = new MainController();
	}

}
