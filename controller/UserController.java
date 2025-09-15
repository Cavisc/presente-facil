package controller;

import model.User;
import model.dao.UserDAO;
import util.Encryption;
import view.UserView;

public class UserController {
    private GiftListController giftListController;
    private UserDAO userDAO;
    private User user;
    private UserView userView;

    UserController(User user) throws Exception {
        this.user = user;
        this.userDAO = new UserDAO();
        this.userView = new UserView();
        this.giftListController = new GiftListController(this.user.getId());
    }

    public boolean home() throws Exception {
        UserView.displayHeader();
        userView.displayBreadcrumb("");

        String option = userView.displayInitialMenu();

        // > Inicio
        while (option.compareTo("S") != 0) {
            switch (option) {
                case "1": // > Meus dados
                while (option.compareTo("R") != 0) {
                    option = userView.displayUserDataMenu(this.user.getName(), this.user.getEmail(), this.user.getSecretQuestion(), this.user.getSecretAnswer());
                    
                        switch (option) {
                            case "1": // > Editar dados
                                UserView.displayHeader();
                                userView.displayBreadcrumb(" > Meus dados > Editar dados");

                                String name = userView.displayLoginInputName();
                                String email = userView.displayLoginInputEmail();
                                String oldPassword = userView.displayLoginInputOldPassword();
                                String encryptedOldPassword = Encryption.encryptPassword(oldPassword);
                                
                                if (user.getHashPassword().compareTo(encryptedOldPassword) != 0) {
                                    UserView.displayHeader();
                                    userView.displayBreadcrumb(" > Meus dados > Editar dados");

                                    userView.displayMessage("Senha incorreta! \nPressione ENTER para continuar...");
                                    System.in.read();

                                    break;
                                }
                                else {
                                    String newPassword = userView.displayLoginInputNewPassword();
                                    String question = userView.displayLoginInputQuestion();
                                    String answer = userView.displayLoginInputAnswer();

                                    User updatedUser = new User(this.user.getId(), name, email, Encryption.encryptPassword(newPassword), question, answer);
                                    if (this.userDAO.update(updatedUser)) {
                                        this.user = updatedUser;

                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Editar dados");

                                        userView.displayMessage("Dados atualizados com sucesso! \nPressione ENTER para continuar...");
                                        System.in.read();

                                        option = "1";
                                    }
                                    else {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Editar dados");

                                        userView.displayMessage("Erro ao atualizar os dados! \nPressione ENTER para continuar...");
                                        System.in.read();
                                    }
                                }

                                break;
                            case "2": // > Excluir conta
                                option = userView.displayConfirmationToDeleteUser();
                                if (option.compareTo("S") == 0) {
                                    if (this.userDAO.delete(this.user.getId())) {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Excluir conta");
                                        userView.displayMessage("Conta excluída com sucesso! \nPressione ENTER para continuar...");
                                        System.in.read();

                                        return false;
                                    }
                                    else {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Excluir conta");
                                        userView.displayMessage("Erro ao excluir a conta! Você possui listas de presentes associadas." + 
                                                                "\nExclua estas listas antes de excluir sua conta! \nPressione ENTER para continuar...");
                                        System.in.read();

                                        option = "1";
                                    }
                                }
                                else option = "1";

                                break;
                            case "R": // Retornar para '> Meus dados'
                                break;
                            default: // Mostra '> Meus dados' novamente
                                break;
                        }
                    }

                    break;    
                case "2": // > Minhas listas
                    giftListController.home();
                    break;
                case "3": // > Produtos
                    break;
                case "4": // > Buscar lista
                    giftListController.findList();
                    break;
                case "S": // Sair
                    return false;
                default: // Mostra '> Inicio' novamente
                    break;
            }

            UserView.displayHeader();
            userView.displayBreadcrumb("");
            option = userView.displayInitialMenu();
        }

        return true;
    }
}
