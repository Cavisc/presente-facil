package model.indexes;

public interface GenericExtensibleHashTable {
    public int hashCode();
    public short size();
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] ba) throws Exception;
}
