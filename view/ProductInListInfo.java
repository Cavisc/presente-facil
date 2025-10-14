// Caminho: view/ProductInListInfo.java
package view;

// Tornamos a classe pública para que o Controller possa acessá-la
public class ProductInListInfo {
  public String productName;
  public int quantity;

  // Este é o construtor que o Controller precisa
  public ProductInListInfo(String name, int qty) {
    this.productName = name;
    this.quantity = qty;
  }
}