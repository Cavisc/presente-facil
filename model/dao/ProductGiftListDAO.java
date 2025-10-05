package model.dao;

import java.util.ArrayList;

import model.ProductGiftList;
import model.indexes.BPlusTree;
import model.indexes.pairs.indirect.PairIdId;

public class ProductGiftListDAO extends DAO<ProductGiftList> {
    BPlusTree<PairIdId> indirectIndexProductIdId;
    BPlusTree<PairIdId> indirectIndexGiftListIdId;
    
    ProductGiftListDAO() throws Exception {
        super("productgiftlist", ProductGiftList.class.getConstructor());
        indirectIndexProductIdId = new BPlusTree<>(PairIdId.class.getConstructor(), 3, "./data/product.productgiftlist.id.db");
        indirectIndexGiftListIdId = new BPlusTree<>(PairIdId.class.getConstructor(), 3, "./data/giftlist.productgiftlist.id.db");
    }

    public int create(ProductGiftList productGiftList) throws Exception {
        int id = super.create(productGiftList);
        indirectIndexProductIdId.create(new PairIdId(productGiftList.getProductId(), id));
        indirectIndexGiftListIdId.create(new PairIdId(productGiftList.getGiftListId(), id));
        return id;
    }

    public ProductGiftList[] readByProductId(int productId) throws Exception {
        ArrayList<PairIdId> pairs = indirectIndexProductIdId.read(new PairIdId(productId, -1));
        ArrayList<ProductGiftList> nonNullProductGiftLists = new ArrayList<>();

        for (int i = 0; i < pairs.size(); i++) {
            ProductGiftList productGiftList = super.read(pairs.get(i).getIdAggregate());
            if (productGiftList != null) {
                nonNullProductGiftLists.add(productGiftList);
            }
        }

        return nonNullProductGiftLists.toArray(new ProductGiftList[0]);
    }

    public ProductGiftList[] readByGiftListId(int giftListId) throws Exception {
        ArrayList<PairIdId> pairs = indirectIndexGiftListIdId.read(new PairIdId(giftListId, -1));
        ArrayList<ProductGiftList> nonNullProductGiftLists = new ArrayList<>();

        for (int i = 0; i < pairs.size(); i++) {
            ProductGiftList productGiftList = super.read(pairs.get(i).getIdAggregate());
            if (productGiftList != null) {
                nonNullProductGiftLists.add(productGiftList);
            }
        }

        return nonNullProductGiftLists.toArray(new ProductGiftList[0]);
    }

    public boolean update(ProductGiftList newProductGiftList) throws Exception {
        ProductGiftList oldProductGiftList = super.read(newProductGiftList.getId());
        int oldProductId = oldProductGiftList.getProductId();
        int oldGiftListId = oldProductGiftList.getGiftListId();

        if (super.update(newProductGiftList)) {
            if (newProductGiftList.getProductId() != oldProductId) {
                this.indirectIndexProductIdId.delete(new PairIdId(oldProductId, newProductGiftList.getId()));
                this.indirectIndexProductIdId.create(new PairIdId(newProductGiftList.getProductId(), newProductGiftList.getId()));
            }

            if (newProductGiftList.getGiftListId() != oldGiftListId) {
                this.indirectIndexGiftListIdId.delete(new PairIdId(oldGiftListId, newProductGiftList.getId()));
                this.indirectIndexGiftListIdId.create(new PairIdId(newProductGiftList.getGiftListId(), newProductGiftList.getId()));
            }

            return true;
        }
        else return false;
    }

    public boolean deleteByProductId(int productId) throws Exception {
        ProductGiftList[] productGiftLists = readByProductId(productId);
        boolean allDeleted = true;

        for (ProductGiftList pgl : productGiftLists) {
            if (super.delete(pgl.getId())) {
                allDeleted = allDeleted && indirectIndexProductIdId.delete(new PairIdId(productId, pgl.getId()));
                allDeleted = allDeleted && indirectIndexGiftListIdId.delete(new PairIdId(pgl.getGiftListId(), pgl.getId()));
            } else {
                allDeleted = false;
            }
        }

        return allDeleted;
    }

    public boolean deleteByGiftListId(int giftListId) throws Exception {
        ProductGiftList[] productGiftLists = readByGiftListId(giftListId);
        boolean allDeleted = true;

        for (ProductGiftList pgl : productGiftLists) {
            if (super.delete(pgl.getId())) {
                allDeleted = allDeleted && indirectIndexGiftListIdId.delete(new PairIdId(giftListId, pgl.getId()));
                allDeleted = allDeleted && indirectIndexProductIdId.delete(new PairIdId(pgl.getProductId(), pgl.getId()));
            } else {
                allDeleted = false;
            }
        }

        return allDeleted;
    }
}
