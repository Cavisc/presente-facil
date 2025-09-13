package model.indexes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ExtensibleHashTable <T extends model.indexes.GenericExtensibleHashTable> {
    int amountDataPerBucket;
    RandomAccessFile bucketFile;
    String bucketFileName;
    Constructor<T> constructor;
    Directory directory;
    RandomAccessFile directoryFile;
    String directoryFileName;

    public class Bucket {
        short bytesPerBucket;
        short bytesPerElement;
        Constructor<T> constructor;
        ArrayList<T> elements;
        byte localDepth;
        short maxQuantityElements;
        short quantityElements;

        public Bucket(Constructor<T> constructor, short maxQuantityElements) throws Exception {
            this(constructor, maxQuantityElements, 0);
        }

        public Bucket(Constructor<T> constructor, short maxQuantityElements, int localDepth) throws Exception {
            this.constructor = constructor;

            if (maxQuantityElements < 1 || maxQuantityElements > 127) 
                throw new Exception("Quantidade máxima de elementos por cesto inválida! Valor: " + maxQuantityElements + " elementos");
            if (localDepth > 127) 
                throw new Exception("Profundidade local inválida! Valor: " + localDepth + " bits");

            this.maxQuantityElements = maxQuantityElements;
            this.localDepth = (byte) localDepth;
            this.elements = new ArrayList<>(maxQuantityElements);
            this.bytesPerElement = constructor.newInstance().size();
            this.bytesPerBucket = (short) (1 + 2 + (this.bytesPerElement * maxQuantityElements));
            this.quantityElements = 0;
        }

        public byte[] toByteArray() throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(this.localDepth);
            dos.writeShort(this.quantityElements);
            
            for (int i = 0; i < this.maxQuantityElements; i++) {
                if (i < this.quantityElements) {
                    dos.write(this.elements.get(i).toByteArray());
                } 
                else {
                    byte[] emptyBytes = new byte[this.bytesPerElement];
                    dos.write(emptyBytes);
                }
            }

            return baos.toByteArray();
        }

        public void fromByteArray(byte[] ba) throws Exception {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);

            this.localDepth = dis.readByte();
            this.quantityElements = dis.readShort();
            this.elements = new ArrayList<>(this.maxQuantityElements);

            for (int i = 0; i < this.maxQuantityElements; i++) {
                byte[] bytes = new byte[this.bytesPerElement];
                dis.read();

                if (i < this.quantityElements) {
                    T element = constructor.newInstance();
                    element.fromByteArray(bytes);
                    this.elements.add(element);
                }
            }
        }

        public boolean create(T element) {
            if (!isFull()) {
                int i;
                for (i = this.quantityElements - 1; i >= 0 && element.hashCode() < this.elements.get(i).hashCode(); i--); // find position

                this.elements.add(i + 1, element);
                this.quantityElements++;

                return true;
            }

            return false;
        }

        public T read(int hashCode) {
            if (!isEmpty()) {
                int i;
                for (i = 0; i < this.quantityElements && hashCode > this.elements.get(i).hashCode(); i++); // find position

                if (i < this.quantityElements && hashCode == this.elements.get(i).hashCode()) return this.elements.get(i);
            }

            return null;
        }

        public boolean update(T newElement) {
            if (!isEmpty()) {
                int i;
                for (i = 0; i < this.quantityElements && newElement.hashCode() > this.elements.get(i).hashCode(); i++); // find position

                if (i < this.quantityElements && newElement.hashCode() == this.elements.get(i).hashCode()) {
                    this.elements.set(i, newElement);
                    return true;
                }
            }

            return false;
        }

        public boolean delete(int hashCode) {
            if (!isEmpty()) {
                int i;
                for (i = 0; i < this.quantityElements && hashCode > this.elements.get(i).hashCode(); i++); // find position

                if (i < this.quantityElements && hashCode == this.elements.get(i).hashCode()) {
                    this.elements.remove(i);
                    this.quantityElements--;
                    return true;
                }
            }

            return false;
        }

        public boolean isFull() {
            return this.quantityElements >= this.maxQuantityElements;
        }

        public boolean isEmpty() {
            return this.quantityElements == 0;
        }

        public String toString() {
            String str = "Profundidade local: " + this.localDepth + "\n";
            str += "Quantidade de elementos: " + this.quantityElements + "\n";
            str += "Elementos:\n";

            for (int i = 0; i < this.quantityElements; i++) {
                str += this.elements.get(i).toString() + "\n";
            }

            return str;
        }

        public int size() {
            return this.bytesPerBucket;
        }
    }

    protected class Directory {
        byte globalDepth;
        long addresses[];

        public Directory() {
            this.globalDepth = 1;
            this.addresses = new long[1];
            this.addresses[0] = 0;
        }

        public boolean updateAddress(int depth, long address) {
            if (depth < 0 || depth >= Math.pow(2, this.globalDepth)) return false;
            this.addresses[depth] = address;
            return true;
        }

        public byte[] toByteArray() throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(this.globalDepth);
            
            int quantity = (int) Math.pow(2, this.globalDepth);
            for (int i = 0; i < quantity; i++) {
                dos.writeLong(this.addresses[i]);
            }

            return baos.toByteArray();
        }

        public void fromByteArray(byte[] bytes) throws Exception {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bais);

            this.globalDepth = dis.readByte();
            int quantity = (int) Math.pow(2, this.globalDepth);
            this.addresses = new long[quantity];

            for (int i = 0; i < quantity; i++) {
                this.addresses[i] = dis.readLong();
            }
        }

        public String toString() {
            String str = "Profundidade global: " + this.globalDepth + "\n";
            str += "Endereços:\n";

            int quantity = (int) Math.pow(2, this.globalDepth);
            for (int i = 0; i < quantity; i++) {
                str += i + ": " + this.addresses[i] + "\n";
            }

            return str;
        }

        protected long address(int depth) {
            if (depth < 0 || depth >= Math.pow(2, this.globalDepth)) return -1;
            return this.addresses[depth];
        }

        protected boolean duplicate() {
            if (this.globalDepth == 127) return false;

            this.globalDepth++;
            int oldQuantity = (int) Math.pow(2, this.globalDepth - 1);
            int newQuantity = (int) Math.pow(2, this.globalDepth);
            long[] newAddresses = new long[newQuantity];
            
            for (int i = 0; i < oldQuantity; i++) {
                newAddresses[i] = this.address(i);
                newAddresses[i + oldQuantity] = this.address(i);
            }

            this.addresses = newAddresses;

            return true;
        }

        protected int hash(int key) {
            return Math.abs(key) % (int) Math.pow(2, this.globalDepth);
        }

        protected int hashDuplication(int key, int localDepth) {
            return Math.abs(key) % (int) Math.pow(2, localDepth);
        }
    }

    public ExtensibleHashTable(Constructor<T> constructor, int amountDataPerBucket, String directoryFileName, String bucketFileName) throws Exception {
        this.amountDataPerBucket = amountDataPerBucket;
        this.bucketFileName = bucketFileName;
        this.constructor = constructor;
        this.directoryFileName = directoryFileName;

        this.bucketFile = new RandomAccessFile(bucketFileName, "rw");
        this.directoryFile = new RandomAccessFile(directoryFileName, "rw");

        // Diretório ou buckets não existem
        if (this.directoryFile.length() == 0 || this.bucketFile.length() == 0) {

            // Cria diretório e bucket inicial
            this.directory = new Directory();
            byte[] bytes = this.directory.toByteArray();

            this.directoryFile.write(bytes);

            Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
            bytes = bucket.toByteArray();

            this.bucketFile.seek(0);
            this.bucketFile.write(bytes);
        }
    }

    public boolean create(T element) throws Exception {
        byte[] bytes = new byte[(int) this.directoryFile.length()];

        this.directoryFile.seek(0);
        this.directoryFile.read(bytes);

        Directory directory = new Directory();
        directory.fromByteArray(bytes);

        int hash = directory.hash(element.hashCode());

        long bucketAddress = directory.address(hash);
        
        Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
        byte[] bucketBytes = new byte[bucket.size()];

        this.bucketFile.seek(bucketAddress);
        this.bucketFile.read(bucketBytes);
        bucket.fromByteArray(bucketBytes);

        // Verifica se o elemento já existe
        if (bucket.read(hash) != null) throw new Exception("Elemento já existe!");

        // Verifica se o bucket não está cheio
        if (!bucket.isFull()) {
            bucket.create(element);
            bucketBytes = bucket.toByteArray();

            this.bucketFile.seek(bucketAddress);
            this.bucketFile.write(bucketBytes);

            return true;
        }

        // Se o bucket estiver cheio, duplica o diretório
        byte localDepth = bucket.localDepth;
        if (localDepth >= directory.globalDepth) directory.duplicate();
        byte globalDepth = directory.globalDepth;

        Bucket newBucketToOldBucketLocation = new Bucket(this.constructor, (short) this.amountDataPerBucket, localDepth + 1);
        this.bucketFile.seek(bucketAddress);
        this.bucketFile.write(newBucketToOldBucketLocation.toByteArray());

        Bucket newBucket = new Bucket(this.constructor, (short) this.amountDataPerBucket, localDepth + 1);
        long newAddress = bucketFile.length();
        bucketFile.seek(newAddress);
        bucketFile.write(newBucket.toByteArray());

        // Atualiza endereços no diretório
        int start = directory.hashDuplication(element.hashCode(), bucket.localDepth);
        int displacement = (int) Math.pow(2, localDepth);
        int max = (int) Math.pow(2, globalDepth);
        boolean swap = false;

        for (int i = start; i < max; i += displacement) {
            if (swap) directory.updateAddress(i, newAddress);
            swap = !swap;
        }

        // Atualiza o arquivo do diretório
        bytes = directory.toByteArray();

        this.bucketFile.seek(0);
        this.bucketFile.write(bytes);

        // Reinsere as chaves do antigo bucket
        for (int i = 0; i < bucket.quantityElements; i++) create(bucket.elements.get(i));

        // Insere o novo elemento
        create(element);
        
        return true;
    }

    public T read(int key) throws Exception {
        byte[] bytes = new byte[(int) this.directoryFile.length()];

        this.directoryFile.seek(0);
        this.directoryFile.read(bytes);

        Directory directory = new Directory();
        directory.fromByteArray(bytes);

        int hash = directory.hash(key);

        long bucketAddress = directory.address(hash);

        Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
        byte[] bucketBytes = new byte[bucket.size()];

        this.bucketFile.seek(bucketAddress);
        this.bucketFile.read(bucketBytes);
        bucket.fromByteArray(bucketBytes);

        return bucket.read(key);
    }

    public boolean update(T element) throws Exception {
        byte[] bytes = new byte[(int) this.directoryFile.length()];

        this.directoryFile.seek(0);
        this.directoryFile.read(bytes);

        Directory directory = new Directory();
        directory.fromByteArray(bytes);

        int hash = directory.hash(element.hashCode());

        long bucketAddress = directory.address(hash);
        
        Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
        byte[] bucketBytes = new byte[bucket.size()];

        this.bucketFile.seek(bucketAddress);
        this.bucketFile.read(bucketBytes);
        bucket.fromByteArray(bucketBytes);

        // Verifica se o elemento já existe
        if (bucket.update(element)) {
            bucketBytes = bucket.toByteArray();

            this.bucketFile.seek(bucketAddress);
            this.bucketFile.write(bucketBytes);

            return true;
        }

        return false;
    }

    public boolean delete(int key) throws Exception {
        byte[] bytes = new byte[(int) this.directoryFile.length()];

        this.directoryFile.seek(0);
        this.directoryFile.read(bytes);

        Directory directory = new Directory();
        directory.fromByteArray(bytes);

        int hash = directory.hash(key);

        long bucketAddress = directory.address(hash);

        Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
        byte[] bucketBytes = new byte[bucket.size()];

        this.bucketFile.seek(bucketAddress);
        this.bucketFile.read(bucketBytes);
        bucket.fromByteArray(bucketBytes);

        // Verifica se o elemento já existe
        if (bucket.delete(key)) {
            bucketBytes = bucket.toByteArray();
            this.bucketFile.seek(bucketAddress);
            this.bucketFile.write(bucketBytes);

            return true;
        }

        return false;
    }

    public void print() throws Exception {
        byte[] bytes = new byte[(int) this.directoryFile.length()];

        this.directoryFile.seek(0);
        this.directoryFile.read(bytes);

        this.directory = new Directory();
        directory.fromByteArray(bytes);

        System.out.println("\nDIRETÓRIO:\n   " + directory);

        System.out.println("\nBUCKETS:\n");
        
        this.bucketFile.seek(0);
        while (this.bucketFile.getFilePointer() != this.bucketFile.length()) {
            System.out.println("Endereço: " + this.bucketFile.getFilePointer());

            Bucket bucket = new Bucket(this.constructor, (short) this.amountDataPerBucket);
            byte[] bucketBytes = new byte[bucket.size()];

            this.bucketFile.read(bucketBytes);
            bucket.fromByteArray(bucketBytes);

            System.out.println(bucket.toString());
        }
    }
}
