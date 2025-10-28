// Caminho: view/GiftListView.java (VERSÃO COMPLETA E AJUSTADA)
package view;

import java.time.LocalDate;
import java.util.List;
import model.GiftList;
import model.Product;
import util.DateFormatter;
import util.InputScanner;

public class GiftListView {

    // Mantido como estático, conforme seu original
    public static void displayHeader() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("PresenteFácil 1.0");
        System.out.println("-----------------");
    }

    public void displayBreadcrumb(String location) {
        System.out.println("> Início > Minhas Listas" + location + "\n");
    }

    public void displayBreadcrumbFind(String location) {
        System.out.println("> Início > Procurar lista" + location + "\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayGiftListSummary(int i, String name, LocalDate creationDate) {
        System.out.println("(" + i + ") " + name + " - " + DateFormatter.formatDateToString(creationDate));
    }

    public String displayInitialMenu() {
        System.out.println("\n(N) Nova lista");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public String displayGiftListInputName() {
        System.out.print("Nome: ");
        return InputScanner.getScanner().nextLine();
    }

    public String displayGiftListInputDescription() {
        System.out.print("Descrição: ");
        return InputScanner.getScanner().nextLine();
    }

    public LocalDate displayGiftListInputLimitDate() {
        System.out.print("Data limite (dd/MM/aaaa) ou ENTER para nenhuma: ");
        String input = InputScanner.getScanner().nextLine();
        if (input.isEmpty())
            return null;
        return DateFormatter.formatStringToDate(input);
    }

    public String displayGiftListInputShareableCode() {
        System.out.println("Digite o código da lista (10 caracteres)\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Código: ");
        return InputScanner.getScanner().nextLine();
    }

    public String displayConfirmationToDeleteGiftList(String name) {
        System.out.print("Tem certeza que deseja excluir a lista '" + name + "'? (S/N): ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    // --- MÉTODOS PARA TP2 (GERENCIAMENTO DE PRODUTOS) ---

    // Este método unificado exibe os detalhes da lista E o menu de opções
    public String displayGiftListDetailsMenu(GiftList list) {
        System.out.println("CÓDIGO.....: " + list.getShareableCode());
        System.out.println("NOME.......: " + list.getName());
        System.out.println("DESCRIÇÃO..: " + list.getDescription());
        System.out.println("CRIAÇÃO....: " + DateFormatter.formatDateToString(list.getCreationDate()));
        if (list.getLimitDate() != null) {
            System.out.println("DATA LIMITE: " + DateFormatter.formatDateToString(list.getLimitDate()));
        }
        System.out.println("\n(1) Gerenciar os produtos da lista");
        System.out.println("(2) Alterar os dados da lista");
        System.out.println("(3) Excluir a lista\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public String displayProductManagementMenu(List<ProductInListInfo> products) {
        if (products.isEmpty()) {
            System.out.println("Nenhum produto nesta lista ainda.");
        } else {
            for (int i = 0; i < products.size(); i++) {
                ProductInListInfo info = products.get(i);
                System.out.println("(" + (i + 1) + ") " + info.productName + " (x" + info.quantity + ")");
            }
        }
        System.out.println("\n(A) Acrescentar produto");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public void displayProductInListDetails(Product product, int quantity, String observations) {
        System.out.println("NOME.......: " + product.getName());
        System.out.println("GTIN-13....: " + product.getGtin13());
        System.out.println("DESCRIÇÃO..: " + product.getDescription());
        System.out.println("QUANTIDADE.: " + quantity);
        System.out.println("OBSERVAÇÕES: " + (observations.isEmpty() ? "Nenhuma" : observations));
    }

    public String displayProductInListDetailsMenu() {
        System.out.println("\n(1) Alterar a quantidade");
        System.out.println("(2) Alterar as observações");
        System.out.println("(3) Remover o produto desta lista\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public String displayAddProductToListMenu() {
        System.out.println("(1) Buscar produtos por GTIN");
        System.out.println("(2) Listar todos os produtos");
        System.out.println("(3) Buscar produtos por nome\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public String promptForNewQuantity() {
        System.out.print("Digite a nova quantidade: ");
        return InputScanner.getScanner().nextLine();
    }

    public String promptForNewObservations() {
        System.out.print("Digite as novas observações (ou pressione ENTER para limpar): ");
        return InputScanner.getScanner().nextLine();
    }

    public String promptForAddConfirmation(String productName) {
        System.out.print("Deseja adicionar \"" + productName + "\" a esta lista? (S/N): ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    public void displayFoundGiftListDetails(GiftList list, String ownerName) {
        System.out.println("CÓDIGO.....: " + list.getShareableCode());
        System.out.println("NOME.......: " + list.getName());
        System.out.println("DESCRIÇÃO..: " + list.getDescription());
        System.out.println("CRIAÇÃO....: " + DateFormatter.formatDateToString(list.getCreationDate()));
        if (list.getLimitDate() != null) {
            System.out.println("DATA LIMITE: " + DateFormatter.formatDateToString(list.getLimitDate()));
        }
        System.out.println("CRIADOR....: " + ownerName);
    }
}