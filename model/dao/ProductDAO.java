// Caminho: model/dao/ProductDAO.java (VERSÃO CORRETA)
package model.dao;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Product;
import model.indexes.BPlusTree;
import model.indexes.InvertedList;
import model.indexes.InvertedListElement;
import model.indexes.pairs.indirect.PairNameId;
import util.InvertedListCalculator;
import util.TextProcessor;

public class ProductDAO extends DAO<Product> {

  private BPlusTree<PairNameId> indexName;
  private InvertedList invertedList;

  public ProductDAO() throws Exception {
    super("product", Product.class.getConstructor());
    indexName = new BPlusTree<>(PairNameId.class.getConstructor(), 4, "./data/product.name.db");
    invertedList = new InvertedList(4, "./data/product.invertedlist.d.db", "./data/product.invertedlist.c.db");
  }

  @Override
  public int create(Product product) throws Exception {
    if (readByGtin13(product.getGtin13()) != null) {
      throw new Exception("Falha ao inserir produto: GTIN-13 '" + product.getGtin13() + "' já existe.");
    }
    int id = super.create(product);
    product.setId(id);
    indexName.create(new PairNameId(id, product.getName()));

    List<String> tokens = TextProcessor.tokenize(product.getName());
    System.out.println("Tokens: " + tokens);
    Map<String, Float> termFrequencies = InvertedListCalculator.calculateTermFrequency(tokens);
    System.out.println("Term Frequencies: " + termFrequencies);

    for (Map.Entry<String, Float> entry : termFrequencies.entrySet()) {
      String term = entry.getKey();
      float tf = entry.getValue();
      InvertedListElement element = new InvertedListElement(id, tf);
      invertedList.create(term, element);
    }

    invertedList.incrementaEntidades();
    System.out.println("Número de entidades: " + invertedList.numeroEntidades());

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

  public List<Product> readByName(String query) throws Exception {
    List<Product> results = new ArrayList<>();
    List<String> searchTerms = TextProcessor.tokenize(query);
    Map<Integer, Float> productScores = new HashMap<>();

    for (String term : searchTerms) {
      InvertedListElement[] termResults = invertedList.read(term);
      
      if (termResults.length == 0) continue;

      // Calcula IDF para o termo
      float idf = (float) InvertedListCalculator.calculateInverseDocumentFrequency(invertedList.numeroEntidades(), termResults.length);

      // Calcula TFxIDF para cada produto
      productScores = InvertedListCalculator.calculateTFxIDF(productScores, termResults, idf);
    }

    // Converte para InvertedListElement para ordenar
    List<InvertedListElement> productScoresOrdered = new ArrayList<>();
    for (Map.Entry<Integer, Float> entry : productScores.entrySet()) {
      InvertedListElement element = new InvertedListElement(entry.getKey(), entry.getValue());
      productScoresOrdered.add(element);
    }

    // Ordena por score (TFxIDF)
    productScoresOrdered.sort((a, b) -> Float.compare(b.getFrequency(), a.getFrequency()));

    // Lê os produtos pelo ID
    for (InvertedListElement element : productScoresOrdered) {
      Product p = super.read(element.getId());
      results.add(p);
    }

    return results;
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
        indexName.delete(new PairNameId(oldProduct.getId(), oldProduct.getName()));

        List<String> tokens = TextProcessor.tokenize(oldProduct.getName());
        System.out.println("Tokens: " + tokens);
      

        for (String token : tokens) {
          invertedList.delete(token, oldProduct.getId());
        }

        indexName.create(new PairNameId(newProduct.getId(), newProduct.getName()));

        tokens = TextProcessor.tokenize(newProduct.getName());
        System.out.println("Tokens: " + tokens);
        Map<String, Float> termFrequencies = InvertedListCalculator.calculateTermFrequency(tokens);
        System.out.println("Term Frequencies: " + termFrequencies);

        for (Map.Entry<String, Float> entry : termFrequencies.entrySet()) {
          String term = entry.getKey();
          float tf = entry.getValue();
          InvertedListElement element = new InvertedListElement(newProduct.getId(), tf);
          invertedList.create(term, element);
        }
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
      indexName.delete(new PairNameId(id, product.getName()));

      List<String> tokens = TextProcessor.tokenize(product.getName());
      System.out.println("Tokens: " + tokens);
      

      for (String token : tokens) {
        invertedList.delete(token, id);
      }

      invertedList.decrementaEntidades();
      System.out.println("Número de entidades: " + invertedList.numeroEntidades());
      return true;
    }
    return false;
  }
}