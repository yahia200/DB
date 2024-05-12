
/** * @author Wael Abouelsaadat */ 

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Properties;


public class DBApp {

	CSVHandler csv = new CSVHandler();
	public Vector<Table> tables = new Vector<Table>();

	public DBApp( ){
		
	}

	// this does whatever initialization you would like 
	// or leave it empty if there is no code you want to 
	// execute at application startup 
	public void init( ) throws Exception{

		try {
			tables = csv.ReadCSV();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}


	// following method creates one table only
	// strClusteringKeyColumn is the name of the column that will be the primary
	// key and the clustering column as well. The data type of that column will
	// be passed in htblColNameType
	// htblColNameValue will have the column name as key and the data 
	// type as value
	public Table createTable(String strTableName,
							 String strClusteringKeyColumn,
							 Hashtable<String,String> htblColNameType) throws Exception {
		try {
			for (Table table : tables) {
				if (table.getName().equalsIgnoreCase(strTableName))
					throw new DBAppException("table already exists");
			}
			Table t = new Table(strTableName, strClusteringKeyColumn, htblColNameType);
			this.tables.add(t);
			saveCSV();
			return t;
		}
		catch (DBAppException e){
		}
		return null;
	}


	// following method creates a B+tree index 
	public void createIndex(String   strTableName,
							String   strColName,
							String   strIndexName) throws Exception{
		for (Table table : tables) {
			if (table.getName().equalsIgnoreCase(strTableName))
				table.createIndex(strColName, strIndexName);

		}
		saveCSV();
	}


	// following method inserts one row only. 
	// htblColNameValue must include a value for the primary key
	public void insertIntoTable(String strTableName,
								Hashtable<String,Object>  htblColNameValue) throws Exception {
		try {
			for (Table table : tables) {
				if (table.getName().equalsIgnoreCase(strTableName)){
					table.insert(htblColNameValue);
				}
			}

		}
		catch (DBAppException e){
		}
	}


	// following method updates one row only
	// htblColNameValue holds the key and new value 
	// htblColNameValue will not include clustering key as column name
	// strClusteringKeyValue is the value to look for to find the row to update.
	public void updateTable (String strTableName,
							 String strClusteringKeyValue,
							 Hashtable < String, Object > htblColNameValue   ) throws Exception {

		for (Table table : tables) {
			if (table.getName().equalsIgnoreCase(strTableName))
				table.updateTable(strClusteringKeyValue, htblColNameValue);
		}
	}


	// following method could be used to delete one or more rows.
	// htblColNameValue holds the key and value. This will be used in search 
	// to identify which rows/tuples to delete. 	
	// htblColNameValue enteries are ANDED together
	public void deleteFromTable(String strTableName, Hashtable<String,Object> ht) throws Exception{
		for (Table table : tables) {
			if (table.getName().equals(strTableName)) {
				table.deleteFromTable(ht);
				break;
			}
		}
	}

	public void saveCSV() throws IOException {
		ArrayList<String[]> meta = new ArrayList<>() ;
		for (Table table : tables){
			meta.addAll(table.getMeta());
		}
		csv.saveCSV(meta);
	}


	public Iterator<Row> selectFromTable (SQLTerm[]arrSQLTerms,
										  String[]strarrOperators)
			throws DBAppException {
		String tableName = arrSQLTerms[0]._strTableName;
		for (int i = 1; i < arrSQLTerms.length - 1; i++) {
			if (!(tableName.equals(arrSQLTerms[i]._strTableName)))
				throw new DBAppException("cant select from different pages");
		}
		if (arrSQLTerms.length != (strarrOperators.length + 1))
			throw new DBAppException("wrong sql statement");

		ArrayList<ArrayList<Row>> Operands = new ArrayList<>();
		for (SQLTerm arrSQLTerm : arrSQLTerms) {
			Operands.add(selectFromTableHelper(arrSQLTerm));
		}

		ArrayList<String> operators = new ArrayList<>();
		for (int i = 0; i < strarrOperators.length; i++) {
			operators.add(strarrOperators[i]);
		}
		while (Operands.size() != 1) {

			if (operators.contains("AND")) {
				for (int i=0; i<operators.size();i++) {
					if (operators.get(i).equalsIgnoreCase("AND")) {
						operators.remove(i);
						ArrayList<Row> result = AND(Operands.remove(i), Operands.remove(i));
						Operands.add(i, result);
					}
				}
			}
			else if (operators.contains("XOR")) {
				for (int i=0; i<operators.size();i++) {
					if (operators.get(i).equalsIgnoreCase("XOR")) {
						operators.remove(i);
						ArrayList<Row> result = XOR(Operands.remove(i), Operands.remove(i));
						Operands.add(i, result);
					}
				}
			}
			else if (operators.contains("OR")) {
				for (int i=0; i<operators.size();i++) {
					if (operators.get(i).equalsIgnoreCase("OR")) {
						operators.remove(i);
						ArrayList<Row> result = OR(Operands.remove(i), Operands.remove(i));
						Operands.add(i, result);
					}
				}
			}
		}
		return Operands.get(0).iterator();
	}



	public ArrayList<Row> OR(ArrayList<Row>r1,ArrayList<Row>r2){
		boolean dup = false;
		ArrayList<Row> r3 = new ArrayList<>();
		r3.addAll(r1);
		for(Row row :r2 ){
			for(Row row2 : r3){
				if(row.PK.equals(row2.PK)){
					dup = true;
					break;
				}
			}
			if(!dup){
				r3.add(row);
				dup = false;
			}

		}
		return r3;
	}
	public ArrayList<Row> AND(ArrayList<Row>r1,ArrayList<Row>r2){
		ArrayList<Row> r3 = new ArrayList<>();
		r1.retainAll(r2);
		return r1;
	}
	public ArrayList<Row> XOR(ArrayList<Row>r1,ArrayList<Row>r2){
		ArrayList<Row> r3 = new ArrayList<>();
		r3.clear();
		r3.addAll(r1);
		r3.addAll(r2);
		r1.retainAll(r2);
		r3.removeAll(r1);
		r3.removeAll(r1);
		return r3;
	}

	public ArrayList<Row> selectFromTableHelper(SQLTerm arrSQLTerms)  throws DBAppException{
		if(arrSQLTerms._strTableName.isEmpty() || arrSQLTerms._strTableName==null) {
			throw new DBAppException("No table name was typed");
		}

		for(Table table : tables) {
			if(table.getName().equalsIgnoreCase(arrSQLTerms._strTableName)) {
				Page page=table.getPh().loadFirstPage();
				ArrayList<Row> result= new ArrayList<>();
				while(page!=null) {
					ArrayList<Row> r = new ArrayList<>(page.getRows());
					Iterator<Row> it = r.iterator();
					String op = arrSQLTerms._strOperator;
					while(it.hasNext()) {
						Row row = (Row)it.next();
						if((arrSQLTerms._strColumnName==null && arrSQLTerms._strOperator==null && arrSQLTerms._objValue==null)
								|| (arrSQLTerms._strColumnName.isEmpty() && arrSQLTerms._strOperator.isEmpty() && arrSQLTerms._objValue.toString().isEmpty())) {

							break;
						}
						if(row.getEntry(arrSQLTerms._strColumnName)==null)
							throw new DBAppException("column name is typed incorrectly or missing");
						switch (op) {
							case "=":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {

									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())!=0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()!=(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((Double) row.getEntry(arrSQLTerms._strColumnName).getValue()!=(double)(arrSQLTerms._objValue))
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;
							case "<":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>=0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()>=(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((double)row.getEntry(arrSQLTerms._strColumnName).getValue()>=(double)arrSQLTerms._objValue)
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;
							case ">":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<=0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()<=(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((double)row.getEntry(arrSQLTerms._strColumnName).getValue()<=(double)arrSQLTerms._objValue)
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;
							case ">=":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()<(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((double)row.getEntry(arrSQLTerms._strColumnName).getValue()<(double)arrSQLTerms._objValue)
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;
							case "<=":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()>(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((double)row.getEntry(arrSQLTerms._strColumnName).getValue()>(double)arrSQLTerms._objValue)
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;
							case "!=":
								if(arrSQLTerms._objValue instanceof String && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
									if(row.getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())==0)
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Integer && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
									if((int)row.getEntry(arrSQLTerms._strColumnName).getValue()==(int)(arrSQLTerms._objValue))
										it.remove();
								}
								else if(arrSQLTerms._objValue instanceof Double && row.getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
									if((double)row.getEntry(arrSQLTerms._strColumnName).getValue()==(double)arrSQLTerms._objValue)
										it.remove();
								}
								else {
									throw new DBAppException("wrong object value");
								}
								break;

							default:
								throw new DBAppException("Invalid operator or missing");


						}

					}
					result.addAll(r);
					page=table.getPh().loadNextPage(page);


				}
				return result;

			}


		}
		throw new DBAppException("Table not found or typed incorrectly");
	}

	public static void main( String[] args ){
	
	try{
			String strTableName = "Student";
			DBApp	dbApp = new DBApp( );
			dbApp.init();
			Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.double");
			dbApp.createTable( strTableName, "id", htblColNameType );
			dbApp.createIndex( strTableName, "gpa", "gpaIndex" );

			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", new Integer( 2343432 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 453455 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 5674567 ));
			htblColNameValue.put("name", new String("Dalia Noor" ) );
			htblColNameValue.put("gpa", new Double( 1.25 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 23498 ));
			htblColNameValue.put("name", new String("John Noor" ) );
			htblColNameValue.put("gpa", new Double( 1.5 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", new Integer( 78452 ));
			htblColNameValue.put("name", new String("Zaky Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.88 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );


			SQLTerm[] arrSQLTerms;
			arrSQLTerms = new SQLTerm[2];
			arrSQLTerms[0] = new SQLTerm();
			arrSQLTerms[1] = new SQLTerm();
			arrSQLTerms[0]._strTableName =  "Student";
			arrSQLTerms[0]._strColumnName=  "name";
			arrSQLTerms[0]._strOperator  =  "=";
			arrSQLTerms[0]._objValue     =  "Zaky Noor";

			arrSQLTerms[1]._strTableName =  "Student";
			arrSQLTerms[1]._strColumnName=  "gpa";
			arrSQLTerms[1]._strOperator  =  "=";
			arrSQLTerms[1]._objValue     =  new Double( 1.5 );
			String[]strarrOperators = new String[1];
			strarrOperators[0] = "OR";
			// select * from Student where name = "John Noor" or gpa = 1.5;
			Iterator resultSet = dbApp.selectFromTable(arrSQLTerms , strarrOperators);
		}
		catch(Exception exp){
			exp.printStackTrace( );
		}
	}

}