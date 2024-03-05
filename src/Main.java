
import java.util.Hashtable;
import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {



    public static void main(String[] args) throws DBAppException {
        Test test = new Test();
        String strTableName = "Student";

        Hashtable htblColNameType = new Hashtable( );
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.double");
        Table t =test.createTable( strTableName, "id", htblColNameType );
        test.createIndex( strTableName, "gpa", "gpaIndex" );

        Hashtable htblColNameValue = new Hashtable( );
        htblColNameValue.put("id", new Integer( 2343432 ));
        htblColNameValue.put("name", new String("Ahmed Noor" ) );
        htblColNameValue.put("gpa", new Double( 0.95 ) );
        test.insertIntoTable( strTableName , htblColNameValue );

        htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 453455 ));
        htblColNameValue.put("name", new String("Ali Noor" ) );
        htblColNameValue.put("gpa", new Double( 0.97 ) );
        test.insertIntoTable( strTableName , htblColNameValue );

        htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 63749 ));
        htblColNameValue.put("name", new String("Dalia Noor" ) );
        htblColNameValue.put("gpa", new Double( 1.25 ) );
        test.insertIntoTable( strTableName , htblColNameValue );

        htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 23498 ));
        htblColNameValue.put("name", new String("John Noor" ) );
        htblColNameValue.put("gpa", new Double( 1.5 ) );
        test.insertIntoTable( strTableName , htblColNameValue );

        htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 78452 ));
        htblColNameValue.put("name", new String("Zaky Noor" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
        if(t != null)
            System.out.println(t.toString());
        else
            System.out.println("table is null");

        test.printInd();
    }

}