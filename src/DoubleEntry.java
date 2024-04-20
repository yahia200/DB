public class DoubleEntry extends Entry {
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public DoubleEntry(String name, Double value) {
        super(name);
        this.value = value;
    }

    @Override
    public Entry createEntry(Object value) {
        return new DoubleEntry(getName(), (double)value);
    }

    @Override
    public String toString() {
        return value+"";
    }

    public String getType(){
        return "java.lang.Double";
    }
}
