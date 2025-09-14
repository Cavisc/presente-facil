package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PairIdId implements model.indexes.GenericBPlusTree<PairIdId> {
    private int idAggregator;
    private int idAggregate;
    private final short sizeInBytes = 8;

    public PairIdId() {
        this(-1, -1);
    }

    public PairIdId(int idAggregator) {
        this(idAggregator, -1);
    }

    public PairIdId(int idAggregator, int idAggregate) {
        this.idAggregator = idAggregator;
        this.idAggregate = idAggregate;
    }

    public int getIdAggregator() {
        return this.idAggregator;
    }

    public int getIdAggregate() {
        return this.idAggregate;
    }

    public short size() {
        return this.sizeInBytes;
    }

    public int compareTo(PairIdId otherPair) {
        if (this.idAggregator != otherPair.idAggregator) return this.idAggregator - otherPair.idAggregator;
        else return this.idAggregate == -1 ? 0 : this.idAggregate - otherPair.idAggregate;
    }

    @Override
    public PairIdId clone() {
        return new PairIdId(this.idAggregator, this.idAggregate);
    }

    @Override
    public String toString() {
        return String.format("%3d", this.idAggregator) + ";" + String.format("%-3d", this.idAggregate);
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idAggregator);
        dos.writeInt(this.idAggregate);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        this.idAggregator = dis.readInt();
        this.idAggregate = dis.readInt();
    }
}
