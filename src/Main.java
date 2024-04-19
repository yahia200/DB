import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static Test test = new Test();
    static String strTableName = "Student";

    @SuppressWarnings({ "removal", "unchecked", "rawtypes" })
    public static void main(String[] args) throws Exception {
        test.init();
        Hashtable htblColNameType = new Hashtable( );
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.double");
        //Table t =test.createTable( strTableName, "id", htblColNameType );
        //Table t2 =test.createTable( strTableName+"test", "id", htblColNameType );
        insert();
        delete();
        test.tables.get(0).pp();
        test.tables.get(1).pp();
       



    }

    public static void delete() throws Exception{
         Hashtable<String, Object> tester = new Hashtable<>();
        tester.put("id", 4);
        tester.put("id", 16);
        tester.put("gpa", 1.5);
        tester.put("name", "Dalia Noor");
        test.deleteFromTable("Student",tester);
    }


    public static void insert() throws Exception{

        Hashtable htblColNameValue = new Hashtable( );
         htblColNameValue.put("id", new Integer( 2 ));
         htblColNameValue.put("name", new String("Ahmed Noor" ) );
         htblColNameValue.put("gpa", new Double( 0.95 ) );
         test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
         htblColNameValue.put("id", new Integer( 4 ));
         htblColNameValue.put("name", new String("Ali Noor" ) );
         htblColNameValue.put("gpa", new Double( 0.97 ) );
         test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
         htblColNameValue.put("id", new Integer( 6 ));
         htblColNameValue.put("name", new String("Dalia Noor" ) );
         htblColNameValue.put("gpa", new Double( 1.25 ) );
         test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
         htblColNameValue.put("id", new Integer( 12 ));
         htblColNameValue.put("name", new String("John Noor" ) );
         htblColNameValue.put("gpa", new Double( 1.5 ) );
         test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 16 ));
        htblColNameValue.put("name", new String("Dalia Noor" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 10 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 22 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 24 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 26 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 5 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 2 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 89 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 3 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 0.88 ) );
        test.insertIntoTable( strTableName , htblColNameValue );
         htblColNameValue.clear( );
        htblColNameValue.put("id", new Integer( 9 ));
        htblColNameValue.put("name", new String("Zaky16" ) );
        htblColNameValue.put("gpa", new Double( 1.5 ) );
        test.insertIntoTable( strTableName+"test" , htblColNameValue );
    }



}