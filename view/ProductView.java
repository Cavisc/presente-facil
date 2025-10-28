// Caminho: view/ProductView.java (ATUALIZADO)
package view;

import java.util.List;
import model.GiftList; // Adicionar import
import model.Product;
import util.InputScanner;

public class ProductView {

    public void displayHeader() { /* ... (sem alterações) ... */ 
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("PresenteFácil 1.0");
        System.out.println("-----------------");
    }
    public void displayBreadcrumb(String path) { /* ... (sem alterações) ... */
        System.out.println("> " + path + "\n");
    }
    public void displayMessage(String message) { /* ... (sem alterações) ... */
        System.out.println(message);
    }
    public String displayProductMenu() { /* ... (sem alterações) ... */
        System.out.println("(1) Buscar produtos por GTIN");
        System.out.println("(2) Listar todos os produtos");
        System.out.println("(3) Cadastrar um novo produto");
        System.out.println("(4) Buscar produtos por nome\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }
    public String displayAllProducts(List<Product> products, int page, int totalPages) { /* ... (sem alterações) ... */
        System.out.println("Página " + page + " de " + totalPages + "\n");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            String status = p.isActive() ? "" : " (INATIVADO)";
            System.out.println("(" + (i + 1) + ") " + p.getName() + status);
        }
        System.out.println("\n(A) Página anterior");
        System.out.println("(P) Próxima página\n");
        System.out.println("(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }

    // MÉTODO ATUALIZADO
    public void displayProductDetails(Product product, List<GiftList> userGiftLists, int otherListsCount) {
        displayHeader();
        displayBreadcrumb("Início > Produtos > " + product.getName());
        
        System.out.println("NOME.......: " + product.getName());
        System.out.println("GTIN-13....: " + product.getGtin13());
        System.out.println("DESCRIÇÃO..: " + product.getDescription());
        
        System.out.println("\nAparece nas minhas listas:");
        if (userGiftLists.isEmpty()) {
            System.out.println("- Nenhuma");
        } else {
            for (GiftList gl : userGiftLists) {
                System.out.println("- " + gl.getName());
            }
        }
        
        System.out.println("\nAparece também em mais " + otherListsCount + " listas de outras pessoas.\n");
    }
    
    public String displayProductDetailsMenu(boolean isActive) { /* ... (sem alterações) ... */
        System.out.println("(1) Alterar os dados do produto");
        if (isActive) {
            System.out.println("(2) Inativar o produto");
        } else {
            System.out.println("(2) Reativar o produto");
        }
        System.out.println("\n(R) Retornar ao menu anterior\n");
        System.out.print("Opção: ");
        return InputScanner.getScanner().nextLine().toUpperCase();
    }
    public String promptForGtin() { /* ... (sem alterações) ... */
        System.out.print("Digite o GTIN-13 (código de barras): ");
        return InputScanner.getScanner().nextLine();
    }
    public String promptForName() { /* ... (sem alterações) ... */
        System.out.print("Digite o nome do produto: ");
        return InputScanner.getScanner().nextLine();
    }
    public String promptForDescription() { /* ... (sem alterações) ... */
        System.out.print("Digite uma Descrição para o produto: ");
        return InputScanner.getScanner().nextLine();
    }
    // Adicione estes métodos dentro da classe ProductView

    public String promptForNewGtin(String oldGtin) {
        System.out.println("GTIN-13 atual: " + oldGtin);
        System.out.print("Digite o novo GTIN-13 (ou pressione ENTER para manter): ");
        return InputScanner.getScanner().nextLine();
    }

    public String promptForNewName(String oldName) {
        System.out.println("Nome atual: " + oldName);
        System.out.print("Digite o novo Nome (ou pressione ENTER para manter): ");
        return InputScanner.getScanner().nextLine();
    }

    public String promptForNewDescription(String oldDescription) {
        System.out.println("Descrição atual: " + oldDescription);
        System.out.print("Digite a nova Descrição (ou pressione ENTER para manter): ");
        return InputScanner.getScanner().nextLine();
    }
}