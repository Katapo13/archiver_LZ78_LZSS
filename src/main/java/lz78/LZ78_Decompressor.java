package lz78;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class LZ78_Decompressor {

    public void decompress(File inputFile, File outputFile) throws IOException {
        List<byte[]> decoded = new ArrayList<>(); //расшифрованная строка

        try (DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            while (in.available() > 0) {
                int index = in.readInt(); //индекс в коде
                byte symbol = in.readByte();//символ в коде

                byte[] phrase;
                if (index == 0) {
                    phrase = new byte[] { symbol };
                } else {
                    byte[] prefix = decoded.get(index - 1);
                    phrase = new byte[prefix.length + 1];
                    System.arraycopy(prefix, 0, phrase, 0, prefix.length);
                    phrase[prefix.length] = symbol;
                }

                decoded.add(phrase);
            }
            for (byte[] arr : decoded) {
                out.write(arr);
            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

}
