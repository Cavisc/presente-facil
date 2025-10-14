// Caminho: model/indexes/pairs/indirect/PairProductNameId.java (VERSÃO FINAL ROBUSTA)
package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import model.indexes.GenericBPlusTree;

public class PairProductNameId implements GenericBPlusTree<PairProductNameId>, Cloneable {

    private String name;
    private int id;

    public PairProductNameId() { this("", -1); }
    public PairProductNameId(String name, int id) { this.name = name; this.id = id; }
    public String getName() { return name; }
    public int getId() { return id; }
    public int getAggregateId() { return this.id; }
    
    // MÉTODO ATUALIZADO PARA SER À PROVA DE NULOS
    @Override
    public int compareTo(PairProductNameId other) {
        if (other == null) {
            return 1; // Por convenção, qualquer objeto é "maior" que nulo
        }
        return this.name.compareTo(other.name);
    }
    
    @Override
    public PairProductNameId clone() {
        try { return (PairProductNameId) super.clone(); } catch (CloneNotSupportedException e) { throw new InternalError(e); }
    }
    
    @Override
    public short size() {
        try { return (short) toByteArray().length; } catch (Exception e) { e.printStackTrace(); return 0; }
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.name);
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        if (ba == null || ba.length == 0) return;
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        if (dis.available() > 0) this.name = dis.readUTF();
        if (dis.available() > 0) this.id = dis.readInt();
    }
}