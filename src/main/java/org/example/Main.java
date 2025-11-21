package org.example;
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//

//import lz78.LZ78_Compressor;
//import lz78.LZ78_Decompressor;
//import lzss.LZSS_Compressor;
//import lzss.LZSS_Decompressor;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class Main {
//    public static void main(String[] args) {
//        File input = new File("src/main/resources/Ducky.bmp");
//        File compressed = new File("src/main/resources/output.lz78");
//        File compressedSS = new File("src/main/resources/output.lzSS");
//        File decompressed = new File("src/main/resources/decompressedDucky.bmp");
//        File decompressedSS = new File("src/main/resources/decompressedSSDucky.bmp");
//
//        LZ78_Compressor compressor = new LZ78_Compressor();
//        LZ78_Decompressor decompressor = new LZ78_Decompressor();
//        LZSS_Compressor compressorSS = new LZSS_Compressor();
//        LZSS_Decompressor decompressorSS = new LZSS_Decompressor();
//
//        try {
//            compressor.compress(input, compressed);
//            decompressor.decompress(compressed, decompressed);
//
//            compressorSS.compress(input, compressedSS);
//            decompressorSS.decompress(compressedSS, decompressedSS);
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//    }
//}


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/org/example/archiver.fxml")));
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Архиватор файлов");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


