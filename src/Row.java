import java.io.Serializable;
import java.util.Vector;

public class Row implements Serializable{
    Serializable PK;

    public Vector<Entry> columns = new Vector<Entry>();

    private int pageNum;

    public Vector<Entry> getColumns() {
        return columns;
    }

    public void setPageNum(int  pageNum) { this.pageNum = pageNum;}
    public int getPageNum() { return pageNum;}

    public Row(Serializable pk) {
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
            if(name.equalsIgnoreCase(e.getName()))
                return e;
        }
        return null;
    }
     public void update(Entry attribute, Object colValue) {
        for (Entry column : columns) {
            if (column.getName().equals(attribute.getName())) {
                if (colValue instanceof Integer) {
                    ((IntEntry)column).setValue((int)colValue);
                } else if (colValue instanceof String) {
                    ((StrEntry)column).setValue((String)colValue);
                } else if (colValue instanceof Double) {
                    ((DoubleEntry)column).setValue((Double)colValue);
                }
                break;
            }
        }
    }
}
