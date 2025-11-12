package lzss;

import java.io.*;
import java.util.*;

public class LZSS_Compressor {

    public void compress(File inputFile, File outputFile) throws IOException {
        List<byte[]> dictionary = new ArrayList<>(); // словарь как список байтовых массивов
        dictionary.add(new byte[0]); // нулевой элемент - пустая строка

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {

            ByteArrayOutputStream currentBuffer = new ByteArrayOutputStream();
            int nextByte;

            while ((nextByte = in.read()) != -1) {
                currentBuffer.write(nextByte);
                byte[] currentBytes = currentBuffer.toByteArray();

                // Ищем текущую последовательность в словаре
                int foundIndex = -1;
                for (int i = 0; i < dictionary.size(); i++) {
                    if (Arrays.equals(dictionary.get(i), currentBytes)) {
                        foundIndex = i;
                        break;
                    }
                }

                if (foundIndex == -1) {
                    // Не нашли в словаре - добавляем новую запись
                    byte[] prefixBytes = Arrays.copyOf(currentBytes, currentBytes.length - 1);
                    byte lastByte = currentBytes[currentBytes.length - 1];

                    // Ищем индекс префикса в словаре
                    int prefixIndex = -1;
                    for (int i = 0; i < dictionary.size(); i++) {
                        if (Arrays.equals(dictionary.get(i), prefixBytes)) {
                            prefixIndex = i;
                            break;
                        }
                    }

                    if (prefixIndex == -1) {
                        // Префикс должен быть в словаре, это ошибка логики
                        throw new IOException("Prefix not found in dictionary");
                    }

                    // Записываем флаг 1 (ссылка) + индекс + байт
                    out.writeByte(1);
                    out.writeInt(prefixIndex);
                    out.writeByte(lastByte);

                    // Добавляем новую последовательность в словарь
                    dictionary.add(currentBytes);
                    currentBuffer.reset();
                }
                // Если нашли, продолжаем накапливать
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

