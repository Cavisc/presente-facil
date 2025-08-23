package model.dao;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

public class DAO <T extends model.Generic> {
    RandomAccessFile file;
    // HashExtensivel<ParIDEndereco> indiceDireto;
    String fileName;
    int HEADER_LEN = 12;
    Constructor<T> construtor;

    public DAO(String fileName, Constructor<T> construtor) throws Exception {
        this.fileName = fileName;
        this.construtor = construtor;
        file = new RandomAccessFile("./dados/" + fileName + ".db", "rw");
        // indiceDireto = new HashExtensivel<>(ParIDEndereco.class.getConstructor(), 4, "./dados/" + nomeFile + ".d.db", "./dados/" + nomeFile + ".c.db");
        if(file.length()<HEADER_LEN) {
            file.writeInt(0);  // último id usado
            file.writeLong(-1);  // cabeça da lista de espaços vazios
        }
    }

    public int create(T entity) throws Exception {

        // Obtem o ID para esta entity
        file.seek(0);
        int newId = file.readInt()+1;
        file.seek(0);
        file.writeInt(newId);
        entity.setId(newId);

        // Escreve o novo registro no fim do file
        byte[] vb = entity.toByteArray();
        long pos = buscaVazio(vb.length);
        if(pos == -1) {
            file.seek(file.length());
            pos = file.getFilePointer();
            file.writeByte(' ');
            file.writeShort(vb.length);
            file.write(vb);
        } else {
            file.seek(pos);
            file.writeByte(' ');
            file.skipBytes(2);
            file.write(vb);
        }
        // indiceDireto.create(new ParIDEndereco(entity.getID(), pos));
        return newId;
    }

    public T read(int id) throws Exception {
        file.seek(HEADER_LEN);

        /*
        ParIDEndereco pie = indiceDireto.read(id);
        if(pie!=null) {
            file.seek(pie.getEndereco());
            byte lapide = file.readByte();
            short tam = file.readShort();
            if(lapide==' ') {
                byte[] vb = new byte[tam];
                file.read(vb);
                T entity = construtor.newInstance();
                entity.fromByteArray(vb);
                if(entity.getID() == id) {
                    return entity;
                }
            }
        }
        */

        return null;
    }

    public boolean update(T novaEntity) throws Exception {
        /*
        ParIDEndereco pie = indiceDireto.read(novaEntity.getID());
        if(pie!=null) {
            long pos = pie.getEndereco();
            file.seek(pos);
            byte lapide = file.readByte();
            short tam = file.readShort();
            if(lapide==' ') {
                byte[] vb = new byte[tam];
                file.read(vb);
                T entity = construtor.newInstance();
                entity.fromByteArray(vb);
                if(entity.getID() == novaEntity.getID()) {
                    byte[] vb2 = novaEntity.toByteArray();
                    int tam2 = vb2.length;
                    if(tam2 <= tam) {
                        file.seek(pos+3);
                        file.write(vb2);
                    } else {
                        file.seek(pos);
                        file.writeByte('*');
                        insereVazio(pos, tam);

                        long novaPos = buscaVazio(tam2);
                        if(novaPos == -1) {
                            file.seek(file.length());
                            file.writeByte(' ');
                            file.writeShort(tam2);
                            file.write(vb2);
                        } else {
                            file.seek(novaPos);
                            file.writeByte(' ');
                            file.skipBytes(2);
                            file.write(vb2);
                        }
                    }
                    return true;
                }
            }
        } 
        */
        return false;
    }
    
    public boolean delete(int id) throws Exception {
        file.seek(HEADER_LEN);
        /*
        ParIDEndereco pie = indiceDireto.read(id);
        if(pie!=null) {
            long pos = pie.getEndereco();
            file.seek(pos);
            byte lapide = file.readByte();
            short tam = file.readShort();
            if(lapide==' ') {
                byte[] vb = new byte[tam];
                file.read(vb);
                T entity = construtor.newInstance();
                entity.fromByteArray(vb);
                if(entity.getID() == id) {
                    file.seek(pos);
                    file.writeByte('*');
                    insereVazio(pos, tam);
                    return true;
                }
            }
        }
        */
        
        return false;
    }

    public void close() throws Exception {
        file.close();
    }

    public void insereVazio(long enderecoEspaco, int tamanhoEspaco) throws Exception {
        long anterior=4; // endereco do ponteiro inicial da lista
        long proximo=-1;
        long endereco;
        byte lapide;
        short tamanho;

        file.seek(4);
        endereco = file.readLong();

        if(endereco==-1) {
            // lista vazia
            file.seek(anterior);
            file.writeLong(enderecoEspaco);
            file.seek(enderecoEspaco+3);  // salta o lápide e o indicador de tamanho;
            file.writeLong(-1);
        } else {
            // lista não estava vazia
            do {
                file.seek(endereco);
                lapide = file.readByte();
                tamanho = file.readShort();
                proximo = file.readLong();
                if(tamanhoEspaco<tamanho) {
                    if(anterior==4)
                        file.seek(anterior);
                    else   
                        file.seek(anterior+3);
                    file.writeLong(enderecoEspaco);
                    file.seek(enderecoEspaco+3);
                    file.writeLong(endereco);
                    break;
                }
                if(proximo==-1) {
                    file.seek(endereco+3);
                    file.writeLong(enderecoEspaco);
                    file.seek(enderecoEspaco+3);
                    file.writeLong(-1);
                    break;
                }
                anterior = endereco;
                endereco = proximo;
            } while(endereco!=-1);
        }
    }

    public long buscaVazio(int tamanhoEspaco) throws Exception {
        long anterior = 4; // cabeça da lista
        long proximo;
        file.seek(anterior);
        long endereco = file.readLong();
        byte lapide;
        int tamanho;

        while(endereco!=-1) {
            file.seek(endereco);
            lapide = file.readByte();
            tamanho = file.readShort();
            proximo = file.readLong();
            if(tamanhoEspaco<tamanho) {
                if(anterior == 4)
                    file.seek(anterior);
                else
                    file.seek(anterior+3);
                file.writeLong(proximo);
                break;
            }
            anterior = endereco;
            endereco = proximo;
        }
        return endereco;
    }
}
