import java.io.*;
import java.util.*;

public class Table {
    private CSVHandler csv = new CSVHandler();
    private PageHandler ph = new PageHandler(this.name);
    private static final int MAX_PAGE_SIZE = 2;
    private String PK;
    private String name;
    private Vector<BPlusTree> indices = new Vector<BPlusTree>();
    Hashtable<String, String> htblColNameType;

    //! Test
    public void pp() throws Exception {
        Page page = ph.loadFirstPage();
        while (page != null) {
            if (page.size() > 0)
                System.out.println(page.toString());
            else
                System.out.println("Empty page");
            page = ph.loadNextPage(page);
        }
    }

    public void addIndex(BPlusTree index) {
        this.indices.add(index);
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
        ph.setName(this.name);
        createTable(htblColNameType);
    }


    public Table(String name, Vector<Entry> entries, String PK) throws Exception {
        this.name = name;
        System.out.println(this.name);
        this.PK = PK;
        this.attributes = entries;
        ph.setName(this.name);
    }

    void createTable(Hashtable<String, String> ht) throws Exception {
        Page firstPage = new Page(1, this.name);
        firstPage.save();
        boolean keyPresent = false;
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
        if (!keyPresent)
            throw new DBAppException("the PK is not an attribute");
    }

    private void addToPage(int index, Row row, Page page) throws Exception {
        Page prevPage = ph.loadPrevPage(page);
        Page nextPage = ph.loadNextPage(page);
        if (page.size() < MAX_PAGE_SIZE) {
            page.addRow(index, row);
            row.setPageNum(page.getNum());
            page.save();
            return;
        }

        if (index == 0 && prevPage != null) {
            if (prevPage.size() < MAX_PAGE_SIZE) {
                addToPage(prevPage.size(), row, prevPage);
                prevPage.save();
                return;
            }
        }

        Row lastRow = page.getLastRow();


        if (nextPage == null) {
            Page newPage = new Page(page.getNum() + 1, this.name);
            newPage.save();
            nextPage = ph.loadNextPage(page);

        }
        page.getRows().remove(page.size() - 1);
        page.addRow(index, row);
        row.setPageNum(page.getNum());
        page.save();
        addToPage(0, lastRow, nextPage);
    }


    private Row getNewRow(Hashtable<String, Object> ht) {
        Row row = new Row((Serializable) ht.get(PK));
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (Entry attribute : attributes) {
                if (((String) key).equalsIgnoreCase(attribute.getName())) {
                    row.add(attribute.createEntry(ht.get(key)));
                    break;
                }
            }
        }
        return row;
    }


    private boolean InPage(Page page, Hashtable<String, Object> ht) throws Exception {
        Page nextPage = ph.loadNextPage(page);
        for (Row row : page.getRows()) {
            int diff = compare(ht.get(PK), row.PK);
            if (diff == 0) {
                System.out.println("Duplicate");
                return true;
            }
            if (diff < 0) {
                Row newRow = getNewRow(ht);
                addToPage(page.getRows().indexOf(row), newRow, page);
                insertIntoIndex(ht, newRow);
                return true;
            }
        }
        if (nextPage != null)
            return false;

        if (page.size() >= MAX_PAGE_SIZE) {
            Page newPage = new Page(page.getNum() + 1, this.name);
            newPage.save();
            return false;
        }

        if ((page.size() < MAX_PAGE_SIZE)) {
            Row newRow = getNewRow(ht);
            addToPage(page.size(), newRow, page);
            insertIntoIndex(ht, newRow);
            return true;
        }


        return false;
    }


    void insert(Hashtable<String, Object> ht) throws Exception {
        Page page = ph.loadFirstPage();
        Row newRow = getNewRow(ht);
        if (page.size() == 0) {
            addToPage(0, newRow, page);
            return;
        }
        while (page != null) {
            if (!InPage(page, ht)) {
                page = ph.loadNextPage(page);
            } else {
                break;
            }
        }
        insertIntoIndex(ht, newRow);
    }


    @Override
    public String toString() {
        Page page = ph.loadFirstPage();
        String res = "";
        while (page != null) {
            res += page.toString();
            page = ph.loadNextPage(page);
        }
        return res.substring(0, res.length());
    }


    public BPlusTree createIndex(String strColName, String strIndexName) throws Exception {
        Page page = ph.loadFirstPage();
        BPlusTree tree = new BPlusTree(10, strIndexName, strColName, this.name);
        while (page != null) {
            for (Row row : page.getRows()) {
                if (row != null) {
                    Entry e = row.getEntry(strColName);
                    tree.insert(e.getValue(), row);
                }
            }
            page = ph.loadNextPage(page);
        }
        indices.add(tree);
        saveIndex();
        return tree;
    }

    public void printInd() {
        for (BPlusTree tree : indices) {
            System.out.println(tree.search(0, 100));
        }
    }

    public void saveIndex() throws Exception {
        for (BPlusTree index : indices)
            index.save();
    }

    public void insertIntoIndex(Hashtable<String, Object> ht, Row row) throws Exception {
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (BPlusTree index : indices) {
                if (((String) key).equalsIgnoreCase(index.getColName())) {
                    index.insert((Serializable) ht.get(key), row);
                    index.save();
                    break;
                }
            }
        }
    }


    void updateTable(String strClusteringKeyValue, Hashtable<String, Object> htblColNameValue) throws Exception {
        boolean rowFound = false;
        int pageNum=-1;
        Row row = null;
        Object[] keys = htblColNameValue.keySet().toArray();
        for(Object att : keys){
            BPlusTree index= checkIndex((String) att);
            if (index != null){
                row=index.search((Serializable)(htblColNameValue.get(att)));
                break;
            }

        }
        Page page = ph.loadFirstPage();
        if (row == null) {
            while (page != null) {
                row = binarySearch(strClusteringKeyValue);

                if(row!=null)
                    break;
            }
        }

        if (row != null) {
            rowFound = true;
            for (Map.Entry<String, Object> entry : htblColNameValue.entrySet()) {
                String colName = entry.getKey();
                Object colValue = entry.getValue();

                for (Entry attribute : attributes) {
                    if (colName.equals(attribute.getName())) {
                        row.update(attribute, colValue);
                        pageNum= row.getPageNum();
                        ph.savePageNum(pageNum);
                        break;
                    }
                }
            }

            if (!rowFound) {
                throw new DBAppException("Row with clustering key value " + strClusteringKeyValue + " not found in table.");
            }
            page = ph.loadNextPage(page);
        }
    }


    public void deleteFromIndex(Hashtable<String, Object> ht, Row row) {
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (BPlusTree index : indices) {
                if (((String) key).equalsIgnoreCase(index.getColName())) {
                    index.remove((Serializable) ht.get(key));
                    break;
                }
            }
        }
    }


    private int compare(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            int t = Integer.parseInt((String)o2);
            return (int) o1 - (int) o2;
        }
        else if (o1 instanceof String)
            return ((String) o1).compareToIgnoreCase((String) o2);
        else if (o1 instanceof Double) {
            Double t = Double.parseDouble((String)o2);
            return ((Double) o1).compareTo((Double) o2);
        }
        return 0;


    }

    public ArrayList<String[]> getMeta() throws IOException {
        ArrayList<String[]> lines = new ArrayList<>();
        boolean clus = false;
        String indString = "null,null";
        String[] ind;
        for (Entry attribute : attributes) {
            if (attribute.getName().equalsIgnoreCase(PK))
                clus = true;
            indString = checkIndex(attribute);
            ind = indString.split(",");
            String[] line = {this.name, attribute.getName(), attribute.getType(), "" + clus, ind[0], ind[1]};
            lines.add(line);
            clus = false;
        }
        return lines;

    }

    public void deleteFromTable(Hashtable<String, Object> ht) throws Exception {
        Row row = null;
        System.out.println("ht: " + ht);
        Iterator<Object> iterator = ht.values().iterator();
        ArrayList<Page> pagesToDelete = new ArrayList<>();

        Object[] keys = ht.keySet().toArray();
        for(Object att : keys){
            BPlusTree index= checkIndex((String) att);
            if (index != null) {
                row = index.search((Serializable) (ht.get(att)));
                Page page = ph.loadPageNum(row.getPageNum());
                page.getRows().remove(row);
                if (page.size() == 0)
                    pagesToDelete.add(page);
                page.save();
            }
        }

        while (iterator.hasNext()) {
            Object value = iterator.next();
            System.out.println("Deleting rows with value: " + value);
            Page page = ph.loadFirstPage();
            if (ht.get(PK) !=null){
                Object pk = ht.get(PK);
                page.getRows().remove(binarySearch((Serializable) pk));
                if (page.size() == 0)
                    pagesToDelete.add(page);
                page.save();
            }
            else {
            while (page != null) {
                for (int i = 0; i < page.getRows().size(); i++) {
                    for (int j = 0; j < this.attributes.size(); j++) {
                        if (value.toString().equalsIgnoreCase(page.getRows().get(i).getColumns().get(j).toString())) {
                            System.out.println("Deleting row with index: " + i);
                            page.getRows().remove(i);
                            if (page.size() == 0)
                                pagesToDelete.add(page);
                            page.save();
                            break;
                        }
                    }
                }
                page = ph.loadNextPage(page);

            }}
        }

        for (Page pageToDelete : pagesToDelete)
            ph.deletePage(pageToDelete);
    }
//            public void deleteFromTable(Hashtable<String,Object> htblColNameValue) throws DBAppException{
//                for (int i = 0; i < this.getRows().size(); i++) {
//                    if ((htblColNameValue.get(this.getPK())).equals(this.rows.get(i).PK)) {
//                        rows.remove(i);
//
//                        break;
//                    }}}


    public String checkIndex(Entry ent) {
        String col = ent.getName();
        for (BPlusTree index : indices) {
            if (index.colName.equalsIgnoreCase(col))
                return index.getName() + ",B+tree";
        }
        return "null,null";
    }


    public Row binarySearch(Serializable pk) {
        Page page = ph.loadFirstPage();
        while (page != null) {
            int l = 0;
            int r = page.size();
            while (compare(l, r) <= 0) {
                int mid = (l + r) / 2;

                // If the element is present at the
                // middle itself
                if (page.getRows().get(mid) == pk) {
                    return page.getRows().get(mid);

                    // If element is smaller than mid, then
                    // it can only be present in left subarray
                    // so we decrease our r pointer to mid - 1
                } else if (compare(page.getRows().get(mid), pk) > 0) {
                    r = mid - 1;

                    // Else the element can only be present
                    // in right subarray
                    // so we increase our l pointer to mid + 1
                } else {
                    l = mid + 1;
                }
            }

            // We reach here when element is not present
            //  in array
        }
        return null;


    }


    public BPlusTree checkIndex(String colName){
        for(BPlusTree index : indices){
            if(index.colName.equalsIgnoreCase(colName))
                return index;
        }
        return null;
    }
}


