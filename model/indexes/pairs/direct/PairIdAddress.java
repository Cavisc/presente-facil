package model.indexes.pairs.direct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import model.indexes.GenericExtensibleHashTable;

public class PairIdAddress implements GenericExtensibleHashTable {
    private int id;
    private long address;
    private final short sizeInBytes = 12;

    public PairIdAddress() {
        this(-1, -1);
    }

    public PairIdAddress(int id, long address) {
        this.id = id;
        this.address = address;
    }

    public int getId() {
        return this.id;
    }

    public long getAddress() {
        return this.address;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public short size() {
        return this.sizeInBytes;
    }

    public String toString() {
        return "{Id: " + this.id + " Endereço: " + this.address + "}";
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(id);
        dos.writeLong(address);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.address = dis.readLong();
    }
}
