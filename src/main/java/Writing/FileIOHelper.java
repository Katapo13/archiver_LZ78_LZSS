package Writing;
import java.io.*;
import java.util.List;

public class FileIOHelper {

    public static void writeToFile(List<byte[]> data, File outputFile) throws IOException {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            for (byte[] block : data) {
                out.write(block);
            }
        }
    }

}
