package lz78;
import basic.Compressor;
import java.io.*;
import java.util.*;

//public class LZ78_Compressor extends Compressor{
//
//    private static class Phrase {
//        int index;
//        byte symbol;
//
//        Phrase(int index, byte symbol) {
//            this.index = index;
//            this.symbol = symbol;
//        }
//    }
//
//    public void compress(File inputFile, File outputFile) throws IOException {
//        Map<List<Byte>, Integer> dictionary = new HashMap<>();
//        List<Phrase> phrases = new ArrayList<>();
//        int dictIndex = 1;
//
//        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
//            ByteArrayOutputStream current = new ByteArrayOutputStream();
//            int nextByte;
//
//            while ((nextByte = in.read()) != -1) {
//                current.write(nextByte);
//                byte[] currentBytes = current.toByteArray();
//                List<Byte> currentList = toByteList(currentBytes);
//
//                if (!dictionary.containsKey(currentList)) {
//                    List<Byte> prefixList = toByteList(Arrays.copyOf(currentBytes, currentBytes.length - 1));
//                    int prefixIndex = (prefixList.isEmpty()) ? 0 : dictionary.getOrDefault(prefixList, 0);
//
//                    phrases.add(new Phrase(prefixIndex, (byte) nextByte));
//                    dictionary.put(currentList, dictIndex++);
//                    current.reset();
//                }
//            }
//
//            // Обработка остатка
//            if (current.size() > 0) {
//                byte[] remaining = current.toByteArray();
//                List<Byte> prefixList = toByteList(Arrays.copyOf(remaining, remaining.length - 1));
//                int prefixIndex = (prefixList.isEmpty()) ? 0 : dictionary.getOrDefault(prefixList, 0);
//                byte lastByte = remaining[remaining.length - 1];
//                phrases.add(new Phrase(prefixIndex, lastByte));
//            }
//        }
//
//        // Запись в файл
//        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {
//            for (Phrase phrase : phrases) {
//                out.writeInt(phrase.index);
//                out.writeByte(phrase.symbol);
//            }
//        }
//    }
//
//    private List<Byte> toByteList(byte[] array) {
//        List<Byte> list = new ArrayList<>(array.length);
//        for (byte b : array) list.add(b);
//        return list;
//    }
//}

public class LZ78_Compressor extends Compressor{

    private static class Phrase {
        int index;
        byte symbol;

        Phrase(int index, byte symbol) {
            this.index = index;
            this.symbol = symbol;
        }

        byte[] toBytes() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (DataOutputStream dataOut = new DataOutputStream(out)) {
                dataOut.writeInt(index);
                dataOut.writeByte(symbol);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return out.toByteArray();
        }
    }

    @Override
    public List<byte[]> compress(File inputFile) throws IOException {
        Map<List<Byte>, Integer> dictionary = new HashMap<>();
        List<byte[]> result = new ArrayList<>();
        int dictIndex = 1;

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            ByteArrayOutputStream current = new ByteArrayOutputStream();
            int nextByte;

            while ((nextByte = in.read()) != -1) {
                current.write(nextByte);
                byte[] currentBytes = current.toByteArray();
                List<Byte> currentList = toByteList(currentBytes);

                if (!dictionary.containsKey(currentList)) {
                    List<Byte> prefixList = toByteList(Arrays.copyOf(currentBytes, currentBytes.length - 1));
                    int prefixIndex = (prefixList.isEmpty()) ? 0 : dictionary.getOrDefault(prefixList, 0);

                    Phrase phrase = new Phrase(prefixIndex, (byte) nextByte);
                    result.add(phrase.toBytes());

                    dictionary.put(currentList, dictIndex++);
                    current.reset();
                }
            }

            // Обработка остатка
            if (current.size() > 0) {
                byte[] remaining = current.toByteArray();
                List<Byte> prefixList = toByteList(Arrays.copyOf(remaining, remaining.length - 1));
                int prefixIndex = (prefixList.isEmpty()) ? 0 : dictionary.getOrDefault(prefixList, 0);
                byte lastByte = remaining[remaining.length - 1];
                Phrase phrase = new Phrase(prefixIndex, lastByte);
                result.add(phrase.toBytes());
            }
        }
        return result;
    }

    private List<Byte> toByteList(byte[] array) {
        List<Byte> list = new ArrayList<>(array.length);
        for (byte b : array) list.add(b);
        return list;
    }
}



