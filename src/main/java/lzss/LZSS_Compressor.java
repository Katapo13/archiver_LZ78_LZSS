package lzss;

import java.io.*;
import java.util.*;

//public class LZSS_Compressor {
//
//    public void compress(File inputFile, File outputFile) throws IOException {
//        List<byte[]> dictionary = new ArrayList<>(); // словарь как список байтовых массивов
//        dictionary.add(new byte[0]); // нулевой элемент - пустая строка
//
//        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
//             DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {
//
//            ByteArrayOutputStream currentBuffer = new ByteArrayOutputStream();
//            int nextByte;
//
//            while ((nextByte = in.read()) != -1) {
//                currentBuffer.write(nextByte);
//                byte[] currentBytes = currentBuffer.toByteArray();
//
//                // Ищем текущую последовательность в словаре
//                int foundIndex = -1;
//                for (int i = 0; i < dictionary.size(); i++) {
//                    if (Arrays.equals(dictionary.get(i), currentBytes)) {
//                        foundIndex = i;
//                        break;
//                    }
//                }
//
//                if (foundIndex == -1) {
//                    // Не нашли в словаре - добавляем новую запись
//                    byte[] prefixBytes = Arrays.copyOf(currentBytes, currentBytes.length - 1);
//                    byte lastByte = currentBytes[currentBytes.length - 1];
//
//                    // Ищем индекс префикса в словаре
//                    int prefixIndex = -1;
//                    for (int i = 0; i < dictionary.size(); i++) {
//                        if (Arrays.equals(dictionary.get(i), prefixBytes)) {
//                            prefixIndex = i;
//                            break;
//                        }
//                    }
//
//                    if (prefixIndex == -1) {
//                        // Префикс должен быть в словаре
//                        throw new IOException("Prefix not found in dictionary");
//                    }
//
//                    // Записываем флаг 1 (ссылка) + индекс + байт
//                    out.writeByte(1);
//                    out.writeInt(prefixIndex);
//                    out.writeByte(lastByte);
//
//                    // Добавляем новую последовательность в словарь
//                    dictionary.add(currentBytes);
//                    currentBuffer.reset();
//                }else {
////                    // Если нашли, продолжаем накапливать
////                    if (currentBuffer.size() > 1024) {
////                        // сбросить как литералы
////                        for (byte b : currentBuffer.toByteArray()) {
////                            out.writeByte(0);
////                            out.writeByte(b);
////                        }
////                        currentBuffer.reset();
////                    }
//
//                }
//            }
//
//            // Обработка оставшихся данных в буфере
//            if (currentBuffer.size() > 0) {
//                byte[] remainingBytes = currentBuffer.toByteArray();
//                // Для одиночного байта используем флаг 0
//                if (remainingBytes.length == 1) {
//                    out.writeByte(0);
//                    out.writeByte(remainingBytes[0]);
//                } else {
//                    // Ищем префикс в словаре
//                    byte[] prefixBytes = Arrays.copyOf(remainingBytes, remainingBytes.length - 1);
//                    byte lastByte = remainingBytes[remainingBytes.length - 1];
//
//                    int prefixIndex = -1;
//                    for (int i = 0; i < dictionary.size(); i++) {
//                        if (Arrays.equals(dictionary.get(i), prefixBytes)) {
//                            prefixIndex = i;
//                            break;
//                        }
//                    }
//
//                    if (prefixIndex != -1) {
//                        out.writeByte(1);
//                        out.writeInt(prefixIndex);
//                        out.writeByte(lastByte);
//                    } else {
//                        // Если префикс не найден, записываем как одиночные байты
//                        for (byte b : remainingBytes) {
//                            out.writeByte(0);
//                            out.writeByte(b);
//                        }
//                    }
//                }
//            }
//
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//            throw ex;
//        }
//    }
//}

public class LZSS_Compressor {

    public final class ByteArrayWrapper {
        private final byte[] data;
        private final int hash;

        public ByteArrayWrapper(byte[] data) {
            this.data = data;
            this.hash = Arrays.hashCode(data); // сразу считаем hash
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ByteArrayWrapper)) return false;
            ByteArrayWrapper other = (ByteArrayWrapper) obj;
            return Arrays.equals(this.data, other.data);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }


    public void compress(File inputFile, File outputFile) throws IOException {
        Map<ByteArrayWrapper, Integer> dictIndex = new HashMap<>(); //обёртка для списка для сохранения
        List<byte[]> dictionary = new ArrayList<>(); //список байтовых массивов
        dictionary.add(new byte[0]); // нулевой элемент - пустая строка
        dictIndex.put(new ByteArrayWrapper(new byte[0]), 0);

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {

            ByteArrayOutputStream currentBuffer = new ByteArrayOutputStream();
            int nextByte;

            while ((nextByte = in.read()) != -1) {
                currentBuffer.write(nextByte);
                byte[] currentBytes = currentBuffer.toByteArray();

                // Ищем текущую последовательность в словаре
                Integer foundIndex = dictIndex.get(new ByteArrayWrapper(currentBytes));

                if (foundIndex == null) {
                    // Не нашли в словаре - добавляем новую запись
                    byte[] prefixBytes = Arrays.copyOf(currentBytes, currentBytes.length - 1);
                    byte lastByte = currentBytes[currentBytes.length - 1];

                    // Ищем индекс префикса в словаре
                    Integer prefixIndex = dictIndex.get(new ByteArrayWrapper(prefixBytes));
                    if (prefixIndex == null) {
                        out.writeByte(0);
                        out.writeByte(lastByte);
                    } else {
                        // Записываем флаг 1 (ссылка) + индекс + байт
                        out.writeByte(1);
                        out.writeInt(prefixIndex);
                        out.writeByte(lastByte);
                    }

                    // Добавляем новую последовательность в словарь
                    dictionary.add(currentBytes);
                    dictIndex.put(new ByteArrayWrapper(currentBytes), dictionary.size() - 1);
                    currentBuffer.reset();
                }
            }

            // Обработка оставшихся данных в буфере
            if (currentBuffer.size() > 0) {
                byte[] remainingBytes = currentBuffer.toByteArray();
                // Для одиночного байта используем флаг 0
                if (remainingBytes.length == 1) {
                    out.writeByte(0);
                    out.writeByte(remainingBytes[0]);
                } else {
                    // Ищем префикс в словаре
                    byte[] prefixBytes = Arrays.copyOf(remainingBytes, remainingBytes.length - 1);
                    byte lastByte = remainingBytes[remainingBytes.length - 1];

                    int prefixIndex = -1;
                    for (int i = 0; i < dictionary.size(); i++) {
                        if (Arrays.equals(dictionary.get(i), prefixBytes)) {
                            prefixIndex = i;
                            break;
                        }
                    }

                    if (prefixIndex != -1) {
                        out.writeByte(1);
                        out.writeInt(prefixIndex);
                        out.writeByte(lastByte);
                    } else {
                        // Если префикс не найден, записываем как одиночные байты
                        for (byte b : remainingBytes) {
                            out.writeByte(0);
                            out.writeByte(b);
                        }
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
}



