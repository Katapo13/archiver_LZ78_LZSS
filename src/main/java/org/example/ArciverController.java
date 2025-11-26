package org.example;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lz78.LZ78_Compressor;
import lz78.LZ78_Decompressor;
import lzss.LZSS_Compressor;
import lzss.LZSS_Decompressor;

import java.io.File;
import java.io.IOException;

public class ArciverController {

    public Button resultFileClose;
    @FXML
    private StackPane fileDropArea;
    @FXML
    private Label selectedFileLabel;
    @FXML
    private Label resultFileLabel;
    @FXML
    private RadioButton lz78RadioButton;
    @FXML
    private RadioButton lzssRadioButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button saveButton;
    @FXML
    private HBox selectedFileBox;
    @FXML
    private HBox resultFileBox;


    private File selectedFile;
    private File resultFile;

    @FXML
    private void initialize() {
        ToggleGroup compressionGroup = new ToggleGroup();
        lz78RadioButton.setToggleGroup(compressionGroup);
        lzssRadioButton.setToggleGroup(compressionGroup);
        lz78RadioButton.setSelected(true);

        fileDropArea.setOnMouseClicked(event -> {
            chooseFile();
        });

    }

    private void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл для архивации");
        selectedFile = fileChooser.showOpenDialog(fileDropArea.getScene().getWindow());
        if (selectedFile != null) {
            selectedFileLabel.setText("Выбран файл: " + selectedFile.getName());
            saveButton.setVisible(false);
            resultFile = null;
        }
        selectedFileBox.setVisible(true);
    }

    @FXML
    private void handleDragOver(javafx.scene.input.DragEvent event) {
        if (event.getGestureSource() != fileDropArea && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    private void handleFileDrop(javafx.scene.input.DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            selectedFile = db.getFiles().get(0);
            selectedFileLabel.setText("Выбран файл: " + selectedFile.getName());
            saveButton.setVisible(false);
            resultFile = null;
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
        selectedFileBox.setVisible(true);
    }

    @FXML
    private void compressFile() {
        if (selectedFile == null) {
            showAlert("Файл не выбран", "Пожалуйста, выберите файл для архивации.");
            return;
        }
        String algo = lz78RadioButton.isSelected() ? "LZ78" : "LZSS";
        File outputFile = new File(selectedFile.getParent(), selectedFile.getName() + "." + algo.toLowerCase());
        resultFile = outputFile;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(0, 10);

                // имитация работы — 10 шагов по 300 мс
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(300); // пауза
                    updateProgress(i, 10); // обновляем прогресс
                }

                // выполняем алгоритм
                if (algo.equals("LZ78")) {
                    new LZ78_Compressor().compress(selectedFile, outputFile);
                } else {
                    new LZSS_Compressor().compress(selectedFile, outputFile);
                }

                return null;
            }
        };

        runTask(task);
    }

    @FXML
    private void decompressFile() {
        if (selectedFile == null) {
            showAlert("Файл не выбран", "Пожалуйста, выберите файл для разархивации.");
            return;
        }
        String algo = lz78RadioButton.isSelected() ? "LZ78" : "LZSS";
        String fileName = selectedFile.getName();
        String originalFileName = fileName.substring(0, fileName.lastIndexOf('.'));
        File outputFile = new File(selectedFile.getParent(), "uncompressed_" + originalFileName);
        resultFile = outputFile;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                    updateProgress(0, 10);

                    // имитация работы — 10 шагов по 300 мс
                    for (int i = 1; i <= 10; i++) {
                        Thread.sleep(300); // пауза
                        updateProgress(i, 10); // обновляем прогресс
                    }

                    if (algo.equals("LZ78")) {
                        new LZ78_Decompressor().decompress(selectedFile, outputFile);
                    } else {
                        new LZSS_Decompressor().decompress(selectedFile, outputFile);
                    }

                    // updateProgress(1, 1);
                    return null;

            }
        };

        runTask(task);
    }

    private void runTask(Task<Void> task) {
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);

        task.setOnSucceeded(e -> {
            // финал
            progressBar.progressProperty().unbind();
            progressBar.setProgress(1.0);
            progressBar.setVisible(false);
            resultFileLabel.setText("Результат: " + resultFile.getName());
            resultFileClose.setVisible(true);
            saveButton.setVisible(true);
        });

        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            progressBar.setVisible(false);
            resultFileBox.setVisible(false);
            resultFileClose.setVisible(false);
            resultFileLabel.setText("");
            resultFile = null;
            showAlert("Ошибка", "Произошла ошибка во время операции.");
            e.getSource().getException().printStackTrace();
        });

        new Thread(task).start();
        resultFileBox.setVisible(true);
    }

    @FXML
    private void saveFile() {
        if (resultFile != null && resultFile.exists()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(resultFile.getName());
            File dest = fileChooser.showSaveDialog(null);
            if (dest != null) {
                try {
                    java.nio.file.Files.copy(resultFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    showAlert("Ошибка сохранения", "Не удалось сохранить файл.");
                    e.printStackTrace();
                }
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void clearSelectedFile() {
        selectedFile = null;
        selectedFileLabel.setText("");
        selectedFileBox.setVisible(false);
        saveButton.setVisible(false);
    }

    @FXML
    private void clearResultFile() {
        resultFile = null;
        resultFileLabel.setText("");
        resultFileBox.setVisible(false);
        resultFileClose.setVisible(false);
        saveButton.setVisible(false);
    }

}