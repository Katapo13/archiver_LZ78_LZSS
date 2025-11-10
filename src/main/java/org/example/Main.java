package org.example;

import lz78.LZ78_Compressor;
import lz78.LZ78_Decompressor;
import lzss.LZSS_Compressor;
import lzss.LZSS_Decompressor;
import java.io.File;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        File input = new File("src/main/resources/input.txt");
        File compressed = new File("src/main/resources/output.lz78");
        File compressedSS = new File("src/main/resources/output.lzSS");
        File decompressed = new File("src/main/resources/decompressed.txt");
        File decompressedSS = new File("src/main/resources/decompressedSS.txt");

        LZ78_Compressor compressor = new LZ78_Compressor();
        LZ78_Decompressor decompressor = new LZ78_Decompressor();
        LZSS_Compressor compressorSS = new LZSS_Compressor();
        LZSS_Decompressor decompressorSS = new LZSS_Decompressor();

        try {
            compressor.compress(input, compressed);
            decompressor.decompress(compressed, decompressed);

            compressorSS.compress(input, compressedSS);
            decompressorSS.decompress(compressedSS, decompressedSS);
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}