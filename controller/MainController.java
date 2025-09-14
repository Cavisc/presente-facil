package controller;

import model.User;
import view.LoginView;

public class MainController {
    public void start() throws Exception {
        boolean running = true;

        while (running) {
            LoginController loginController = new LoginController();
            LoginView loginView = new LoginView();
            
            String option = loginView.displayInitialMenu();

            switch (option) {
                case "1":
                    User userLogged = loginController.login();

                    switch (userLogged.getId()) {
                        case -1:
                            break;
                        default:
                            UserController userController = new UserController(userLogged);
                            userController.home();
                            break;
                    }

                    break;
                case "2":
                    loginController.register();
                    break;
                case "S":
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }
}
