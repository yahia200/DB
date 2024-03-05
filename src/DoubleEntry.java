public class DoubleEntry extends Entry {
    private double value;

    public int getValue() {
        return (int)value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public DoubleEntry(String name, Double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Entry duplicate(Object value) {
        return new DoubleEntry(getName(), (double)value);
    }

    @Override
    public String toString() {
        return value+"";
    }
}
