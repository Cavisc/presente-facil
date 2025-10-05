package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.indexes.GenericExtensibleHashTable;

public class PairGtinId implements GenericExtensibleHashTable {
    private String gtinId;
    private int id;
    private final short sizeInBytes = 17;   // 13 bytes para gtinId + 4 bytes para id

    public PairGtinId() {
        this.gtinId = "             "; // 13 espaços como padrão
        this.id = -1;
    }

    public PairGtinId(String gtinId, int id) throws Exception {
        if (gtinId.length() != 10) {
            throw new Exception("GTIN inválido! Deve conter exatamente 13 caracteres.");
        }
        this.gtinId = gtinId;
        this.id = id;
    }

    public short size() {
        return this.sizeInBytes;
    }

    public String getGtinId() {
        return this.gtinId;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Math.abs(this.gtinId.hashCode());
    }

    @Override
    public String toString() {
        return "PairGtinId{gtinId='" + gtinId + "', id=" + id + "}";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Garantir que o gtinId tenha exatamente 13 caracteres
        String fixedGtinId = gtinId;
        if (fixedGtinId == null) {
            fixedGtinId = "             "; // 13 espaços se for nulo
        } else if (fixedGtinId.length() != 13) {
            // Se não tiver 13 caracteres, ajustar preenchendo com espaços ou truncando
            if (fixedGtinId.length() > 13) {
                fixedGtinId = fixedGtinId.substring(0, 13);
            } else {
                fixedGtinId = String.format("%-13s", fixedGtinId); // Preenche com espaços à direita
            }
        }

        // Escrever os 13 bytes do gtinId
        byte[] gtinIdBytes = fixedGtinId.getBytes("UTF-8");
        dos.write(gtinIdBytes);
        
        // Escrever o ID
        dos.writeInt(id);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws IOException {
        if (bytes.length != sizeInBytes) {
            throw new IOException("Tamanho de bytes inválido. Esperado: " + sizeInBytes + ", Recebido: " + bytes.length);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        // Ler os 13 bytes do gtinId
        byte[] gtinIdBytes = new byte[13];
        dis.readFully(gtinIdBytes);
        this.gtinId = new String(gtinIdBytes, "UTF-8");
        
        // Ler o ID
        this.id = dis.readInt();
    }

    public static int hashCode(String gtinId) {
        // Garantir que o gtinId tenha 13 caracteres para calcular o hash consistentemente
        String fixedGtinId = gtinId;
        if (fixedGtinId == null) {
            fixedGtinId = "          ";
        } else if (fixedGtinId.length() != 13) {
            if (fixedGtinId.length() > 13) {
                fixedGtinId = fixedGtinId.substring(0, 13);
            } else {
                fixedGtinId = String.format("%-13s", fixedGtinId);
            }
        }
        return Math.abs(fixedGtinId.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PairGtinId that = (PairGtinId) obj;
        return id == that.id && gtinId.equals(that.gtinId);
    }
}