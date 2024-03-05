import java.util.Vector;

public class Row {
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
}
