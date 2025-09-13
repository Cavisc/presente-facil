package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import model.indexes.GenericExtensibleHashTable;

public class PairEmailId implements GenericExtensibleHashTable {
    private String email;
    private int id;
    private final short sizeInBytes = 44;   
    
    public PairEmailId() throws Exception {
        this("", -1);
    }

    public PairEmailId(String email) throws Exception {
        this(email, -1);
    }

    public PairEmailId(String email, int id) throws Exception {
        if (email.length() == 0 || email.length() <= 40) {
            this.email = email;
            this.id = id;
        } else {
            throw new Exception("Email inválido! Deve conter no máximo 40 caracteres.");
        }
    }

    public short size() {
        return this.sizeInBytes;
    }

    public String getEmail() {
        return this.email;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.email.hashCode());
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        if (email.length() == 0 || email.length() > 40) throw new Exception("Email inválido! Deve conter no máximo 40 caracteres.");

        byte[] bytes = new byte[40];
        bytes = email.getBytes();
        
        dos.write(bytes);
        dos.writeInt(this.id);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        byte[] emailBytes = new byte[40];
        dis.read(emailBytes);

        this.email = new String(emailBytes).trim();
        this.id = dis.readInt();
    }
}
