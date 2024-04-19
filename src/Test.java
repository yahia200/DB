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
//      public ArrayList<Row> selectFromTableHelper(SQLTerm arrSQLTerms)  throws DBAppException{
    	
//     			for(Table table : tables) {
//     				if(table.getName()==arrSQLTerms._strTableName) {
//     					ArrayList<Row> r = new ArrayList<>(table.getRows());
//     					Iterator<Row> it = r.iterator();
//     					String op = arrSQLTerms._strOperator;
//     					while(it.hasNext()) {
//     						//it.next().getEntry(arrSQLTerms[0]._strColumnName)
//     						 switch (op) {
//     				            case "=":
//     				                if(arrSQLTerms._objValue instanceof String) {
//     				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())!=0)
//     				                		it.remove();
//     				                }
//     				                else {
//     				                	if(it.next().getEntry(arrSQLTerms._strColumnName).getValue()!=(double)(arrSQLTerms._objValue))
//     				                		it.remove();
//     				                }
//     				                break;
//     				            case "<":
//     				            	  if(arrSQLTerms._objValue instanceof String) {
//       				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>=0)
//       				                		it.remove();
//       				                }
//       				                else {
//       				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()>=(double)arrSQLTerms._objValue)
//       				                		it.remove();
//       				                } 
//     				            	break;
//     				            case ">":
//     				            	  if(arrSQLTerms._objValue instanceof String) {
//       				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<=0)
//       				                		it.remove();
//       				                }
//       				                else {
//       				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()<=(double)arrSQLTerms._objValue)
//       				                		it.remove();
//       				                }
//     				                break;
//     				            case ">=":
//     				            	if(arrSQLTerms._objValue instanceof String) {
//       				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())<0)
//       				                		it.remove();
//       				                }
//       				                else {
//       				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()<(double)arrSQLTerms._objValue)
//       				                		it.remove();
//       				                }
//     				                break;
//     				            case "<=":
//     				            	if(arrSQLTerms._objValue instanceof String) {
//       				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())>0)
//       				                		it.remove();
//       				                }
//       				                else {
//       				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()>(double)arrSQLTerms._objValue)
//       				                		it.remove();
//       				                }
//     				                break;
//     				            case "!=":
//     				            	if(arrSQLTerms._objValue instanceof String) {
//       				                	if(it.next().getEntry(arrSQLTerms._strColumnName).toString().compareToIgnoreCase(arrSQLTerms._objValue.toString())==0)
//       				                		it.remove();
//       				                }
//       				                else {
//       				                	if((double)it.next().getEntry(arrSQLTerms._strColumnName).getValue()==(double)arrSQLTerms._objValue)
//       				                		it.remove();
//       				                }
//     				                break;
    				        
//     				            default:
//     				                System.out.println("Invalid operator");
    				            
    				           
//     				        }
//     					}
//     					return  r;
//     				}
    				
    			
//     			}
// 				return null;
//     }
//     public Iterator<Row> selectFromTable(SQLTerm[] arrSQLTerms,
//     		 						String[] strarrOperators)
//     		 							throws DBAppException{
//     	ArrayList<Row> res = new ArrayList<>();
//     	ArrayList<Row> r1 = new ArrayList<>();
//     	ArrayList<Row> r2 = new ArrayList<>();

//     	if(arrSQLTerms.length==1) 
//     		return selectFromTableHelper(arrSQLTerms[0]).iterator();
//     	else {
//     		int i=0;
//     		int j=0;
//     		while(i<arrSQLTerms.length-1) {
    			
//     			if(i==0)
//         		    r1= selectFromTableHelper(arrSQLTerms[i]);
//         		else
//         			r1=res;
//     				r2= selectFromTableHelper(arrSQLTerms[i+1]);
//     		    	  switch (strarrOperators[j]) {
//     		            case "AND":
//     		                r1.retainAll(r2);
//     		                res=r1;
//     		                break;
//     		            case "OR":
//     		            	res.addAll(r1);
//     		            	res.addAll(r2);
//     		            	break;
//     		            case "XOR":
//     		            	res.addAll(r1);
//     		            	res.addAll(r2);   
//     		            	r1.retainAll(r2);
//     		            	res.removeAll(r1);
//     		            	break;
    		            
//     		            default:
//     		                System.out.println("Invalid operator");
    		                
//     		        }	

	
    				
    				
    				
//     			i++;
//     			j++;
//     		}
//     		return res.iterator();
//     	}
//     }

}
