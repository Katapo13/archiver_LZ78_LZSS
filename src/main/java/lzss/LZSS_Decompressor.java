package lzss;
import basic.Decompressor;
import java.io.*;
import java.util.*;

public class LZSS_Decompressor extends Decompressor{

    @Override
    public List<byte[]> decompress(File inputFile) throws IOException {
        List<byte[]> dictionary = new ArrayList<>(); // словарь как список байтовых массивов
        List<byte[]> result = new ArrayList<>();

        dictionary.add(new byte[0]); // нулевой элемент - пустая строка

        try (DataInputStream in = new DataInputStream(new FileInputStream(inputFile))) {

            while (in.available() > 0) {
                byte flag = in.readByte();
                byte[] phrase;

                if (flag == 0) {
                    // Одиночный байт
                    byte symbol = in.readByte();
                    phrase = new byte[]{symbol};
                } else if (flag == 1) {
                    // Ссылка на словарь + байт
                    int index = in.readInt();
                    byte symbol = in.readByte();

                    if (index < 0 || index >= dictionary.size()) {
                        throw new IOException("Invalid dictionary index: " + index);
                    }

                    // Получаем байты из словаря по индексу и добавляем новый символ
                    byte[] prefix = dictionary.get(index);
                    phrase = new byte[prefix.length + 1];
                    System.arraycopy(prefix, 0, phrase, 0, prefix.length);
                    phrase[phrase.length - 1] = symbol;
                } else {
                    throw new IOException("Неверный флаг: " + flag);
                }

                // Добавляем в словарь
                dictionary.add(phrase);
                // Записываем в выходной поток
                result.add(phrase);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return result;
    }
}
