package controller;

import model.User;
import model.dao.UserDAO;
import util.Encryption;
import view.LoginView;

public class LoginController {
    private UserDAO userDAO;
    private LoginView loginView;

    LoginController() throws Exception {
        this.userDAO = new UserDAO();
    }

    public User login() throws Exception {
        this.loginView = new LoginView();
        
        LoginView.displayHeader();
        loginView.displayBreadcrumb("Login");

        String email = loginView.displayLoginMenuEmail();

        User user = this.userDAO.readByEmail(email);

        if (user == null) {
            LoginView.displayHeader();
            loginView.displayBreadcrumb("Login");

            loginView.displayMessage("Usuário não encontrado! \nPressione ENTER para continuar...");
            System.in.read();

            return new User();
        }

        String password = loginView.displayLoginMenuPassword();
        String encryptedPassword = Encryption.encryptPassword(password);

        if (user.getHashPassword().compareTo(encryptedPassword) != 0) {
            LoginView.displayHeader();
            loginView.displayBreadcrumb("Login > Confirmação de identidade");

            loginView.displayMessage("Senha incorreta! \nResponda a pergunta secreta.\n");

            loginView.displayMessage("Pergunta: " + user.getSecretQuestion() + "\n");
            String answer = loginView.displayLoginMenuAnswer();

            if (user.getSecretAnswer().compareTo(answer) != 0) {
                LoginView.displayHeader();
                loginView.displayBreadcrumb("Login");

                loginView.displayMessage("Resposta incorreta! \nPressione ENTER para continuar...");
                System.in.read();

                return new User();
            } else {
                LoginView.displayHeader();
                loginView.displayBreadcrumb("Login > Redefinição de senha");

                loginView.displayMessage("Resposta correta! \nDefina uma nova senha.\n");

                String newPassword = loginView.displayLoginMenuPassword();
                user.setHashPassword(Encryption.encryptPassword(newPassword));
                userDAO.update(user);

                LoginView.displayHeader();
                loginView.displayBreadcrumb("Login > Redefinição de senha");
                loginView.displayMessage("Senha redefinida com sucesso! \nPressione ENTER para continuar...");
                System.in.read();

                return new User();
            }
        }

        return user;
    }

    public void register() throws Exception {
        this.loginView = new LoginView();
        
        LoginView.displayHeader();
        loginView.displayBreadcrumb("Cadastro");

        String name = loginView.displayLoginMenuName();
        String email = loginView.displayLoginMenuEmail();
        String password = loginView.displayLoginMenuPassword();
        String question = loginView.displayLoginMenuQuestion();
        String answer = loginView.displayLoginMenuAnswer();

        User newUser = new User(name, email, Encryption.encryptPassword(password), question, answer);
        userDAO.create(newUser);

        LoginView.displayHeader();
        loginView.displayBreadcrumb("Cadastro");

        loginView.displayMessage("Usuário cadastrado com sucesso! \nPressione ENTER para continuar...");    
        System.in.read();
    }
}
