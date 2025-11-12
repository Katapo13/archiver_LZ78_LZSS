package lz78;

import java.io.*;
import java.util.*;

public class LZ78_Compressor {
    private static class Phrase {
        short index;
        byte symbol;

        Phrase(short index, byte symbol) {
            this.index = index;
            this.symbol = symbol;

        }
    }

    //функция сжатия
    public void compress(File inputFile, File outputFile) throws IOException {
        Map<String, Short> dictionaryPosPhrase = new HashMap<>(); //для позиции и фразы
        List<Phrase> phrases = new ArrayList<>(); //зашифрованная строка
        short dictIndex = 1; //позиция для фразы

        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile))) {
            StringBuilder current = new StringBuilder(); //считанная строка
            int nextByte;

            while ((nextByte = in.read()) != -1) {
                current.append((char) nextByte);
                String currentStr = current.toString(); //строка с новым символам

                if (!dictionaryPosPhrase.containsKey(currentStr)) {
                    short prefixIndex = (current.length() == 1)? 0 : dictionaryPosPhrase.get(currentStr.substring(0, current.length() - 1)); //вычисляем индекс последнего символа для кода
                    phrases.add(new Phrase(prefixIndex, (byte) nextByte));
                    dictionaryPosPhrase.put(currentStr, dictIndex++);
                    current.setLength(0);
                }
            }
            if (current.length() > 0) {
                String currentStr = current.toString();
                short prefixIndex = (current.length() == 1)? 0 : dictionaryPosPhrase.get(currentStr.substring(0, current.length() - 1));
                byte lastByte = (byte) currentStr.charAt(currentStr.length() - 1);
                phrases.add(new Phrase(prefixIndex, lastByte));
            }

        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        // Запись в выходной файл
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {
            for (Phrase phrase : phrases) {
                out.writeInt(phrase.index);
                out.writeByte(phrase.symbol);
            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
