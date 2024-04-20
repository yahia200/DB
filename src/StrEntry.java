public class StrEntry extends Entry {
    private String value;

    public int getValue() {
        int ascii = 0;
        for(int i=0;i<value.length();i++) {
            ascii += (int) value.charAt(i);
        }
        return ascii;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public StrEntry(String name, String value) {
        super(name);
        this.value = value;
    }

    public Entry createEntry(Object value) {
        return new StrEntry(getName(), (String)value);
    }

    @Override
    public String toString() {
        return value;
    }

    public String getType(){
        return "java.lang.String";
    }


}
