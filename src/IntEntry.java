public class IntEntry extends Entry {

    int value;

    public void setValueint(int valueint) {
        this.value = valueint;
    }

    public IntEntry(String name, int valueint) {
        super(name);

        this.value = valueint;
    }
    public IntEntry createEntry(Object value){return new IntEntry(getName(), (int)value);
    }


    public Comparable getValue() {
        return value;
    }

    @Override
    public String toString() {

        return this.value+"";
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getType(){
        return "java.lang.Integer";
    }

}
