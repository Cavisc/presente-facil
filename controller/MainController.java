package controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import view.LoginView;

public class MainController {
    public void start() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        boolean running = true;

        while (running) {
            LoginController loginController = new LoginController();
            LoginView loginView = new LoginView();
            
            String option = loginView.displayInitialMenu();

            switch (option) {
                case "1":
                    loginController.login();
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
