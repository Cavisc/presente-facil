// Caminho: model/Product.java (VERSÃO CORRETA)
package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Product implements Generic {
    private int id;
    private String gtin13;
    private String name;
    private String description;
    private boolean active;

    public Product() {
        this(-1, "", "", "", true);
    }

    public Product(String gtin13, String name, String description) {
        this(-1, gtin13, name, description, true);
    }

    public Product(int id, String gtin13, String name, String description, boolean active) {
        this.id = id;
        this.gtin13 = gtin13;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getGtin13() {
        return gtin13;
    }

    public void setGtin13(String gtin13) {
        this.gtin13 = gtin13;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeUTF(gtin13);
        dos.writeUTF(name);
        dos.writeUTF(description);
        dos.writeBoolean(active);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.gtin13 = dis.readUTF();
        this.name = dis.readUTF();
        this.description = dis.readUTF();
        this.active = dis.readBoolean();
    }
}