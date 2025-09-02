package controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import model.User;
import util.Encryption;
import view.LoginView;

public class LoginController {
    public void login() {
        LoginView loginView = new LoginView();
        
        loginView.displayHeader();
        loginView.displayBreadcrumb("Login");

        String email = loginView.displayLoginMenuEmail();
        String password = loginView.displayLoginMenuPassword();
    }

    public void register() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        LoginView loginView = new LoginView();

        loginView.displayHeader();
        loginView.displayBreadcrumb("Cadastro");

        String name = loginView.displayLoginMenuName();
        String email = loginView.displayLoginMenuEmail();
        String password = loginView.displayLoginMenuPassword();
        String question = loginView.displayLoginMenuQuestion();
        String answer = loginView.displayLoginMenuAnswer();

        User newUser = new User(name, email, Encryption.encryptPassword(password), question, answer);
    }
}
