import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
}
