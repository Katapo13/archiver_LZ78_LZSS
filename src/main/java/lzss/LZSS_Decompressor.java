package lzss;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LZSS_Decompressor {

    public void decompress(File inputFile, File outputFile) throws IOException {
        List<byte[]> decoded = new ArrayList<>(); //расшифрованная строка

        try (BufferedReader in = new BufferedReader(new FileReader(inputFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            String buffer;
            while ((buffer = in.readLine())!=null) {
                byte[] phrase = {0};

                if(buffer.contains("_")){

                   int indexDevChar =  buffer.indexOf('_');
                   int position = Integer.parseInt(buffer.substring(1,indexDevChar));
                   int length = Integer.parseInt(buffer.substring(indexDevChar+1));
                    ByteArrayOutputStream addStr = new ByteArrayOutputStream();

                    for (int i = 0; i < length; i++) {
                        int positionInArr = decoded.size() - position - 1 + i;
                        if (positionInArr >= 0 && positionInArr < decoded.size()) {
                            addStr.write(decoded.get(positionInArr)[0]);
                        }
                    }

                    phrase = addStr.toByteArray();
                }else{
                    int index = in.readInt(); //индекс в коде
                    byte symbol = in.readByte();//символ в коде
                    phrase = new byte[] { symbol };
                }

                // добавляем каждый байт отдельно
                for (byte b : phrase) {
                    decoded.add(new byte[]{b});
                }

            }

            for (byte[] arr : decoded) {
                out.write(arr);
            }

        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
