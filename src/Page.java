import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class Page implements Serializable {
    private int num;
    private Vector<Row> rows = new Vector<>();
    private String filePath;

    public Page(int num) {
        this.rows = new Vector<>();
        this.num=num;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addRow(Row row) {
        rows.add(row);
            

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object row : rows) {
            sb.append(row.toString()).append(",");
        }
        return sb.substring(0,sb.length()-1);
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
}