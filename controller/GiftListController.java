package controller;

import java.time.LocalDate;

import model.GiftList;
import model.dao.GiftListDAO;
import view.GiftListView;

public class GiftListController {
    private int userId;
    private GiftListDAO giftListDAO;
    private GiftList[] giftLists;
    private GiftListView giftListView;

    GiftListController(int userId) throws Exception {
        this.userId = userId;
        this.giftListDAO = new GiftListDAO();
        this.giftLists = new GiftList[0];
        this.giftListView = new GiftListView();
    }

    public void home() throws Exception {
        GiftListView.displayHeader();
        giftListView.displayBreadcrumb("");

        giftLists = giftListDAO.readByUserIdTheName(userId);

        if (giftLists.length > 0) {
            System.out.println("LISTAS");
            for (int i = 0; i < giftLists.length; i++) {
                giftListView.displayGiftListSummary(i + 1, giftLists[i].getName(), giftLists[i].getCreationDate());
            }
        }
        else giftListView.displayMessage("NENHUMA LISTA ENCONTRADA\n");

        String option = giftListView.displayInitialMenu();

        // > Inicio > Minhas listas
        while (option.compareTo("R") != 0) {
            switch (option) {
                case "N": // Nova lista
                    GiftListView.displayHeader();
                    giftListView.displayBreadcrumb(" > Nova lista");

                    String name = giftListView.displayGiftListInputName();
                    String description = giftListView.displayGiftListInputDescription();
                    LocalDate limitDate = giftListView.displayGiftListInputLimitDate();
                    
                    GiftList newGiftList = new GiftList(userId, name, description, limitDate);

                    giftListDAO.create(newGiftList, userId);

                    GiftListView.displayHeader();
                    giftListView.displayBreadcrumb(" > Nova lista");
                    giftListView.displayMessage("Lista criada com sucesso! \nPressione ENTER para continuar...");
                    System.in.read();

                    break;
                case "R": // Retornar para '> Inicio'
                    option = "R";
                    break;
                default: // Selecionar lista
                    if (option.matches("[0-9]+")) {
                        if (Integer.parseInt(option) > 0 && Integer.parseInt(option) <= giftLists.length) {
                            while (option.compareTo("R") != 0) {
                                int i = Integer.parseInt(option) - 1;

                                GiftList selectedGiftList = giftLists[i];

                                GiftListView.displayHeader();
                                giftListView.displayBreadcrumb(" > " + selectedGiftList.getName());

                                giftListView.displayGiftList(selectedGiftList.getName(), selectedGiftList.getDescription(), 
                                                            selectedGiftList.getCreationDate(), selectedGiftList.getLimitDate(), 
                                                            selectedGiftList.getShareableCode());

                                option = giftListView.displayGiftListMenu();

                                switch (option) {
                                    case "1": // > Gerenciar produtos da lista
                                        break;
                                    case "2": // > Editar lista
                                        GiftListView.displayHeader();
                                        giftListView.displayBreadcrumb(" > " + selectedGiftList.getName() + " > Editar lista");

                                        String newName = giftListView.displayGiftListInputName();
                                        String newDescription = giftListView.displayGiftListInputDescription();
                                        LocalDate newLimitDate = giftListView.displayGiftListInputLimitDate();

                                        GiftList updateGiftList = new GiftList(selectedGiftList.getId(), userId, newName, newDescription, newLimitDate);
                                        if (this.giftListDAO.update(updateGiftList)) {
                                            giftLists[i] = updateGiftList;

                                            GiftListView.displayHeader();
                                            giftListView.displayBreadcrumb(" > " + selectedGiftList.getName() + " > Editar lista");

                                            giftListView.displayMessage("Lista atualizada com sucesso! \nPressione ENTER para continuar...");
                                            System.in.read();

                                            option = "" + (i + 1) + "";
                                        }

                                        break;
                                    case "3": // > Excluir lista
                                        option = giftListView.displayConfirmationToDeleteGiftList(selectedGiftList.getName());
                                        if (option.compareTo("S") == 0) {
                                            if (this.giftListDAO.delete(selectedGiftList.getId())) {
                                                GiftListView.displayHeader();
                                                giftListView.displayBreadcrumb(" > " + selectedGiftList.getName() + " > Excluir lista");
                                                giftListView.displayMessage("Lista excluída com sucesso! \nPressione ENTER para continuar...");
                                                System.in.read();

                                                option = "R";
                                            }
                                            else {
                                                GiftListView.displayHeader();
                                                giftListView.displayBreadcrumb(" > " + selectedGiftList.getName() + " > Excluir lista");
                                                giftListView.displayMessage("Erro ao excluir a lista! \nPressione ENTER para continuar...");
                                                System.in.read();

                                                option = "" + (i + 1) + "";
                                            }
                                        }
                                        else option = "" + (i + 1) + "";

                                        break;
                                    case "R": // Retornar para '> Inicio > Minhas listas'
                                        break;
                                    default: // Mostra a lista novamente
                                        break;
                                }
                            }
                        }
                    }

                    break;
            }

            GiftListView.displayHeader();
            giftListView.displayBreadcrumb("");
            giftListView.displayMessage("LISTAS");

            giftLists = giftListDAO.readByUserIdTheName(userId);

            if (giftLists.length > 0) {
                for (int i = 0; i < giftLists.length; i++) {
                    giftListView.displayGiftListSummary(i + 1, giftLists[i].getName(), giftLists[i].getCreationDate());
                }
            }
            else giftListView.displayMessage("NENHUMA LISTA ENCONTRADA\n");

            option = giftListView.displayInitialMenu();
        }


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

                    giftListView.displayGiftList(giftList.getName(), giftList.getDescription(), 
                                                giftList.getCreationDate(), giftList.getLimitDate(), 
                                                giftList.getShareableCode());

                    giftListView.displayMessage("\nPressione ENTER para continuar...");
                    System.in.read();
                }
            }

            GiftListView.displayHeader();
            giftListView.displayBreadcrumbFind("");

            option = giftListView.displayGiftListInputShareableCode();
        }
    }
}
