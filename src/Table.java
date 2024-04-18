import java.io.*;
import java.util.*;

public class Table {
    private CSVHandler csv = new CSVHandler();
    private PageHandler ph = new PageHandler(this.name);
    private static final int MAX_PAGE_SIZE = 10;
    private String PK;
    private String name;
    private Vector<Row> rows = new Vector<Row>();
    private Vector<BPlusTree> indices = new Vector<BPlusTree>();
    public Vector<Page> pages = new Vector<Page>();
    Hashtable<String, String> htblColNameType;

    //! Test
    public void pp(){
        for (Page page : pages){
            System.out.println(page.toString());
        }
    }


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


    public Table(String name, String PK, Hashtable<String, String> htblColNameType) throws Exception {
        this.name = name;
        this.PK = PK;
        this.htblColNameType = htblColNameType;
        Page firstPage = new Page(1, this.name);
        firstPage.save();
        createTable(htblColNameType);
        ph.setName(name);
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

    private void addToPage(int index, Row row, Page page) throws Exception{
        Page prevPage = ph.loadPrevPage(page);
        Page nextPage = ph.loadNextPage(page);
        if (page.size() < MAX_PAGE_SIZE){
            page.addRow(index,row);
            page.save();
            return;
        }
        
        if (index == 0 && prevPage != null){
            if ( prevPage.size() < MAX_PAGE_SIZE) {
                addToPage(prevPage.size(), row, prevPage);
                prevPage.save();
                return;
            }
        }
        
        Row lastRow = page.getLastRow();

        
        if (nextPage == null) {
            Page newPage = new Page(page.getNum()+1, this.name);
            newPage.save();
            nextPage = ph.loadNextPage(page);

        }

        addToPage(0, lastRow, nextPage);
    }


    private Row getNewRow(Hashtable<String, Object> ht){
        Row row = new Row(ht.get(PK));
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (Entry attribute : attributes) {
                if ((String) key == attribute.getName()) {
                    row.add(attribute.createEntry(ht.get(key)));
                    break;
                }
            }
        }
        return row;
    }




    private boolean InPage(Page page, Hashtable<String, Object> ht) throws Exception {
        Page nextPage = ph.loadNextPage(page);
        for (Row row : page.getRows()){
            int diff = compare(ht.get(PK), row.PK);
            if (diff == 0){
                System.out.println("Duplicate");
                return true;
            }
            if (diff < 0){
                Row newRow = getNewRow(ht);
                addToPage(page.getRows().indexOf(row), newRow, page);
                updateIndex(ht, newRow);
                return true;
            }
        }
            
        if (nextPage == null){
            Row newRow = getNewRow(ht);
            addToPage(page.size(), newRow, page);
            updateIndex(ht, newRow);
            return true;
        }

        
        return false;
}



    void insert(Hashtable<String, Object> ht) throws Exception {
        Page page = ph.loadFirstPage();
        if (page.size() == 0){
            Row newRow = getNewRow(ht);
            addToPage(0, newRow, page);
            return;
    }
        while (page  != null) {
            if(!InPage(page, ht)){
                page = ph.loadNextPage(page);
            }
            else{
                break;
            }   
        }
    }


    @Override
    public String toString() {
        Page page = ph.loadFirstPage();
        String res="";
        while (page !=null){
                res += page.toString();
                page = ph.loadNextPage(page);
        }
        return res.substring(0,res.length());
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
                
                break;
            }
        }
    }
     void updateTable(String strClusteringKeyValue, Hashtable<String, Object> htblColNameValue) throws DBAppException {
        boolean rowFound = false;

        for (Row row : rows) {
            if (strClusteringKeyValue.equals(row.PK)){
                rowFound = true;
                for (Map.Entry<String, Object> entry : htblColNameValue.entrySet()) {
                    String colName = entry.getKey();
                    Object colValue = entry.getValue();

                    for (Entry attribute : attributes) {
                        if (colName.equals(attribute.getName())) {
                            row.update(attribute, colValue);
                            break;
                        }
                    }
                }
                break;
            }
        }

        if (!rowFound) {
            throw new DBAppException("Row with clustering key value " + strClusteringKeyValue + " not found in table.");
        }
    }

    

   

    

    private int compare(Object o1, Object o2) {
        if (o1 instanceof Integer)
            return  (int)o1 - (int)o2;
        else if (o1 instanceof String)
            return   ((String) o1).compareToIgnoreCase((String) o2);
        else if (o1 instanceof Double)
            return   ((Double) o1).compareTo((Double) o2);
        return 0;


        }

    public void saveMeta() throws IOException{
        ArrayList<String[]> lines = new ArrayList<>();
        boolean clus = false;
        String indString = "null,null";
        String[] ind;
        for (Entry attribute : attributes){
            if (attribute.getName().equalsIgnoreCase(PK))
                clus = true;
            indString = checkIndex(attribute);
            ind = indString.split(",");
            String[] line = {this.name,attribute.getName(),attribute.getType(),""+clus,ind[0], ind[1]};
            lines.add(line);
            clus=false;
        }

        csv.saveCSV(lines);

    }




    public String checkIndex(Entry ent){
        String col = ent.getName();
        for (BPlusTree index : indices){
            if (index.colName.equalsIgnoreCase(col))
                return index.getName()+",B+tree";
        }
        return "null,null";
    }

}
