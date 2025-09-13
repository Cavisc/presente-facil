package model.dao;

import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;

import model.indexes.ExtensibleHashTable;
import model.indexes.pairs.direct.PairIdAddress;

public class DAO <T extends model.Generic> {
    Constructor<T> constructor;
    ExtensibleHashTable<PairIdAddress> directIndex;
    RandomAccessFile file;
    String fileName;
    int HEADER_SIZE = 12;

    DAO(String fileName, Constructor<T> constructor) throws Exception {
        this.fileName = fileName;
        this.constructor = constructor;
        file = new RandomAccessFile("./data/"+fileName+".db", "rw");
        directIndex = new ExtensibleHashTable<>(PairIdAddress.class.getConstructor(), 4, "./data/"+fileName+".d.db", "./data/"+fileName+".c.db");

        if (file.length() < HEADER_SIZE) {
            file.writeInt(0);  // Último ID usado
            file.writeLong(-1);  // Cabeça da lista de espaços livres
        }
    }

    public int create(T entity) throws Exception {
        // Obtem o ID para essa entidade
        file.seek(0);
        int newId = file.readInt() + 1;
        file.seek(0);
        file.writeInt(newId);
        entity.setId(newId);

        // Escreve o novo registro no final do arquivo
        byte[] bytes = entity.toByteArray();
        long pos = findFreeSpace(bytes.length);

        if (pos == -1) {
            file.seek(file.length());
            pos = file.getFilePointer();
            file.writeByte(' ');
            file.writeShort(bytes.length);
            file.write(bytes);
        } 
        else {
            file.seek(pos);
            file.writeByte(' ');
            file.skipBytes(2);
            file.write(bytes);
        }
        
        directIndex.create(new PairIdAddress(entity.getId(), pos));

        return newId;
    }

    public T read(int id) throws Exception {
        file.seek(HEADER_SIZE);
        PairIdAddress pia = directIndex.read(id);

        if (pia != null) {
            file.seek(pia.getAddress());
            byte tombstone = file.readByte();
            short size = file.readShort();

            if (tombstone == ' ') {
                byte[] bytes = new byte[size];
                file.read(bytes);

                T entity = constructor.newInstance();
                entity.fromByteArray(bytes);

                return entity;
            }
        }

        return null;
    }

    public boolean update(T newEntity) throws Exception {
        PairIdAddress pia = directIndex.read(newEntity.getId());

        if (pia != null) {
            long pos = pia.getAddress();
            file.seek(pos);
            byte tombstone = file.readByte();
            short size = file.readShort();

            if (tombstone == ' ') {
                byte[] bytes = new byte[size];
                file.read(bytes);
                T oldEntity = constructor.newInstance();
                oldEntity.fromByteArray(bytes);

                byte[] newBytes = newEntity.toByteArray();

                if (newBytes.length <= bytes.length) {
                    file.seek(pos + 3); // 1 byte para tombstone + 2 bytes para size
                    file.write(newBytes);
                    return true;
                } 
                else {
                    // Marca o registro antigo como deletado
                    file.seek(pos);
                    file.writeByte('*');

                    // Adiciona o espaço antigo à lista de espaços livres
                    addToFreeList(pos, size + 3); // 1 byte para tombstone + 2 bytes para size

                    // Escreve o novo registro no final do arquivo
                    file.seek(file.length());
                    long newPos = file.getFilePointer();
                    file.writeByte(' ');
                    file.writeShort(newBytes.length);
                    file.write(newBytes);

                    // Atualiza o index
                    directIndex.update(new PairIdAddress(newEntity.getId(), newPos));

                    return true;
                }
            }
        }

        return false;
    }

    public boolean delete(int id) throws Exception {
        PairIdAddress pia = directIndex.read(id);

        if (pia != null) {
            long pos = pia.getAddress();
            file.seek(pos);
            byte tombstone = file.readByte();
            short size = file.readShort();

            if (tombstone == ' ') {
                // Marca o registro como deletado
                file.seek(pos);
                file.writeByte('*');

                // Adiciona o espaço à lista de espaços livres
                addToFreeList(pos, size + 3); // 3 bytes para tombstone e size

                // Remove from index
                directIndex.delete(id);

                return true;
            }
        }

        return false;
    }

    public void close() throws Exception {
        file.close();
    }

    public void addToFreeList(long spaceAddress, int spaceSize) throws Exception {
        long previous = 4; // Endereço do ponteiro inicial da lista
        long next = -1;
        long address;
        short size;

        file.seek(4);
        address = file.readLong();

        if (address == -1) {
            // A lista de espaços livres está vazia, insira como o primeiro elemento
            file.seek(previous);
            file.writeLong(spaceAddress);
            file.seek(spaceAddress + 3);
            file.writeLong(-1);
        } 
        else {
            // Percorra a lista de espaços livres para encontrar a posição correta
            while (address != -1) {
                file.seek(address);
                file.readByte();
                size = file.readShort();
                next = file.readLong();

                if (spaceSize < size) {
                    if (previous == 4) file.seek(previous);
                    else file.seek(previous + 3);
                    
                    file.writeLong(spaceAddress);
                    file.seek(spaceAddress + 3);
                    file.writeLong(address);

                    break;
                }

                if (next == -1) {
                    file.seek(address + 3);
                    file.writeLong(spaceAddress);
                    file.seek(spaceAddress + 3);
                    file.writeLong(-1);
                    break;
                }

                previous = address;
                address = next;
            }
        }
    }

    public long findFreeSpace(int requiredSize) throws Exception {
        long previous = 4; // Cabeça da lista de espaços livres
        long next;
        file.seek(previous);
        long address = file.readLong();

        while (address != -1) {
            file.seek(address);
            file.readByte();
            short size = file.readShort();
            next = file.readLong();

            if (requiredSize < size) {
                // Found a suitable space
                if (previous == 4) file.seek(previous);
                else file.seek(previous + 3);
                
                file.writeLong(next); // Remove da lista de espaços livres
                
                break;
            }

            previous = address;
            address = next;
        }

        return address;
    }
}
