import java.io.IOException;
import java.util.*;

public class Test {
	CSVHandler csv = new CSVHandler();
	public Vector<Table> tables = new Vector<Table>();
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
            System.out.println(e.getMessage());
        }
        return null;
    }

	public void init( ) throws Exception{

		try {
			tables = csv.ReadCSV();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    public void deleteFromTable(String strTableName, Hashtable<String,Object> ht) throws Exception{
        for (Table table : tables) {
            if (table.getName().equals(strTableName)) {
                table.deleteFromTable(ht);
                break;
            }
        }
    }


    public void saveCSV() throws IOException{
        ArrayList<String[]> meta = new ArrayList<>() ;
        for (Table table : tables){
            meta.addAll(table.getMeta());
        }
        csv.saveCSV(meta);
    }

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
            System.out.println("failed insert");
        }
    }


    public void createIndex(String   strTableName,
                            String   strColName,
                            String   strIndexName) throws Exception{
        for (Table table : tables) {
            if (table.getName().equalsIgnoreCase(strTableName))
                table.createIndex(strColName, strIndexName);
                
        }
        saveCSV();
    }

    public void printInd(){
        for (Table table : tables) {
                table.printInd();
        }
    }
    // selects only one sql term
    public ArrayList<Row> selectFromTableHelper(SQLTerm arrSQLTerms)  throws DBAppException{
    	if(arrSQLTerms._strTableName.isEmpty() || arrSQLTerms._strTableName==null) {
    		throw new DBAppException("No table name was typed");
    	}
    	
    			for(Table table : tables) {
    				if(table.getName()==arrSQLTerms._strTableName) {
    					Page page=table.getPh().loadFirstPage();
    					ArrayList<Row> result= new ArrayList<>();
    					while(page!=null) {
    					ArrayList<Row> r = new ArrayList<>(page.getRows());
    					Iterator<Row> it = r.iterator();
    					String op = arrSQLTerms._strOperator;
    					while(it.hasNext()) {
    						//select all if no cloumnName and operator and value typed
    				    	if((arrSQLTerms._strColumnName==null && arrSQLTerms._strOperator==null && arrSQLTerms._objValue==null) 
    				    			|| (arrSQLTerms._strColumnName.isEmpty() && arrSQLTerms._strOperator.isEmpty() && arrSQLTerms._objValue==null)) {
    				    		
    				    	}
    						if(it.next().getEntry(arrSQLTerms._strColumnName)==null)
		                		throw new DBAppException("column name is typed incorrectly or missing");
    						
    		
    						switch (op) {
    				            case "=":
    				                if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
    				         
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())!=0)
    				                		it.remove();
    				                }
    				                else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()!=(int)(arrSQLTerms._objValue))
    				                		it.remove();
    				                }
    				                else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()!=(double)(arrSQLTerms._objValue))
    				                		it.remove();
    				                }
    				                else {
										throw new DBAppException("wrong object value");
									}
    				                break;
    				            case "<":
    				            	  if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>=0)
      				                		it.remove();
      				                }
    				            	  else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()>=(int)(arrSQLTerms._objValue))
      				                		it.remove();
      				                }
    				            	  else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
      				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()>=(double)arrSQLTerms._objValue)
      				                		it.remove();
      				                } 
    				            	  else {
    				            		  throw new DBAppException("wrong object value");
									}
    				            	break;
    				            case ">":
    				            	  if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<=0)
      				                		it.remove();
      				                }
    				            	  else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
        				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()<=(int)(arrSQLTerms._objValue))
        				                		it.remove();
        				                }
    				            	  else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
      				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()<=(double)arrSQLTerms._objValue)
      				                		it.remove();
      				                }
    				            	  else {
    				            		  throw new DBAppException("wrong object value");
									}
    				                break;
    				            case ">=":
    				            	if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<0)
      				                		it.remove();
      				                }
    				            	else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()<(int)(arrSQLTerms._objValue))
    				                		it.remove();
    				                }
    				            	else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
      				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()<(double)arrSQLTerms._objValue)
      				                		it.remove();
      				                }
    				            	else {
  				            		  throw new DBAppException("wrong object value");
									}
    				                break;
    				            case "<=":
    				            	if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>0)
      				                		it.remove();
      				                }
    				            	else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()>(int)(arrSQLTerms._objValue))
    				                		it.remove();
    				                }
    				            	else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
      				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()>(double)arrSQLTerms._objValue)
      				                		it.remove();
      				                }
    				            	else {
    				            		  throw new DBAppException("wrong object value");
  									}
    				                break;
    				            case "!=":
    				            	if(arrSQLTerms._objValue instanceof String && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.String")) {
      				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())==0)
      				                		it.remove();
      				                }
    				            	else if(arrSQLTerms._objValue instanceof Integer && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Integer")) {
    				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()==(int)(arrSQLTerms._objValue))
    				                		it.remove();
    				                }
    				            	else if(arrSQLTerms._objValue instanceof Double && it.next().getEntry(arrSQLTerms._strColumnName).getType().equals("java.lang.Double")){
      				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()==(double)arrSQLTerms._objValue)
      				                		it.remove();
      				                }
    				            	else {
  				            		  throw new DBAppException("wrong object value");
									}
    				                break;
    				        
    				            default:
    				                throw new DBAppException("Invalid operator or missing");
    				            
    				           
    				        }
    						page=table.getPh().loadNextPage(page);
    								
    					}
    					result.addAll(r);
    					
			
    			  
    			}
    					return result;
			
    }
    							
    			
    }
    			throw new DBAppException("Table not found or typed incorrectly");
    }
    public Iterator<Row> selectFromTable(SQLTerm[] arrSQLTerms,
    		 						String[] strarrOperators)
    		 							throws DBAppException{
    	String tableName= arrSQLTerms[0]._strTableName;
    	for(int i =1;i<arrSQLTerms.length-1;i++) {
    		if(!(tableName.equals(arrSQLTerms[i]._strTableName)))
    				throw new DBAppException("cant select from different pages");
    	}
    	if(arrSQLTerms.length!=(strarrOperators.length+1))
    		throw new DBAppException("wrong sql statement");
    	
    ArrayList<ArrayList<Row>>Operands= new ArrayList<>();
    for(SQLTerm arrSQLTerm : arrSQLTerms) {
    	Operands.add(selectFromTableHelper(arrSQLTerm));
    }
    
    ArrayList<String>operators=new ArrayList<>();
    for(int i=0; i<strarrOperators.length;i++) {
    	operators.add(strarrOperators[i]);
    }
    while(Operands.size()!=1) {
    	
    	if(operators.contains("AND")) {
    		for(String operator : operators) {
    			if(operator.equalsIgnoreCase("AND")) {
    				int index1=operators.indexOf(operator);
    				int index2=index1+1;
    				operators.remove(index1);
    			ArrayList<Row> result =AND(Operands.remove(index1),Operands.remove(index2));
    				Operands.add(index1, result);
    			}
    		}
    	}
    	if(operators.contains("XOR")) {
    		for(String operator : operators) {
    			if(operator.equalsIgnoreCase("XOR")) {
    				int index1=operators.indexOf(operator);
    				int index2=index1+1;
    				operators.remove(index1);
    			    ArrayList<Row> result =XOR(Operands.remove(index1),Operands.remove(index2));
    				Operands.add(index1, result);
    			}
    		}
    	}
    	if(operators.contains("OR")) {
    		for(String operator : operators) {
    			if(operator.equalsIgnoreCase("OR")) {
    				int index1=operators.indexOf(operator);
    				int index2=index1+1;
    				operators.remove(index1);
    			    ArrayList<Row> result =OR(Operands.remove(index1),Operands.remove(index2));
    				Operands.add(index1, result);
    			}
    		}
    	}
    }
      return Operands.get(0).iterator();
    
    
    }

    
    public ArrayList<Row> OR(ArrayList<Row>r1,ArrayList<Row>r2){
    	ArrayList<Row> r3 = new ArrayList<>();
    	r3.clear();
    	r3.addAll(r1);
    	r1.retainAll(r2);
    	r3.removeAll(r1);
    	r3.addAll(r2);
    	return r3;
}
    public ArrayList<Row> AND(ArrayList<Row>r1,ArrayList<Row>r2){
    	ArrayList<Row> r3 = new ArrayList<>();
    	  r1.retainAll(r2);
          r3=r1;
    	return r3;
}
    public ArrayList<Row> XOR(ArrayList<Row>r1,ArrayList<Row>r2){
    	ArrayList<Row> r3 = new ArrayList<>();
    	r3.clear();
    	r3.addAll(r1);
    	r3.addAll(r2);   
    	r1.retainAll(r2);
    	r3.removeAll(r1);
    	return r3;
}
    
    
    
}