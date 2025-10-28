package model.indexes;

public class InvertedListElement implements Comparable<InvertedListElement>, Cloneable {
    private int id;
    private float frequency;

    public InvertedListElement() {
        this(-1, 0);
    }

    public InvertedListElement(int id, float frequency) {
        this.id = id;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "InvertedListElement{" + "id=" + id + ", frequency=" + frequency +'}';
    }

    @Override
    public InvertedListElement clone() {
        try {
            return (InvertedListElement) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int compareTo(InvertedListElement other) {
        return Integer.compare(this.id, other.id);
    }
}
