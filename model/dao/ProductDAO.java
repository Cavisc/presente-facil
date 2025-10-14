// Caminho: model/dao/ProductDAO.java (VERSÃO CORRETA)
package model.dao;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import model.Product;
import model.indexes.BPlusTree;
import model.indexes.pairs.indirect.PairProductNameId;

public class ProductDAO extends DAO<Product> {

  private BPlusTree<PairProductNameId> indexName;

  public ProductDAO() throws Exception {
    super("product", Product.class.getConstructor());
    indexName = new BPlusTree<>(PairProductNameId.class.getConstructor(), 4, "./data/product.name.db");
  }

  @Override
  public int create(Product product) throws Exception {
    if (readByGtin13(product.getGtin13()) != null) {
      throw new Exception("Falha ao inserir produto: GTIN-13 '" + product.getGtin13() + "' já existe.");
    }
    int id = super.create(product);
    product.setId(id);
    indexName.create(new PairProductNameId(product.getName(), id));
    return id;
  }

  public Product readByGtin13(String gtin13) throws Exception {
    RandomAccessFile raf = new RandomAccessFile("./data/" + this.fileName + ".db", "r");
    if (raf.length() <= HEADER_SIZE) {
      raf.close();
      return null;
    }
    raf.seek(HEADER_SIZE);
    while (raf.getFilePointer() < raf.length()) {
      raf.readByte(); // Tombstone
      short size = raf.readShort();
      byte[] data = new byte[size];
      raf.read(data);
      Product p = this.constructor.newInstance();
      p.fromByteArray(data);
      if (p.getGtin13().equals(gtin13)) {
        raf.close();
        return p;
      }
    }
    raf.close();
    return null;
  }

  public List<Product> readAll() throws Exception {
    List<Product> products = new ArrayList<>();
    RandomAccessFile raf = new RandomAccessFile("./data/" + this.fileName + ".db", "r");
    if (raf.length() <= HEADER_SIZE) {
      raf.close();
      return products;
    }
    raf.seek(HEADER_SIZE);
    while (raf.getFilePointer() < raf.length()) {
      byte tombstone = raf.readByte();
      short size = raf.readShort();
      byte[] data = new byte[size];
      raf.read(data);
      if (tombstone == ' ') {
        Product p = this.constructor.newInstance();
        p.fromByteArray(data);
        products.add(p);
      }
    }
    raf.close();
    Collections.sort(products, Comparator.comparing(Product::getName));
    return products;
  }

  @Override
  public boolean update(Product newProduct) throws Exception {
    Product oldProduct = super.read(newProduct.getId());
    if (oldProduct == null)
      return false;
    if (!oldProduct.getGtin13().equals(newProduct.getGtin13())) {
      if (readByGtin13(newProduct.getGtin13()) != null) {
        throw new Exception(
            "Falha ao atualizar: GTIN-13 '" + newProduct.getGtin13() + "' já pertence a outro produto.");
      }
    }
    if (super.update(newProduct)) {
      if (!oldProduct.getName().equals(newProduct.getName())) {
        indexName.delete(new PairProductNameId(oldProduct.getName(), oldProduct.getId()));
        indexName.create(new PairProductNameId(newProduct.getName(), newProduct.getId()));
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean delete(int id) throws Exception {
    Product product = super.read(id);
    if (product == null)
      return false;
    if (super.delete(id)) {
      indexName.delete(new PairProductNameId(product.getName(), id));
      return true;
    }
    return false;
  }
}