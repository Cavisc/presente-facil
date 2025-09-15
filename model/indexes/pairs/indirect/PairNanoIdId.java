package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.indexes.GenericExtensibleHashTable;

public class PairNanoIdId implements GenericExtensibleHashTable {
    private String nanoId;
    private int id;
    private final short sizeInBytes = 14;   // 10 bytes para nanoId + 4 bytes para id

    public PairNanoIdId() {
        this.nanoId = "          "; // 10 espaços como padrão
        this.id = -1;
    }

    public PairNanoIdId(String nanoId, int id) throws Exception {
        if (nanoId.length() != 10) {
            throw new Exception("Nano ID inválido! Deve conter exatamente 10 caracteres.");
        }
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

    @Override
    public String toString() {
        return "PairNanoIdId{nanoId='" + nanoId + "', id=" + id + "}";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Garantir que o nanoId tenha exatamente 10 caracteres
        String fixedNanoId = nanoId;
        if (fixedNanoId == null) {
            fixedNanoId = "          "; // 10 espaços se for nulo
        } else if (fixedNanoId.length() != 10) {
            // Se não tiver 10 caracteres, ajustar preenchendo com espaços ou truncando
            if (fixedNanoId.length() > 10) {
                fixedNanoId = fixedNanoId.substring(0, 10);
            } else {
                fixedNanoId = String.format("%-10s", fixedNanoId); // Preenche com espaços à direita
            }
        }

        // Escrever os 10 bytes do nanoId
        byte[] nanoIdBytes = fixedNanoId.getBytes("UTF-8");
        dos.write(nanoIdBytes);
        
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

        // Ler os 10 bytes do nanoId
        byte[] nanoIdBytes = new byte[10];
        dis.readFully(nanoIdBytes);
        this.nanoId = new String(nanoIdBytes, "UTF-8");
        
        // Ler o ID
        this.id = dis.readInt();
    }

    public static int hashCode(String nanoId) {
        // Garantir que o nanoId tenha 10 caracteres para calcular o hash consistentemente
        String fixedNanoId = nanoId;
        if (fixedNanoId == null) {
            fixedNanoId = "          ";
        } else if (fixedNanoId.length() != 10) {
            if (fixedNanoId.length() > 10) {
                fixedNanoId = fixedNanoId.substring(0, 10);
            } else {
                fixedNanoId = String.format("%-10s", fixedNanoId);
            }
        }
        return Math.abs(fixedNanoId.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PairNanoIdId that = (PairNanoIdId) obj;
        return id == that.id && nanoId.equals(that.nanoId);
    }
}