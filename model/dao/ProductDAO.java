package model.dao;

import model.Product;
import model.indexes.ExtensibleHashTable;
import model.indexes.pairs.indirect.PairGtinId;

public class ProductDAO extends DAO<Product> {
    ExtensibleHashTable<PairGtinId> indirectIndexGtinId;

    public ProductDAO() throws Exception {
        super("product", Product.class.getConstructor());
        this.indirectIndexGtinId = new ExtensibleHashTable<>(PairGtinId.class.getConstructor(), 3, "./data/product.gtin.d.db", "./data/product.gtin.c.db");
    }
    
    public int create(Product product) throws Exception {
        int id = super.create(product);
        this.indirectIndexGtinId.create(new PairGtinId(product.getGtin(), id));

        return id;
    }

    public Product readByGtin(String gtin) throws Exception {
        PairGtinId pgi = this.indirectIndexGtinId.read(PairGtinId.hashCode(gtin));

        if (pgi == null) return null;
        int id = pgi.getId();
        
        return super.read(id);
    }

    public boolean update(Product newProduct) throws Exception {
        Product oldProduct = super.read(newProduct.getId());
        String oldGtin = oldProduct.getGtin();

        if (super.update(newProduct)) {
            if (newProduct.getGtin().compareTo(oldGtin) != 0) {
                this.indirectIndexGtinId.delete(PairGtinId.hashCode(oldGtin));
                this.indirectIndexGtinId.create(new PairGtinId(newProduct.getGtin(), newProduct.getId()));
            }

            return true;
        }
        else return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Product product = super.read(id);
        
        if (product == null) return false;

        ProductGiftListDAO productGiftListDAO = new ProductGiftListDAO();

        if (productGiftListDAO.readByProductId(id).length == 0 && super.delete(id)) {
            this.indirectIndexGtinId.delete(PairGtinId.hashCode(product.getGtin()));
            return true;
        }
        else {
            return productGiftListDAO.deleteByProductId(id) 
                   && super.delete(id) 
                   && this.indirectIndexGtinId.delete(PairGtinId.hashCode(product.getGtin()));
        }
    }
}
