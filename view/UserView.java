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
        System.out.println("(3) Produtos");
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

    public String displayLoginMenuName() {
        System.out.print("Nome: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginMenuEmail() {
        System.out.print("Email: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginMenuOldPassword() {
        System.out.print("Senha antiga: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginMenuNewPassword() {
        System.out.print("Senha nova: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginMenuQuestion() {
        System.out.print("Pergunta secreta: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayLoginMenuAnswer() {
        System.out.print("Resposta secreta: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }
}
