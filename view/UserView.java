package view;

import util.InputScanner;

public class UserView {
    public static void displayHeader() {
        System.out.println("\nPresenteFácil 1.0");
        System.out.println("-----------------");
    }
    
    public void displayBreadcrumb(String location) {
        System.out.println("> Inicio" + location + "\n");
    }

    public String displayInitialMenu() {
        System.out.println("(1) Meus dados");
        System.out.println("(2) Minhas listas");
        System.out.println("(3) Produtos [TP2]");
        System.out.println("(4) Buscar lista\n");
        System.out.println("(S) Sair\n");

        System.out.print("Opção: ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public String displayUserDataMenu(String name, String email, String secretQuestion, String secretAnswer) {
        displayHeader();
        displayBreadcrumb(" > Meus dados");
        System.out.println("NOME: " + name);
        System.out.println("EMAIL: " + email);
        System.out.println("PERGUNTA SECRETA: " + secretQuestion);
        System.out.println("RESPOSTA SECRETA: " + secretAnswer + "\n");

        System.out.println("(1) Editar dados");
        System.out.println("(2) Excluir conta\n");
        System.out.println("(R) Retornar ao menu anterior\n");

        System.out.print("Opção: ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public String displayConfirmationToDeleteUser() {
        displayHeader();
        displayBreadcrumb(" > Meus dados > Excluir conta");
        System.out.print("Tem certeza que deseja excluir sua conta? (S/N): ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public String displayLoginInputName() {
        System.out.print("Nome: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginInputEmail() {
        System.out.print("Email: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginInputOldPassword() {
        System.out.print("Senha antiga: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginInputNewPassword() {
        System.out.print("Senha nova: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginInputQuestion() {
        System.out.print("Pergunta secreta: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginInputAnswer() {
        System.out.print("Resposta secreta: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }
}
