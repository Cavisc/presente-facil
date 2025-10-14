// Caminho: model/indexes/pairs/indirect/PairGtin13Id.java (VERSÃO À PROVA DE FALHAS)
package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import model.indexes.GenericExtensibleHashTable;

public class PairGtin13Id implements GenericExtensibleHashTable, Cloneable {

    private String gtin13;
    private int id;

    public PairGtin13Id() { this("", -1); }
    public PairGtin13Id(String gtin13, int id) { this.gtin13 = gtin13; this.id = id; }
    public String getGtin13() { return gtin13; }
    public int getId() { return id; }

    @Override
    public int hashCode() { return hashCode(this.gtin13); }
    public static int hashCode(String gtin13) { return Math.abs(gtin13.hashCode()); }
    
    @Override
    public short size() {
        try { return (short) toByteArray().length; } catch (Exception e) { e.printStackTrace(); return 0; }
    }
    
    @Override
    public PairGtin13Id clone() {
        try { return (PairGtin13Id) super.clone(); } catch (CloneNotSupportedException e) { throw new InternalError(e); }
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(gtin13);
        dos.writeInt(id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws Exception {
        if (ba == null || ba.length == 0) return;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // NOVA VERIFICAÇÃO DE ROBUSTEZ
        if (dis.available() > 0) {
            this.gtin13 = dis.readUTF();
        }
        if (dis.available() > 0) {
            this.id = dis.readInt();
        }
    }
}