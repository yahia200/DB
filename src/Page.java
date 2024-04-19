import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Page implements Serializable {
    private int num;
    private String tableName;
    private Vector<Row> rows = new Vector<>();
    private String filePath;

    public Page(int num, String tableName) {
        this.rows = new Vector<>();
        this.num=num;
        this.tableName=tableName;
        this.filePath= tableName+  "_" + num + ".class";
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addRow(Row row) {
        rows.add(row);
            

    }

    public void setNum(int num){
        this.num=num;
        this.filePath= this.tableName+  "_" + num + ".class";
    }

    public String getPath(){
        return  filePath;
    }


    public void addRow(int index, Row row) {
        rows.add(index, row);
            

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object row : rows) {
            sb.append(row.toString()).append(",");
        }
        return sb.substring(0,sb.length()-1);
    }

    public int getNum(){
        return  num;
    }

    public void save() throws Exception {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public static Page load(String filePath) throws Exception {
        FileInputStream fileIn = new FileInputStream(filePath);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Page page = (Page) in.readObject();
        in.close();
        fileIn.close();
        return page;
    }

    public int size() {
        return  rows.size();
    }

    public  Vector<Row> getRows() {
        return rows;
    }


    public Row  getLastRow(){
        return rows.get(rows.size()-1);
    }


}