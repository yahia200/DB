public class IntEntry extends Entry {

    int valueint;

    public int getValueint() {
        return valueint;
    }

    public void setValueint(int valueint) {
        this.valueint = valueint;
    }

    public IntEntry(String name, int valueint) {
        super(name);
        this.valueint = valueint;
    }
    public IntEntry duplicate(Object value){return new IntEntry(getName(), (int)value);
    }

    @Override
    public String toString() {

        return valueint+"";
    }
}
