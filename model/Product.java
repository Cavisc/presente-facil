package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Product implements Generic  {
    private int id;
    private String gtin;
    private String name;
    private String description;

    public Product() {
        this(-1, "", "", "");
    }

    public Product(String gtin, String name, String description) {
        this(-1, gtin, name, description);
    }

    public Product(int id, String gtin, String name, String description) {
        this.id = id;
        this.gtin = gtin;
        this.name = name;
        this.description = description;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getGtin() {
        return this.gtin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.id);
        dos.write(this.gtin.getBytes());
        dos.writeUTF(this.name);
        dos.writeUTF(this.description);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        
        this.id = dis.readInt();

        byte[] baGtin = new byte[13];
        dis.read(baGtin);
        this.gtin = new String(baGtin);

        this.name = dis.readUTF();
        this.description = dis.readUTF();
    }
}
