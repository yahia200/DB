import java.util.Hashtable;
import java.util.Vector;

public class Test {
    public Vector<Table> tables = new Vector<Table>();
    public Table createTable(String strTableName,
                                    String strClusteringKeyColumn,
                                    Hashtable<String,String> htblColNameType) {
        try {
            for (Table table : tables) {
                if (table.getName() == strTableName)
                    throw new DBAppException("table already exists");
            }
            Table t = new Table(strTableName, strClusteringKeyColumn, htblColNameType);
            this.tables.add(t);
            return t;
        }
        catch (DBAppException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void insertIntoTable(String strTableName,
                                       Hashtable<String,Object>  htblColNameValue) throws DBAppException {
        try {


            for (Table table : tables) {
                if (table.getName() == strTableName)
                    table.insert(htblColNameValue);
            }

        }
        catch (DBAppException e){
            System.out.println(e.getMessage());
        }
    }

    public void createIndex(String   strTableName,
                            String   strColName,
                            String   strIndexName) throws DBAppException{
        for (Table table : tables) {
            if (table.getName() == strTableName)
                table.createIndex(strColName, strIndexName);
        }

    }

    public void printInd(){
        for (Table table : tables) {
                table.printInd();
        }
    }

}
