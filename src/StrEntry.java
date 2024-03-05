public class StrEntry extends Entry {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StrEntry(String name, String value) {
        super(name);
        this.value = value;
    }

    public Entry duplicate(Object value) {
        return new StrEntry(getName(), (String)value);
    }

    @Override
    public String toString() {
        return value;
    }
}
