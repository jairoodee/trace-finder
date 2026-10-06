package reader;

// IOException is used when reading the file fails.
import java.io.IOException;

// Files provides methods for reading files.
import java.nio.file.Files;

// Path represents the location of the file.
import java.nio.file.Path;

// List stores all the lines read from the file.
import java.util.List;


public class LogFileReader {
    // This method receives the path of the log file.
    // It declares IOException so that the calling code
    // can decide how to handle a file-reading failure.
    public void readFile(String FilePath) throws IOException{
        // Convert the file path String into a Path object.
        Path path = Path.of(FilePath);

        // Read every line in the file into a List of Strings.
        // Each element in the List represents one line.
        List<String> lines = Files.readAllLines(path);
        
        // Start at index 0 because Java lists use zero-based indexing.
        for(int i = 0; i < lines.size(); i++){
            // The actual line number starts at 1,
            // even though the List index starts at 0.
            int lineNumber = i + 1;

            // Retrieve the content of the current line.
            String line = lines.get(i);

            // Print the line number followed by its original content.
            System.out.println(lineNumber + ": " + line);
        }
    }

    public static void main(String[] args) throws IOException{
            LogFileReader thisFile = new LogFileReader();

            thisFile.readFile("test.txt");
           
    }
}
