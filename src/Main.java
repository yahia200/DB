import java.io.*;
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
     //   Table t =test.createTable( strTableName, "id", htblColNameType );
        //test.createIndex(strTableName, "id", "idIndex");
        //test.saveCSV();
        //Table t2 =test.createTable( strTableName+"test", "id", htblColNameType );
        //insert();
       // delete();
     //  test.tables.get(0).pp();
        //test.tables.get(1).pp();
        //System.out.println(test.tables.size());
        //test.tables.get(0).printInd();
    	 
    	      
    	        // tests
    	      /* String x = "apple";
    	       String y = "banana";
    	       System.out.println((int)x.charAt(0));
    	       System.out.println((int)y.charAt(0));
    	       Entry entry = new StrEntry(null, x);
    	       System.out.println(entry.getValue());
    	       System.out.println(x.compareTo(y));*/
    	        //SQLTerm[] arrSQLTerms;
    	/*        SQLTerm[] arrSQLTerms = new SQLTerm[3];
    	        arrSQLTerms[0]=new SQLTerm();
    	        arrSQLTerms[1]=new SQLTerm();
    	        arrSQLTerms[2]=new SQLTerm();
    	        arrSQLTerms[0]._strTableName = "Student";
    	        arrSQLTerms[0]._strColumnName= "name";
    	        arrSQLTerms[0]._strOperator = "=";
    	        arrSQLTerms[0]._objValue = "John Noor";
    	        arrSQLTerms[1]._strTableName = "Student";
    	        arrSQLTerms[1]._strColumnName= "gpa";
    	        arrSQLTerms[1]._strOperator = "<";
    	        arrSQLTerms[1]._objValue = new Double( 0.7 );
    	        arrSQLTerms[2]._strTableName = "Student";
    	        arrSQLTerms[2]._strColumnName= "id";
    	        arrSQLTerms[2]._strOperator = "=";
    	        arrSQLTerms[2]._objValue = new Integer( 78452 );
    	        String[]strarrOperators = new String[2];
    	        strarrOperators[0] = "OR";
    	        strarrOperators[1] = "XOR";
    	     ArrayList<Row>  te =test.selectFromTableHelper(arrSQLTerms[0]);
    	     ArrayList<Row>  ts =test.selectFromTableHelper(arrSQLTerms[1]);
    	     ArrayList<Row>  tw =test.selectFromTableHelper(arrSQLTerms[2]);
    	     
    		   System.out.println("-----------------");
    	     //while(tee.hasNext())
    		   System.out.println(te.toString());
    		   System.out.println(ts.toString());
    		   System.out.println(tw.toString());
    		 //  te.retainAll(ts);
    		   //System.out.println(te.toString());
    		     Iterator<Row>  tk =test.selectFromTable(arrSQLTerms, strarrOperators);
    			  while(tk.hasNext()) 
    		     System.out.println(tk.next());
    			  
    	       
    	    */



    }

    public static void delete() throws Exception{
         Hashtable<String, Object> tester = new Hashtable<>();
       // tester.put("id", 4);
      //  tester.put("id", 16);
      //  tester.put("gpa", 1.5);
        tester.put("name", "Dalia Noor");
        tester.put("name", "Ali Noor");
        tester.put("name", "Ahmed Noor");
        tester.put("name","Zaky16" );
        test.deleteFromTable("Student",tester);
    }


    public static void insert() throws Exception{

    	Hashtable htblColNameType = new Hashtable( ); 
    	htblColNameType.put("id", "java.lang.Integer"); 
    	htblColNameType.put("name", "java.lang.String"); 
    	htblColNameType.put("gpa", "java.lang.double"); 
    	test.createTable( strTableName, "id", htblColNameType); 
    	//test.createIndex( strTableName, new String[] {"gpa"} ); 
    	 
    	Hashtable htblColNameValue = new Hashtable( ); 
    	htblColNameValue.put("id", new Integer( 2343432 )); 
    	htblColNameValue.put("name", new String("Ahmed Noor" ) ); 
    	htblColNameValue.put("gpa", new Double( 0.95 ) ); 
    	test.insertIntoTable( strTableName , htblColNameValue ); 
    	 
    	htblColNameValue.clear( ); 
    	htblColNameValue.put("id", new Integer( 453455 )); 
    	htblColNameValue.put("name", new String("Ahmed Noor" ) ); 
    	htblColNameValue.put("gpa", new Double( 0.95 ) ); 
    	test.insertIntoTable( strTableName , htblColNameValue ); 
    	htblColNameValue.clear( ); 
    	htblColNameValue.put("id", new Integer( 5674567 )); 
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
    }



}