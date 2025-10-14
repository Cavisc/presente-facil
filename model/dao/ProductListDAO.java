// Caminho: model/dao/ProductListDAO.java
package model.dao;

import java.util.ArrayList;
import java.util.List;
import model.ProductList;
import model.indexes.BPlusTree;
import model.indexes.pairs.indirect.PairIdId;

public class ProductListDAO extends DAO<ProductList> {

    private BPlusTree<PairIdId> indexByListId;
    private BPlusTree<PairIdId> indexByProductId;

    public ProductListDAO() throws Exception {
        super("productlist", ProductList.class.getConstructor());
        indexByListId = new BPlusTree<>(PairIdId.class.getConstructor(), 4, "./data/productlist.list_id.db");
        indexByProductId = new BPlusTree<>(PairIdId.class.getConstructor(), 4, "./data/productlist.product_id.db");
    }

    @Override
    public int create(ProductList productList) throws Exception {
        int id = super.create(productList);
        productList.setId(id);
        indexByListId.create(new PairIdId(productList.getIdList(), id));
        indexByProductId.create(new PairIdId(productList.getIdProduct(), id));
        return id;
    }

    public List<ProductList> readByListId(int listId) throws Exception {
        List<PairIdId> pairs = indexByListId.read(new PairIdId(listId, -1));
        List<ProductList> results = new ArrayList<>();
        for (PairIdId pair : pairs) {
            ProductList pl = super.read(pair.getIdAggregate());
            if (pl != null) {
                results.add(pl);
            }
        }
        return results;
    }
    
    public List<ProductList> readByProductId(int productId) throws Exception {
        List<PairIdId> pairs = indexByProductId.read(new PairIdId(productId, -1));
        List<ProductList> results = new ArrayList<>();
        for (PairIdId pair : pairs) {
            ProductList pl = super.read(pair.getIdAggregate());
            if (pl != null) {
                results.add(pl);
            }
        }
        return results;
    }
    
    @Override
    public boolean delete(int id) throws Exception {
        ProductList pl = super.read(id);
        if (pl == null) {
            return false;
        }

        if (super.delete(id)) {
            indexByListId.delete(new PairIdId(pl.getIdList(), id));
            indexByProductId.delete(new PairIdId(pl.getIdProduct(), id));
            return true;
        }
        return false;
    }

    public boolean deleteAllFromList(int listId) throws Exception {
        List<ProductList> productLists = readByListId(listId);
        boolean allDeleted = true;
        for (ProductList pl : productLists) {
            if (!delete(pl.getId())) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }
}