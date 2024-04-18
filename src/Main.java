
import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {



    @SuppressWarnings({ "removal", "unchecked", "rawtypes" })
    public static void main(String[] args) throws Exception {
        Test test = new Test();
        test.init();
        String strTableName = "Student";
        Hashtable htblColNameType = new Hashtable( );
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.double");
        Table t =test.createTable( strTableName, "id", htblColNameType );
        // //test.createIndex( strTableName, "gpa", "gpaIndex" );
        // Hashtable htblColNameValue = new Hashtable( );
        // htblColNameValue.put("id", new Integer( 2 ));
        // htblColNameValue.put("name", new String("Ahmed Noor" ) );
        // htblColNameValue.put("gpa", new Double( 0.95 ) );
        // test.insertIntoTable( strTableName , htblColNameValue );
        // htblColNameValue.clear( );
        // htblColNameValue.put("id", new Integer( 4 ));
        // htblColNameValue.put("name", new String("Ali Noor" ) );
        // htblColNameValue.put("gpa", new Double( 0.97 ) );
        // test.insertIntoTable( strTableName , htblColNameValue );
        // htblColNameValue.clear( );
        // htblColNameValue.put("id", new Integer( 6 ));
        // htblColNameValue.put("name", new String("Dalia Noor" ) );
        // htblColNameValue.put("gpa", new Double( 1.25 ) );
        // test.insertIntoTable( strTableName , htblColNameValue );
        // htblColNameValue.clear( );
        // htblColNameValue.put("id", new Integer( 12 ));
        // htblColNameValue.put("name", new String("John Noor" ) );
        // htblColNameValue.put("gpa", new Double( 1.5 ) );
        // test.insertIntoTable( strTableName , htblColNameValue );
        // htblColNameValue.clear( );
        // htblColNameValue.put("id", new Integer( 7 ));
        // htblColNameValue.put("name", new String("Zaky Noor" ) );
        // htblColNameValue.put("gpa", new Double( 0.88 ) );
        // test.insertIntoTable( strTableName , htblColNameValue );
        test.tables.get(0).pp();
        
    }

}