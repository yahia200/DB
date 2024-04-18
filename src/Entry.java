import java.io.Serializable;

public abstract class Entry implements Serializable{
    private String name;

    public Entry(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public abstract Entry createEntry(Object o);

    public abstract int getValue();

    public abstract String getType();

}