package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import model.indexes.GenericExtensibleHashTable;

public class PairNanoIdId implements GenericExtensibleHashTable {
    private String nanoId;
    private int id;
    private final short sizeInBytes = 14;   
    
    public PairNanoIdId() throws Exception {
        this("", -1);
    }

    public PairNanoIdId(String nanoId) throws Exception {
        this(nanoId, -1);
    }

    public PairNanoIdId(String nanoId, int id) throws Exception {
        if (nanoId.length() != 10) throw new Exception("Nano ID inválido! Deve conter exatamente 10 caracteres.");
        this.nanoId = nanoId;
        this.id = id;
    }

    public short size() {
        return this.sizeInBytes;
    }

    public String getNanoId() {
        return this.nanoId;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.nanoId.hashCode());
    }

    public static int hashCode(String nanoId) throws Exception {
        if (nanoId.length() != 10) throw new Exception("Nano ID inválido! Deve conter exatamente 10 caracteres.");
        return Math.abs(nanoId.hashCode());
    }

    @Override
    public String toString() {
        return "(" + this.nanoId + ";" + this.id + ")";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        if (nanoId.length() != 10) throw new Exception("Nano ID inválido! Deve conter exatamente 10 caracteres.");

        byte[] bytes = new byte[10];
        bytes = nanoId.getBytes();
        
        dos.write(bytes);
        dos.writeInt(id);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        byte[] nanoIdBytes = new byte[10];
        dis.read(nanoIdBytes);
        
        this.nanoId = new String(nanoIdBytes);
        this.id = dis.readInt();
    }
}
