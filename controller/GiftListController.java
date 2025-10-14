// Caminho: controller/GiftListController.java (VERSÃO COMPLETA E INTEGRADA)
package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.GiftList;
import model.Product;
import model.ProductList;
import model.dao.GiftListDAO;
import model.dao.ProductDAO;
import model.dao.ProductListDAO;
import view.GiftListView;
import view.ProductInListInfo;
import view.ProductView; // Importação necessária

public class GiftListController {
    private int userId;
    private GiftListDAO giftListDAO;
    private GiftList[] giftLists;
    private GiftListView giftListView;

    // --- NOVOS DAOs ADICIONADOS ---
    private ProductDAO productDAO;
    private ProductListDAO productListDAO;

    GiftListController(int userId) throws Exception {
        this.userId = userId;
        this.giftListDAO = new GiftListDAO();
        this.giftLists = new GiftList[0];
        this.giftListView = new GiftListView();

        // --- INICIALIZAÇÃO DOS NOVOS DAOs ---
        this.productDAO = new ProductDAO();
        this.productListDAO = new ProductListDAO();
    }

    public void home() throws Exception {
        String option = "";

        // Loop principal que mostra a lista de listas
        while (!option.equals("R")) {
            GiftListView.displayHeader();
            giftListView.displayBreadcrumb(" > Minhas Listas");

            giftLists = giftListDAO.readByUserIdTheName(userId);

            if (giftLists.length > 0) {
                System.out.println("LISTAS:");
                for (int i = 0; i < giftLists.length; i++) {
                    giftListView.displayGiftListSummary(i + 1, giftLists[i].getName(), giftLists[i].getCreationDate());
                }
            } else {
                giftListView.displayMessage("NENHUMA LISTA ENCONTRADA\n");
            }

            option = giftListView.displayInitialMenu();

            switch (option) {
                case "N": // Nova lista
                    GiftListView.displayHeader();
                    giftListView.displayBreadcrumb(" > Minhas Listas > Nova lista");

                    String name = giftListView.displayGiftListInputName();
                    String description = giftListView.displayGiftListInputDescription();
                    LocalDate limitDate = giftListView.displayGiftListInputLimitDate();

                    GiftList newGiftList = new GiftList(userId, name, description, limitDate);
                    giftListDAO.create(newGiftList, userId);

                    giftListView.displayMessage("Lista criada com sucesso! \nPressione ENTER para continuar...");
                    System.in.read();
                    break;

                case "R": // Retornar
                    break;

                default: // Tenta selecionar uma lista pelo número
                    try {
                        int listIndex = Integer.parseInt(option) - 1;
                        if (listIndex >= 0 && listIndex < giftLists.length) {
                            // Chama o menu de detalhes para a lista selecionada
                            giftListDetailsMenu(giftLists[listIndex]);
                        } else {
                            giftListView.displayMessage("Opção inválida!");
                            Thread.sleep(1000);
                        }
                    } catch (NumberFormatException e) {
                        giftListView.displayMessage("Opção inválida!");
                        Thread.sleep(1000);
                    }
                    break;
            }
        }
    }

    // Menu de detalhes de uma lista específica
    private void giftListDetailsMenu(GiftList selectedGiftList) throws Exception {
        String option = "";
        while (!option.equals("R")) {
            GiftListView.displayHeader();
            giftListView.displayBreadcrumb(" > Minhas Listas > " + selectedGiftList.getName());

            option = giftListView.displayGiftListDetailsMenu(selectedGiftList);

            switch (option) {
                case "1": // > Gerenciar produtos da lista --- LÓGICA INTEGRADA AQUI ---
                    manageProductsInList(selectedGiftList);
                    break;
                case "2": // > Editar lista
                    // Sua lógica de edição de lista
                    break;
                case "3": // > Excluir lista
                    String confirmation = giftListView.displayConfirmationToDeleteGiftList(selectedGiftList.getName());
                    if (confirmation.equalsIgnoreCase("S")) {

                        // --- MUDANÇA CRÍTICA DE INTEGRIDADE ---
                        // 1. Deleta todas as associações de produtos com esta lista
                        this.productListDAO.deleteAllFromList(selectedGiftList.getId());

                        // 2. Deleta a lista em si
                        if (this.giftListDAO.delete(selectedGiftList.getId(), this.userId)) {
                            giftListView.displayMessage(
                                    "Lista e todos os produtos associados foram excluídos com sucesso!");
                            option = "R"; // Força a saída deste menu, já que a lista não existe mais
                        } else {
                            giftListView.displayMessage("Erro ao excluir a lista!");
                        }
                        System.in.read();
                    }
                    break;
                case "R": // Retornar
                    break;
                default:
                    giftListView.displayMessage("Opção inválida!");
                    Thread.sleep(1000);
                    break;
            }
        }
    }

    // --- NOVOS MÉTODOS PARA GERENCIAR PRODUTOS ---

    private void manageProductsInList(GiftList giftList) throws Exception {
        boolean running = true;
        while (running) {
            giftListView.displayHeader();
            giftListView.displayBreadcrumb("Início > Minhas Listas > " + giftList.getName() + " > Produtos");

            List<ProductList> associations = productListDAO.readByListId(giftList.getId());
            List<ProductInListInfo> productInfos = new ArrayList<>();
            for (ProductList pl : associations) {
                Product p = productDAO.read(pl.getIdProduct());
                if (p != null) {
                    productInfos.add(new ProductInListInfo(p.getName(), pl.getQuantity()));
                }
            }

            String option = giftListView.displayProductManagementMenu(productInfos);

            switch (option) {
                case "A":
                    addProductToList(giftList);
                    break;
                case "R":
                    running = false;
                    break;
                default:
                    try {
                        int index = Integer.parseInt(option) - 1;
                        if (index >= 0 && index < associations.size()) {
                            productInListMenu(associations.get(index));
                        } else {
                            giftListView.displayMessage("Opção inválida!");
                            Thread.sleep(1000);
                        }
                    } catch (NumberFormatException e) {
                        giftListView.displayMessage("Opção inválida!");
                        Thread.sleep(1000);
                    }
                    break;
            }
        }
    }

    private void productInListMenu(ProductList association) throws Exception {
        boolean running = true;
        while (running) {
            Product product = productDAO.read(association.getIdProduct());
            if (product == null) {
                giftListView.displayMessage("ERRO: Produto não encontrado. Removendo associação corrompida...");
                productListDAO.delete(association.getId());
                Thread.sleep(2000);
                return;
            }

            giftListView.displayHeader();
            giftListView.displayBreadcrumb("... > Produtos > " + product.getName());
            giftListView.displayProductInListDetails(product, association.getQuantity(), association.getObservations());
            String option = giftListView.displayProductInListDetailsMenu();

            switch (option) {
                case "1": // Alterar quantidade
                    String qtyStr = giftListView.promptForNewQuantity();
                    try {
                        int newQty = Integer.parseInt(qtyStr);
                        if (newQty > 0) {
                            association.setQuantity(newQty);
                            productListDAO.update(association);
                            giftListView.displayMessage("Quantidade alterada com sucesso!");
                        } else {
                            giftListView.displayMessage("A quantidade deve ser maior que zero.");
                        }
                    } catch (NumberFormatException e) {
                        giftListView.displayMessage("Quantidade inválida.");
                    }
                    Thread.sleep(1500);
                    break;
                case "2": // Alterar observações
                    String newObs = giftListView.promptForNewObservations();
                    association.setObservations(newObs);
                    productListDAO.update(association);
                    giftListView.displayMessage("Observações alteradas com sucesso!");
                    Thread.sleep(1500);
                    break;
                case "3": // Remover produto
                    productListDAO.delete(association.getId());
                    giftListView.displayMessage("Produto removido da lista com sucesso!");
                    Thread.sleep(1500);
                    running = false;
                    break;
                case "R":
                    running = false;
                    break;
                default:
                    giftListView.displayMessage("Opção inválida!");
                    Thread.sleep(1000);
                    break;
            }
        }
    }

    private void addProductToList(GiftList giftList) throws Exception {
        boolean running = true;
        while (running) {
            giftListView.displayHeader();
            giftListView.displayBreadcrumb("... > Produtos > Acrescentar produto");
            String option = giftListView.displayAddProductToListMenu();

            switch (option) {
                case "1": // Buscar por GTIN
                    String gtin = new ProductView().promptForGtin();
                    Product p = productDAO.readByGtin13(gtin);
                    if (p != null && p.isActive()) {
                        confirmAndCreateAssociation(giftList, p);
                    } else {
                        giftListView.displayMessage("Produto não encontrado ou inativo.");
                        Thread.sleep(1500);
                    }
                    break;
                case "2": // Listar todos
                    // Aqui entraria a mesma lógica de paginação do ProductController
                    giftListView.displayMessage("Funcionalidade de listar para adicionar ainda não implementada.");
                    Thread.sleep(1500);
                    break;
                case "R":
                    running = false;
                    break;
                default:
                    giftListView.displayMessage("Opção inválida!");
                    Thread.sleep(1000);
                    break;
            }
        }
    }

    private void confirmAndCreateAssociation(GiftList giftList, Product product) throws Exception {
        List<ProductList> currentAssociations = productListDAO.readByListId(giftList.getId());
        for (ProductList pl : currentAssociations) {
            if (pl.getIdProduct() == product.getId()) {
                giftListView.displayMessage("Este produto já está na sua lista.");
                Thread.sleep(2000);
                return;
            }
        }

        String confirmation = giftListView.promptForAddConfirmation(product.getName());
        if (confirmation.equalsIgnoreCase("S")) {
            String qtyStr = giftListView.promptForNewQuantity();
            int qty = 1;
            try {
                int parsedQty = Integer.parseInt(qtyStr);
                if (parsedQty > 0)
                    qty = parsedQty;
            } catch (Exception e) {
                /* Usa 1 como padrão */ }

            String obs = giftListView.promptForNewObservations();

            ProductList newAssociation = new ProductList(giftList.getId(), product.getId(), qty, obs);
            productListDAO.create(newAssociation);
            giftListView.displayMessage("Produto adicionado com sucesso!");
        } else {
            giftListView.displayMessage("Adição cancelada.");
        }
        Thread.sleep(1500);
    }

    public void findList() throws Exception {
        GiftListView.displayHeader();
        giftListView.displayBreadcrumbFind("");

        String option = giftListView.displayGiftListInputShareableCode();

        while (!option.toUpperCase().equals("R")) {
            if (option.length() == 10) {
                GiftList giftList = giftListDAO.readByNanoId(option);

                if (giftList != null) {
                    GiftListView.displayHeader();
                    giftListView.displayBreadcrumbFind(" > " + giftList.getName());

                    // CORREÇÃO: Chamando o novo método de exibição
                    giftListView.displayFoundGiftListDetails(giftList);

                    giftListView.displayMessage("\nPressione ENTER para continuar...");
                    System.in.read();
                } else {
                    giftListView.displayMessage("Lista com o código informado não encontrada.");
                    Thread.sleep(2000);
                }
            }

            GiftListView.displayHeader();
            giftListView.displayBreadcrumbFind("");
            option = giftListView.displayGiftListInputShareableCode();
        }
    }
}