package model.indexes.pairs.indirect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import model.indexes.GenericExtensibleHashTable;

public class PairEmailId implements GenericExtensibleHashTable {
    private String email;
    private int id;
    private final short sizeInBytes = 44;   // 40 bytes para email + 4 bytes para ID

    public PairEmailId() {
        this.email = "";
        this.id = -1;
    }

    public PairEmailId(String email, int id) {
        this.email = email;
        this.id = id;
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
        return "PairEmailId{email='" + email + "', id=" + id + "}";
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Garantir que o email não seja nulo
        if (email == null) {
            email = "";
        }

        // Converter o email para bytes com tamanho fixo de 40 bytes
        byte[] emailBytes = email.getBytes("UTF-8");
        if (emailBytes.length > 40) {
            throw new IOException("Email muito longo! Máximo 40 bytes.");
        }

        // Criar array de 40 bytes e preencher com zeros
        byte[] fixedEmailBytes = new byte[40];
        System.arraycopy(emailBytes, 0, fixedEmailBytes, 0, emailBytes.length);

        // Escrever os bytes fixos do email e o ID
        dos.write(fixedEmailBytes);
        dos.writeInt(id);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] bytes) throws IOException {
        if (bytes.length != sizeInBytes) {
            throw new IOException("Tamanho de bytes inválido. Esperado: " + sizeInBytes + ", Recebido: " + bytes.length);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        // Ler os 40 bytes do email
        byte[] emailBytes = new byte[40];
        dis.readFully(emailBytes);
        
        // Converter para string e remover caracteres nulos/espaços extras
        this.email = new String(emailBytes, "UTF-8").trim();
        
        // Ler o ID
        this.id = dis.readInt();
    }

    public static int hashCode(String email) {
        return Math.abs(email.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PairEmailId that = (PairEmailId) obj;
        return id == that.id && email.equals(that.email);
    }
}