package view;

import java.time.LocalDate;

import util.DateFormatter;
import util.InputScanner;

public class GiftListView {
    public static void displayHeader() {
        System.out.println("\nPresenteFácil 1.0");
        System.out.println("-----------------");
    }
    
    public void displayBreadcrumb(String location) {
        System.out.println("> Inicio > Minhas listas" + location + "\n");
    }

    public void displayBreadcrumbFind(String location) {
        System.out.println("> Inicio > Procurar lista" + location + "\n");
    }

    public void displayGiftListSummary(int i, String name, LocalDate creationDate) {
        System.out.println("(" + i + ") " + name + " - " + DateFormatter.formatDateToString(creationDate));
    }

    public String displayInitialMenu() {
        System.out.println("\n(N) Nova lista");
        System.out.println("(R) Retornar ao menu anterior\n");

        System.out.print("Opção: ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public String displayGiftListInputName() {
        System.out.print("Nome: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public String displayGiftListInputDescription() {
        System.out.print("Descrição: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public LocalDate displayGiftListInputLimitDate() {
        System.out.print("Data limite (dd/MM/aaaa): ");

        String input = InputScanner.getScanner().nextLine();

        if (input.isEmpty()) return null;

        return DateFormatter.formatStringToDate(input);
    }

    public String displayGiftListInputShareableCode() {
        System.out.println("O código é composto por 10 dígitos!\n");
        System.out.println("(R) Retornar ao menu anterior\n");

        System.out.print("Código: ");

        String input = InputScanner.getScanner().nextLine();

        return input;
    }

    public void displayGiftList(String name, String description, LocalDate creationDate, LocalDate limitDate, String shareableCode) {
        System.out.println("CÓDIGO: " + shareableCode);
        System.out.println("NOME: " + name);
        System.out.println("DESCRIÇÃO: " + description);
        System.out.println("DATA DE CRIAÇÃO: " + DateFormatter.formatDateToString(creationDate));
        if (limitDate != null) System.out.println("DATA LIMITE: " + DateFormatter.formatDateToString(limitDate));
    }

    public String displayGiftListMenu() {
        System.out.println("\n(1) Gerenciar produtos da lista");
        System.out.println("(2) Alterar dados da lista");
        System.out.println("(3) Excluir lista\n");
        System.out.println("(R) Retornar ao menu anterior\n");

        System.out.print("Opção: ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public String displayConfirmationToDeleteGiftList(String name) {
        displayHeader();
        displayBreadcrumb(" > " + name + " > Excluir lista");
        System.out.print("Tem certeza que deseja excluir a lista" + name + "? (S/N): ");

        String option = InputScanner.getScanner().nextLine().toUpperCase();

        return option;
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }
}
