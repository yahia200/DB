import java.io.*;
import java.util.*;
import BTree.*;

public class Table {
    private CSVHandler csv = new CSVHandler();
    private PageHandler ph = new PageHandler(this.name);
    private static final int MAX_PAGE_SIZE = 4;
    private String PK;
    private String name;
    private Vector<BTree> indices = new Vector<BTree>();
    Hashtable<String, String> htblColNameType;


    public void addIndex(BTree index) {
        this.indices.add(index);
    }

    public void setIndecies(Vector<BTree> indecies) {

        this.indices = indecies;
    }

    public Vector<BTree> getIndecies() {
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
        if(binarySearch((Serializable) ht.get(PK)) != null)
            return true;
        for (Row row : page.getRows()) {
            int diff = compare(ht.get(PK), row.PK);
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


    public BTree createIndex(String strColName, String strIndexName) throws Exception {
        Page page = ph.loadFirstPage();
        Vector<Row> rows = new Vector<>();
        BTree tree = new BTree(strIndexName, strColName, this.name);
        while (page != null) {
            for (Row row : page.getRows()) {
                if (row != null) {
                    rows.add(row);
                    Entry e = row.getEntry(strColName);
                    if (tree.search(e.getValue()) != null){
                        ((Vector<Row>)tree.search(e.getValue())).add(row);
                    }
                    else
                        tree.insert(e.getValue(), rows);
                }
            }
            page = ph.loadNextPage(page);
        }
        indices.add(tree);
        saveIndex();
        return tree;
    }

    public void printInd() {
        for (BTree tree : indices) {
            tree.print();
        }
    }

    public void saveIndex() throws Exception {
        for (BTree index : indices)
            index.save();
    }

    public void insertIntoIndex(Hashtable<String, Object> ht, Row row) throws Exception {
        Object[] keys = ht.keySet().toArray();
        for (Object key : keys) {
            for (BTree index : indices) {
                if (((String) key).equalsIgnoreCase(index.getColName())) {
                    index.insert((Comparable) (ht.get(key)), row);
                    index.save();
                    break;
                }
            }
        }
    }


    void updateTable(String strClusteringKeyValue, Hashtable<String, Object> htblColNameValue) throws Exception {
        boolean rowFound = false;
        int pageNum=-1;
        int rowInd = -1;
        Row row = null;
        Object[] keys = htblColNameValue.keySet().toArray();
        for(Object att : keys){
            BTree index= checkIndex((String) att);
            if (index != null){
                row= (Row) index.search((Comparable) htblColNameValue.get(att));
                break;
            }

        }
        Page page = ph.loadFirstPage();
        if (row == null) {
            while (page != null) {
                row = binarySearch(strClusteringKeyValue);
                rowInd = binarySearchIndex(strClusteringKeyValue);

                if(row!=null || rowInd != -1)
                    break;
            }
        }

        if (row != null || rowInd !=-1) {
            rowFound = true;
            for (Map.Entry<String, Object> entry : htblColNameValue.entrySet()) {
                String colName = entry.getKey();
                Object colValue = entry.getValue();

                for (Entry attribute : attributes) {
                    if (colName.equals(attribute.getName())) {
                        pageNum= row.getPageNum();
                        Page p = ph.loadPageNum(pageNum);
                        p.getRows().get(rowInd).update(attribute, colValue);
                        p.save();
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
            for (BTree index : indices) {
                if (((String) key).equalsIgnoreCase(index.getColName())) {
                    index.delete((Comparable) ht.get(key));
                    break;
                }
            }
        }
    }
    


    private int compare(Object o1, Object o2) {
        if (o1 instanceof Integer) {
            if (o2 instanceof String)
                o2 = Integer.parseInt((String) o2);
            return (int) o1 - (int) o2;
        }else if (o1 instanceof String)
            return ((String) o1).compareToIgnoreCase((String) o2);
        else if (o1 instanceof Double) {
            if (o2 instanceof String)
                o2 = Double.parseDouble((String) o2);
            return ((Double) o1).compareTo((Double) o2);
        }
        return 0;

    }
        public PageHandler getPh () {
            return ph;
        }

        public void setPh (PageHandler ph){
            this.ph = ph;
        }

        public ArrayList<String[]> getMeta () throws IOException {
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

        public void deleteFromTable (Hashtable < String, Object > ht) throws Exception {
            ArrayList<Row> rows = new ArrayList<>();
            Iterator<Object> iterator = ht.values().iterator();
            ArrayList<Page> pagesToDelete = new ArrayList<>();

            Object[] keys = ht.keySet().toArray();
            for (Object att : keys) {
                BTree index = checkIndex((String) att);
                if (index != null) {
                    rows = (ArrayList<Row>) index.search((Comparable) ht.get(att));
                    for (Row row : rows){
                        Page page = ph.loadPageNum(row.getPageNum());
                        page.getRows().remove(row);
                        if (page.size() == 0)
                            pagesToDelete.add(page);
                        page.save();
                    }
                }
            }
            boolean pkRemoved = false;
            while (iterator.hasNext()) {
                Object value = iterator.next();
                Page page = ph.loadFirstPage();
                if (ht.get(PK) != null && !pkRemoved) {
                    Object pk = ht.get(PK);
                    page.getRows().remove(binarySearch((Serializable) pk));
                    pkRemoved = true;
                    if (page.size() == 0)
                        pagesToDelete.add(page);
                    page.save();
                } else {
                    while (page != null) {
                        for (int i = 0; i < page.getRows().size(); i++) {
                            for (int j = 0; j < this.attributes.size(); j++) {
                                if (value.toString().equalsIgnoreCase(page.getRows().get(i).getColumns().get(j).toString())) {
                                    page.getRows().remove(i--);
                                    if (page.size() == 0)
                                        pagesToDelete.add(page);
                                    page.save();
                                    break;
                                }
                            }
                        }
                        page = ph.loadNextPage(page);

                    }
                }
            }

            for (Page pageToDelete : pagesToDelete)
                ph.deletePage(pageToDelete);
        }


        public String checkIndex (Entry ent){
            String col = ent.getName();
            for (BTree index : indices) {
                if (index.getColName().equalsIgnoreCase(col))
                    return index.getName() + ",B+tree";
            }

            return "null,null";
        }


        public Row binarySearch (Serializable pk){
            Page page = ph.loadFirstPage();
            while (page != null) {
                int l = 0;
                int r = page.size() - 1;
                while (compare(l, r) <= 0) {
                    int mid = (l + r) / 2;

                    // If the element is present at the
                    // middle itself
                    if (compare(page.getRows().get(mid).PK, pk) == 0) {
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
                page = ph.loadNextPage(page);
            }
            return null;


        }


        public int binarySearchIndex (Serializable pk){
            Page page = ph.loadFirstPage();
            while (page != null) {
                int l = 0;
                int r = page.size() - 1;
                while (compare(l, r) <= 0) {
                    int mid = (l + r) / 2;

                    // If the element is present at the
                    // middle itself
                    if (compare(page.getRows().get(mid).PK, pk) == 0) {
                        return mid;

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
                page = ph.loadNextPage(page);
            }
            return -1;


        }


        public BTree checkIndex (String colName){
            for (BTree index : indices) {
                if (index.getColName().equalsIgnoreCase(colName))
                    return index;
            }
            return null;
        }

    }




