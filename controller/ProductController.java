// Caminho: controller/ProductController.java (VERSÃO COMPLETA E FINAL)
package controller;

import java.util.ArrayList;
import java.util.List;
import model.GiftList;
import model.Product;
import model.ProductList;
import model.User;
import model.dao.GiftListDAO;
import model.dao.ProductDAO;
import model.dao.ProductListDAO;
import view.ProductView;

public class ProductController {

    private User loggedInUser;
    private ProductView productView;
    private ProductDAO productDAO;
    private ProductListDAO productListDAO;
    private GiftListDAO giftListDAO;

    public ProductController(User user) {
        this.loggedInUser = user;
        this.productView = new ProductView();
        try {
            this.productDAO = new ProductDAO();
            this.productListDAO = new ProductListDAO();
            this.giftListDAO = new GiftListDAO();
        } catch (Exception e) {
            this.productDAO = null;
            this.productListDAO = null;
            this.giftListDAO = null;
            System.err.println("Falha ao instanciar os DAOs no ProductController: " + e.getMessage());
        }
    }

    public void menu() {
        if (productDAO == null || productListDAO == null || giftListDAO == null) {
            productView.displayMessage("ERRO GRAVE: Não foi possível carregar os bancos de dados.\nVoltando ao menu anterior...");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return;
        }

        boolean running = true;
        while (running) {
            productView.displayHeader();
            productView.displayBreadcrumb("Início > Produtos");
            String option = productView.displayProductMenu();

            switch (option) {
                case "1":
                    searchByGtin();
                    break;
                case "2":
                    listAllProducts();
                    break;
                case "3":
                    registerNewProduct();
                    break;
                case "R":
                    running = false;
                    break;
                default:
                    productView.displayMessage("Opção inválida!");
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    break;
            }
        }
    }
    
    private void searchByGtin() {
        productView.displayHeader();
        productView.displayBreadcrumb("Início > Produtos > Buscar por GTIN");
        String gtin = productView.promptForGtin();

        try {
            Product product = productDAO.readByGtin13(gtin);
            if (product != null) {
                productDetailsMenu(product);
            } else {
                productView.displayMessage("\nProduto com GTIN " + gtin + " não encontrado.");
                productView.displayMessage("\nPressione ENTER para continuar...");
                try { System.in.read(); } catch(Exception e) {}
            }
        } catch (Exception e) {
            productView.displayMessage("\nERRO ao buscar produto: " + e.getMessage());
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
        }
    }
    
    private void listAllProducts() {
        try {
            List<Product> allProducts = productDAO.readAll();
            if (allProducts.isEmpty()) {
                productView.displayMessage("Nenhum produto cadastrado.");
                try { Thread.sleep(2000); } catch (InterruptedException e) {}
                return;
            }

            int currentPage = 1;
            int totalPages = (int) Math.ceil((double) allProducts.size() / 10.0);
            boolean running = true;

            while(running) {
                productView.displayHeader();
                productView.displayBreadcrumb("Início > Produtos > Listagem");
                
                int start = (currentPage - 1) * 10;
                int end = Math.min(start + 10, allProducts.size());
                List<Product> pageProducts = allProducts.subList(start, end);

                String choice = productView.displayAllProducts(pageProducts, currentPage, totalPages);

                switch(choice) {
                    case "A":
                        if (currentPage > 1) currentPage--;
                        break;
                    case "P":
                        if (currentPage < totalPages) currentPage++;
                        break;
                    case "R":
                        running = false;
                        break;
                    default:
                        try {
                            int productIndex = Integer.parseInt(choice) - 1;
                            if (productIndex >= 0 && productIndex < pageProducts.size()) {
                                Product selectedProduct = pageProducts.get(productIndex);
                                productDetailsMenu(selectedProduct);
                            } else {
                                productView.displayMessage("Opção inválida!");
                                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                            }
                        } catch (NumberFormatException e) {
                            productView.displayMessage("Opção inválida!");
                            try { Thread.sleep(1000); } catch (InterruptedException ie) {}
                        }
                        break;
                }
            }

        } catch (Exception e) {
            productView.displayMessage("\nERRO: Falha ao listar os produtos do banco de dados: " + e.getMessage());
            try { Thread.sleep(3000); } catch (InterruptedException ie) {}
        }
    }

    private void registerNewProduct() {
        productView.displayHeader();
        productView.displayBreadcrumb("Início > Produtos > Cadastrar Novo Produto");

        String gtin = productView.promptForGtin();
        if (gtin.length() != 13 || !gtin.matches("[0-9]+")) {
            productView.displayMessage("\nGTIN-13 inválido! Deve conter exatamente 13 dígitos numéricos.");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return;
        }
        String name = productView.promptForName();
        if (name.isEmpty()) {
            productView.displayMessage("\nO nome do produto não pode ser vazio.");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return;
        }
        String description = productView.promptForDescription();
        
        try {
            Product newProduct = new Product(gtin, name, description);
            productDAO.create(newProduct);
            productView.displayMessage("\nProduto \"" + name + "\" cadastrado com sucesso!");
        } catch (Exception e) {
            productView.displayMessage("\nERRO AO CADASTRAR: " + e.getMessage());
        }
        productView.displayMessage("\nPressione ENTER para continuar...");
        try { System.in.read(); } catch(Exception e) {}
    }

    private void productDetailsMenu(Product product) throws Exception {
        boolean running = true;
        while(running) {
            List<ProductList> allAssociations = productListDAO.readByProductId(product.getId());
            List<GiftList> userGiftLists = new ArrayList<>();
            int otherListsCount = 0;
            for (ProductList pl : allAssociations) {
                GiftList gl = giftListDAO.read(pl.getIdList());
                if (gl != null) {
                    if (gl.getUserId() == loggedInUser.getId()) {
                        userGiftLists.add(gl);
                    } else {
                        otherListsCount++;
                    }
                }
            }
        
            productView.displayProductDetails(product, userGiftLists, otherListsCount);
            String option = productView.displayProductDetailsMenu(product.isActive());
            
            switch(option) {
                case "1":
                    product = editProduct(product);
                    break;
                case "2":
                    product = toggleProductActivation(product);
                    break;
                case "R":
                    running = false;
                    break;
                default:
                    productView.displayMessage("Opção inválida!");
                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
                    break;
            }
        }
    }

    private Product editProduct(Product product) throws Exception {
        productView.displayHeader();
        productView.displayBreadcrumb("Início > Produtos > " + product.getName() + " > Alterar");

        String newGtin = productView.promptForNewGtin(product.getGtin13());
        if (newGtin.isEmpty()) newGtin = product.getGtin13();
        else if (newGtin.length() != 13 || !newGtin.matches("[0-9]+")) {
            productView.displayMessage("\nGTIN-13 inválido! A alteração foi cancelada.");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return product;
        }
        String newName = productView.promptForNewName(product.getName());
        if (newName.isEmpty()) newName = product.getName();
        String newDescription = productView.promptForNewDescription(product.getDescription());
        if (newDescription.isEmpty()) newDescription = product.getDescription();

        try {
            Product checkProduct = null;
            if (!newGtin.equals(product.getGtin13())) {
                checkProduct = productDAO.readByGtin13(newGtin);
            }
            if (checkProduct != null) {
                productView.displayMessage("\nERRO: O GTIN " + newGtin + " já está sendo utilizado por outro produto.");
            } else {
                product.setGtin13(newGtin);
                product.setName(newName);
                product.setDescription(newDescription);
                productDAO.update(product);
                productView.displayMessage("\nProduto alterado com sucesso!");
            }
        } catch (Exception e) {
            productView.displayMessage("\nERRO ao alterar o produto: " + e.getMessage());
        }
        
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        return product;
    }
    
    private Product toggleProductActivation(Product product) throws Exception {
        boolean currentStatus = product.isActive();
        product.setActive(!currentStatus);
        try {
            productDAO.update(product);
            String message = currentStatus ? "inativado" : "reativado";
            productView.displayMessage("\nProduto " + message + " com sucesso!");
        } catch (Exception e) {
            product.setActive(currentStatus);
            productView.displayMessage("\nERRO ao atualizar o status do produto: " + e.getMessage());
        }
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        return product;
    }
}