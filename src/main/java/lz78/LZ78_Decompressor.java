package lz78;

import java.io.*;
import java.util.*;

public class LZ78_Decompressor {

    public void decompress(File inputFile, File outputFile) throws IOException {
        List<byte[]> dictionary = new ArrayList<>();
        dictionary.add(new byte[0]); // индекс 0 — пустая строка

        try (DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            while (in.available() > 0) {
                int index = in.readInt();
                byte symbol = in.readByte();

                byte[] prefix = dictionary.get(index);
                byte[] phrase = Arrays.copyOf(prefix, prefix.length + 1);
                phrase[phrase.length - 1] = symbol;

                dictionary.add(phrase);
                out.write(phrase);
            }
        }
    }
}
