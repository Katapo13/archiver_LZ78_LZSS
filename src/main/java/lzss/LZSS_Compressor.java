package lzss;

import lzss.LZSS_Compressor;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class LZSS_Compressor {

    //функция сжатия
    public void compress(File inputFile, File outputFile) throws IOException {

        List<String> codePhrases = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new FileReader(inputFile))) {

            String buffer;


            while ((buffer = in.readLine())!=null) {
                if (buffer.equals("")){
                    continue;
                }
                String dictionary = ""; // словарь

                //цикл по буферу
                for (int i = 0; i < buffer.length(); i++){
                    String currChar = "" + buffer.charAt(i);
                    String currCode;

                    if (dictionary.isEmpty()){
                        currCode = "0" + currChar;
                    }else {
                        if(dictionary.contains(currChar)){
                            while(i + 1 < buffer.length() && dictionary.contains(currChar + buffer.charAt(i+1))){
                                currChar += buffer.charAt(i+1);
                                ++i;
                            }
                            int position = dictionary.length() - dictionary.indexOf(currChar) - 1;
                            currCode = "1" + position + "_" + currChar.length();
                        }else {
                            currCode = "0" + currChar;
                        }

                    }
                    dictionary += currChar;
                    codePhrases.add(currCode);
                }

            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }

        // Запись в выходной файл
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile))) {
            for (String phrase : codePhrases) {
                if (phrase.startsWith("0")) {
                    out.writeByte(0);
                    out.writeByte((byte) phrase.charAt(1)); // символ
                } else {
                    int indexDevChar = phrase.indexOf('_');
                    int position = Integer.parseInt(phrase.substring(1, indexDevChar));
                    int length = Integer.parseInt(phrase.substring(indexDevChar + 1));
                    out.writeByte(1);
                    out.writeInt(position);
                    out.writeInt(length);
                }
            }
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }


    }

}
