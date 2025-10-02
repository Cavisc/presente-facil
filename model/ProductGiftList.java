package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ProductGiftList implements Generic {
    private int id;
    private int listId;
    private int productId;
    private byte quantity;
    private String observation;

    public ProductGiftList() {
        this(-1, -1, -1, (byte)-1, "");
    }

    public ProductGiftList(int listId, int productId, byte quantity, String observation) {
        this(-1, listId, productId, quantity, observation);
    }

    public ProductGiftList(int id, int listId, int productId, byte quantity, String observation) {
        this.id = id;
        this.listId = listId;
        this.productId = productId;
        this.quantity = quantity;
        this.observation = observation;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getListId() {
        return this.listId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductId() {
        return this.productId;
    }

    public void setQuantity(byte quantity) {
        this.quantity = quantity;
    }

    public byte getQuantity() {
        return this.quantity;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getObservation() {
        return this.observation;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.writeInt(this.listId);
        dos.writeInt(this.productId);
        dos.writeByte(this.quantity);

        // Controle para saber se foi fornecido observação
        if (!this.observation.isEmpty()) {
            dos.writeBoolean(true);
            dos.writeUTF(this.observation);
        }
        else { 
            dos.writeBoolean(false);
        }

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();
        this.listId = dis.readInt();
        this.productId = dis.readInt();
        this.quantity = dis.readByte();

        // Controle para saber se foi fornecido observação
        if (dis.readBoolean()) {
            this.observation = dis.readUTF();
        }
    }
}
