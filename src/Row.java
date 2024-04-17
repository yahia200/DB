import java.io.Serializable;
import java.util.Vector;

public class Row implements Serializable{
    Object PK;
    private Vector<Entry> columns = new Vector<Entry>();

    public Vector<Entry> getColumns() {
        return columns;
    }

    public Row(Object pk) {
        PK = pk;
    }

    public void add(Entry e){
        columns.add(e);
    }

    public void setColumns(Vector<Entry> columns) {
        this.columns = columns;
    }

    public Row(Vector<Entry> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        String res="";
        for(Entry col : columns){
            res += col.toString()+",";
        }
        return res.substring(0,res.length()-1);
    }

    public Entry getEntry(String name){
        for (Entry e : columns){
            if(name == e.getName())
                return e;
        }
        return null;
    }
     public void update(Entry attribute, Object colValue) {
        for (Entry column : columns) {
            if (column.getName().equals(attribute.getName())) {
                if (colValue == "java.lang.Integer") {
                   column=new IntEntry(column.getName(),(int)colValue);
                } else if (colValue == "java.lang.String") {
                    column=new StrEntry(column.getName(),(String)colValue);
                } else if (colValue == "java.lang.double") {
                    column=new DoubleEntry(column.getName(),(Double)colValue);
                }
                break;
            }
        }
    }
}
