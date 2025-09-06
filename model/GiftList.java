package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

import util.NanoID;

public class GiftList implements Generic {
    private int id;
    private int userId;
    private String name;
    private String description;
    private LocalDate creationDate;
    private LocalDate limitDate;
    private String shareableCode;

    GiftList() {
        this.id = -1;
        this.userId = -1;
        this.name = "";
        this.description = "";
        this.creationDate = null;
        this.limitDate = null;
        this.shareableCode = "";
    }

    GiftList(int id, int userId, String name, String description, LocalDate limitDate) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.creationDate = LocalDate.now();
        this.limitDate = limitDate;
        this.shareableCode = NanoID.randomNanoId(10);
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return this.userId;
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

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCreationDate() {
        return this.creationDate;
    }

    public void setLimitDate(LocalDate limitDate) {
        this.limitDate = limitDate;
    }

    public LocalDate getLimitDate() {
        return this.limitDate;
    }

    public void generateShareableCode() {
        this.shareableCode = NanoID.randomNanoId(10);
    }

    public String getShareableCode() {
        return this.shareableCode;
    }

    public boolean isExpired() {
        return this.limitDate != null && LocalDate.now().isAfter(this.limitDate);
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeInt(this.id);
        dos.writeInt(this.userId);
        dos.writeUTF(this.name);
        dos.writeUTF(this.description);
        dos.writeInt((int) this.creationDate.toEpochDay());

        // Controle para saber se foi fornecido data limite
        if (this.limitDate != null) {
            dos.writeBoolean(true);
            dos.writeInt((int) this.limitDate.toEpochDay());
        }
        else { 
            dos.writeBoolean(false);
        }

        dos.write(this.shareableCode.getBytes());

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.userId = dis.readInt();
        this.name = dis.readUTF();
        this.description = dis.readUTF();
        this.creationDate = LocalDate.ofEpochDay(dis.readInt());

        // Controle para saber se foi fornecido data limite
        if (dis.readBoolean()) {
            this.limitDate = LocalDate.ofEpochDay(dis.readInt());
        }

        byte[] baShareableCode = new byte[10];
        dis.read(baShareableCode);
        this.shareableCode = new String(baShareableCode);
    }   
}
