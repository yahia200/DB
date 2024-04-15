public class IntEntry extends Entry {

    int value;

    public void setValueint(int valueint) {
        this.value = valueint;
    }

    public IntEntry(String name, int valueint) {
        super(name);

        this.value = valueint;
    }
    public IntEntry duplicate(Object value){return new IntEntry(getName(), (int)value);
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {

        return this.value+"";
    }
}
