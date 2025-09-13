package model.indexes;

public interface GenericBPlusTree<T> {
    public short size();
    public int compareTo(T element);
    public T clone();
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] bytes) throws Exception;
}
