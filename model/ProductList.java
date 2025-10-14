// Caminho: model/ProductList.java
package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ProductList implements Generic {
    private int id;
    private int idList;
    private int idProduct;
    private int quantity;
    private String observations;

    public ProductList() {
        this(-1, -1, -1, 1, "");
    }

    public ProductList(int idList, int idProduct, int quantity, String observations) {
        this(-1, idList, idProduct, quantity, observations);
    }
    
    public ProductList(int id, int idList, int idProduct, int quantity, String observations) {
        this.id = id;
        this.idList = idList;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.observations = observations;
    }

    @Override
    public int getId() { return id; }
    @Override
    public void setId(int id) { this.id = id; }
    public int getIdList() { return idList; }
    public void setIdList(int idList) { this.idList = idList; }
    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idList);
        dos.writeInt(idProduct);
        dos.writeInt(quantity);
        dos.writeUTF(observations);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.idList = dis.readInt();
        this.idProduct = dis.readInt();
        this.quantity = dis.readInt();
        this.observations = dis.readUTF();
    }
}