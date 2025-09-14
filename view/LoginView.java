package view;

import util.InputScanner;

public class LoginView {
    public static void displayHeader() {
        System.out.println("\nPresenteFácil 1.0");
        System.out.println("-----------------");
    }
    
    public void displayBreadcrumb(String location) {
        System.out.println("> " + location + "\n");
    }

    public String displayInitialMenu() {
        displayHeader();
        System.out.println("\n(1) Login");
        System.out.println("(2) Novo usuário\n");
        System.out.println("(S) Sair\n");
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

    public String displayLoginMenuPassword() {
        System.out.print("Senha: ");

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
