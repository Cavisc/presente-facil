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
                case "1": // Login
                    User userLogged = loginController.login();

                    switch (userLogged.getId()) {
                        case -1:
                            break;
                        default:
                            UserController userController = new UserController(userLogged);
                            boolean isLogged = userController.home();

                            if (!isLogged) break;
                    }

                    break;
                case "2": // Novo usuário
                    loginController.register();
                    break;
                case "S": // Sair
                    running = false;
                    break;
                default: // Mostra menu inicial novamente
                    break;
            }
        }
    }
}
