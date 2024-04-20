import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import BTree.*;

public class CSVHandler {

    public CSVHandler(){

    }


    private String convertToCSV(String[] data) {
        return Stream.of(data)
          .map(this::escapeSpecialCharacters)
          .collect(Collectors.joining(","));
    }


    public void saveCSV(ArrayList<String[]> lines) throws IOException {
        File csvOutputFile = new File("Meta-Data.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            lines.stream()
              .map(this::convertToCSV)
              .forEach(pw::println);
        }
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public Vector<Table> ReadCSV() throws Exception{
        ArrayList<ArrayList<Object>> tables = new ArrayList<>();
        Vector<Table> res = new Vector<>();
        Vector<BTree> indecies = new Vector<>();
        tables.add(new ArrayList<>());
        String csvFile = "Meta-Data.csv";
        String line;
        String csvDelimiter = ",";
        List<String[]> records = new ArrayList<>();
        ArrayList<String> tableNames = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine())!= null) {
                String[] values = line.split(csvDelimiter);
                records.add(values);
            }
        }

        String currTable = records.get(0)[0];

        for (String[ ] record : records){
            if(!tableNames.contains(record[0])){
                tableNames.add(record[0]);
                ArrayList<Object> table = new ArrayList<>();
                table.add(record[0]);
                table.add(new Vector<Entry>());
                tables.add(table);
            }
        }
        tables.remove(0);

        for (ArrayList<Object> table : tables){
            for (String[ ] record : records){
                if(table.contains(record[0])){
                    if(!record[5].equalsIgnoreCase("null")){
                        BTree index = loadIndex(record[4], record[0]);
                        indecies.add(index);
                    }
                    if (record[2].equalsIgnoreCase("java.lang.Integer")) {
                        ((Vector<Entry>)table.get(1)).add(new IntEntry((record[1]), 0));
                        if (record[3].equalsIgnoreCase("true"))
                            table.add(record[1]);
                    } else if (record[2].equalsIgnoreCase("java.lang.String")) {
                        ((Vector<Entry>)table.get(1)).add(new StrEntry((record[1]), "ds"));
                        if (record[3].equalsIgnoreCase("true"))
                            table.add(record[1]);
                    } else if (record[2].equalsIgnoreCase("java.lang.double")) {
                        ((Vector<Entry>)table.get(1)).add(new DoubleEntry((record[1]), 22.2));
                        if (record[3].equalsIgnoreCase("true"))
                            table.add(record[1]);
                    }      
                }
            }

        }

        for (ArrayList<Object> table : tables){
            Table newTable = new Table((String)table.get(0), (Vector<Entry>)table.get(1), (String)table.get(2));
            for(BTree index : indecies){
                if (index.tableName.equalsIgnoreCase(newTable.getName())){
                    newTable.addIndex(index);
                    System.out.println("foundIndex");
                }
            }
            res.add(newTable);

            
        }

        return res;

    }

    public BTree loadIndex(String indexName, String tableName) throws Exception{
        FileInputStream fileIn = new FileInputStream(indexName + "_" + tableName + ".class");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        BTree index = (BTree) in.readObject();
        in.close();
        fileIn.close();
        return index;
    }
}


      
    

