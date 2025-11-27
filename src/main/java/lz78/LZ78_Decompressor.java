package lz78;
import basic.Decompressor;
import java.io.*;
import java.util.*;

public class LZ78_Decompressor extends Decompressor {

    @Override
    public List<byte[]> decompress(File inputFile) throws IOException {
        List<byte[]> dictionary = new ArrayList<>();
        dictionary.add(new byte[0]); // индекс 0 — пустая строка
        List<byte[]> result = new ArrayList<>();

        try (DataInputStream in = new DataInputStream(new FileInputStream(inputFile))) {

            while (in.available() > 0) {
                int index = in.readInt();
                byte symbol = in.readByte();

                byte[] prefix = dictionary.get(index);
                byte[] phrase = Arrays.copyOf(prefix, prefix.length + 1);
                phrase[phrase.length - 1] = symbol;

                dictionary.add(phrase);
                result.add(phrase);
            }
        }
        return result;
    }
}
