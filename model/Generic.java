package model;

public interface Generic {
    public void setId(int id);
    public int getId();
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] ba) throws Exception;
}
