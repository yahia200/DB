import java.util.Hashtable;
import java.util.Vector;

public class Table {
    private String PK;
    private String name;
    private Vector<Row> rows = new Vector<Row>();
    private Vector<BPlusTree> indices = new Vector<BPlusTree>();

    public void setIndecies(Vector<BPlusTree> indecies) {

        this.indices = indecies;
    }

    public Vector<BPlusTree> getIndecies() {
        return indices;
    }

    private Vector<Entry> attributes = new Vector<Entry>();

    public Vector<Entry> getAttributes() {
        return attributes;
    }

    public void setAttributes(Vector<Entry> attributes) {
        this.attributes = attributes;
    }

    public Vector<Row> getRows() {
        return rows;
    }

    public void setRows(Vector<Row> rows) {
        this.rows = rows;
    }

    public String getPK() {
        return PK;
    }

    public void setPK(String PK) {
        this.PK = PK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    Hashtable<String, String> htblColNameType;

    public Table(String name, String PK, Hashtable<String, String> htblColNameType) throws DBAppException {
        this.name = name;
        this.PK = PK;
        this.htblColNameType = htblColNameType;
        createTable(htblColNameType);
    }

    void createTable(Hashtable<String, String> ht) throws DBAppException {
        boolean keyPresent=false;
        Object[] attributes = ht.keySet().toArray();

        for (Object attribute : attributes) {
            if (attribute == this.PK)
                keyPresent = true;
            if ((String) ht.get(attribute) == "java.lang.Integer") {
                this.attributes.add(new IntEntry(((String) attribute), 0));
            } else if ((String) ht.get(attribute) == "java.lang.String") {
                this.attributes.add(new StrEntry(((String) attribute), ""));
            } else if ((String) ht.get(attribute) == "java.lang.double") {
                this.attributes.add(new DoubleEntry(((String) attribute), 0.0));
            }
        }
        if(!keyPresent)
            throw new DBAppException("the PK is not an attribute");
    }

    boolean duplicateRow(Object value) {
        for (Row row : rows) {
            if (value.equals(row.PK)) {
                return true;
            }
        }
        return false;
    }

    void insert(Hashtable<String, Object> ht) throws DBAppException {
        Object[] keys = ht.keySet().toArray();
        if (duplicateRow(ht.get(PK))){
            throw new DBAppException("Duplicate PK");
        }
        else {
            Row row = new Row(ht.get(PK));
            for (Object key : keys) {
                for (Entry attribute : attributes) {
                    if ((String) key == attribute.getName()) {
                        row.add(attribute.duplicate(ht.get(key)));
                        break;
                    }
                }
            }
            updateIndex(ht, row);
            rows.add(row);
        }
    }

    @Override
    public String toString() {
        String res="";
        for(Row row : rows){
            res += row.toString() +"\n" ;
        }
        return res.substring(0,res.length()-2);
    }


    public BPlusTree createIndex(String   strColName,
                                String   strIndexName){
            BPlusTree tree = new BPlusTree(4, strIndexName, strColName);
            for(Row row:rows){
               Entry e = row.getEntry(strColName);
               tree.insert(e.getValue(),row);
            }
            indices.add(tree);
            return tree;

    }

    public void printInd(){
        for (BPlusTree tree : indices){
            System.out.println(tree.search(0,2));
        }
    }

    public void updateIndex(Hashtable<String, Object> ht, Row row){
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (BPlusTree index : indices) {
                if ((String) key == index.getColName()) {
                    index.insert(ht.get(key), row);
                    break;
                }
            }
        }
    }

 public void deleteFromTable(Hashtable<String,Object> htblColNameValue) throws DBAppException{
        for (int i = 0; i < this.getRows().size(); i++) {
            if ((htblColNameValue.get(this.getPK())).equals(this.rows.get(i).PK)) {
                rows.remove(i);
                System.out.println("Row Deleted");
                break;
            }
        }
    }
}
