package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

public class PairNameId implements model.indexes.GenericBPlusTree<PairNameId> {
    private int idAggregator;
    private String nameAggregate;
    private static final int MAX_STRING_LENGTH = 70;
    private static final int STRING_MAX_BYTES = MAX_STRING_LENGTH * 2; // 140 bytes para UTF-8
    private final short sizeInBytes = (short) (4 + STRING_MAX_BYTES); // 4 para int + 140 para string

    public PairNameId() {
        this(-1, "");
    }

    public PairNameId(int idAggregator) {
        this(idAggregator, "");
    }

    public PairNameId(int idAggregator, String nameAggregate) {
        this.idAggregator = idAggregator;
        this.nameAggregate = truncateString(nameAggregate, MAX_STRING_LENGTH);
    }

    public int getIdAggregator() {
        return this.idAggregator;
    }

    public String getNameAggregate() {
        return this.nameAggregate;
    }

    public short size() {
        return this.sizeInBytes;
    }

    public int compareTo(PairNameId otherPair) {
        if (this.idAggregator != otherPair.idAggregator) return this.idAggregator - otherPair.idAggregator;
        else return this.nameAggregate.compareTo(otherPair.nameAggregate);
    }

    @Override
    public PairNameId clone() {
        return new PairNameId(this.idAggregator, this.nameAggregate);
    }

    public String toString() {
        return String.format("%3d", this.idAggregator) + ";" + this.nameAggregate;
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idAggregator);
        
        // Converter a string para bytes UTF-8
        byte[] stringBytes = this.nameAggregate.getBytes(StandardCharsets.UTF_8);
        byte[] fixedStringBytes = new byte[STRING_MAX_BYTES];
        
        // Copiar os bytes da string para o array de tamanho fixo
        int length = Math.min(stringBytes.length, STRING_MAX_BYTES);
        System.arraycopy(stringBytes, 0, fixedStringBytes, 0, length);
        
        // Preencher o restante com zeros se necessário
        dos.write(fixedStringBytes);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        this.idAggregator = dis.readInt();
        
        // Ler os bytes da string
        byte[] stringBytes = new byte[STRING_MAX_BYTES];
        dis.readFully(stringBytes);
        
        // Converter para string, removendo caracteres nulos no final
        this.nameAggregate = new String(stringBytes, StandardCharsets.UTF_8).trim();
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength);
    }
}