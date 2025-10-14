// Caminho: controller/UserController.java (AJUSTADO)
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
                    // Criei uma variável local para o loop interno para não confundir com o loop
                    // principal
                    String subOption = " ";
                    while (subOption.compareTo("R") != 0) {
                        UserView.displayHeader(); // Adicionado para redesenhar o cabeçalho
                        userView.displayBreadcrumb(" > Meus dados"); // Adicionado para redesenhar o breadcrumb
                        subOption = userView.displayUserDataMenu(this.user.getName(), this.user.getEmail(),
                                this.user.getSecretQuestion(), this.user.getSecretAnswer());

                        switch (subOption) {
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
                                } else {
                                    String newPassword = userView.displayLoginInputNewPassword();
                                    String question = userView.displayLoginInputQuestion();
                                    String answer = userView.displayLoginInputAnswer();

                                    User updatedUser = new User(this.user.getId(), name, email,
                                            Encryption.encryptPassword(newPassword), question, answer);
                                    if (this.userDAO.update(updatedUser)) {
                                        this.user = updatedUser;
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Editar dados");
                                        userView.displayMessage(
                                                "Dados atualizados com sucesso! \nPressione ENTER para continuar...");
                                        System.in.read();
                                    } else {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Editar dados");
                                        userView.displayMessage(
                                                "Erro ao atualizar os dados! \nPressione ENTER para continuar...");
                                        System.in.read();
                                    }
                                }
                                break;
                            case "2": // > Excluir conta
                                String confirmation = userView.displayConfirmationToDeleteUser();
                                if (confirmation.compareTo("S") == 0) {
                                    if (this.userDAO.delete(this.user.getId())) {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Excluir conta");
                                        userView.displayMessage(
                                                "Conta excluída com sucesso! \nPressione ENTER para continuar...");
                                        System.in.read();
                                        return false; // Sai do sistema
                                    } else {
                                        UserView.displayHeader();
                                        userView.displayBreadcrumb(" > Meus dados > Excluir conta");
                                        userView.displayMessage(
                                                "Erro ao excluir a conta! Você possui listas de presentes associadas." +
                                                        "\nExclua estas listas antes de excluir sua conta! \nPressione ENTER para continuar...");
                                        System.in.read();
                                    }
                                }
                                break; // <- BUG CORRIGIDO: Faltava um break aqui
                            case "R": // Retornar para o menu anterior
                                break;
                            default: // Mostra '> Meus dados' novamente
                                userView.displayMessage("Opção inválida!");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                                break;
                        }
                    }
                    break;
                case "2": // > Minhas listas
                    giftListController.home();
                    break;
                case "3": // > Produtos <- LÓGICA ADICIONADA AQUI
                    ProductController productController = new ProductController(this.user);
                    productController.menu();
                    break;
                case "4": // > Buscar lista
                    giftListController.findList();
                    break;
                case "S": // Sair
                    return false; // Retorna false para o MainController, que encerra o loop de login
                default: // Mostra '> Inicio' novamente
                    userView.displayMessage("Opção inválida!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    break;
            }

            UserView.displayHeader();
            userView.displayBreadcrumb("");
            option = userView.displayInitialMenu();
        }
        return true; // Nunca deve chegar aqui se a opção 'S' for tratada
    }
}