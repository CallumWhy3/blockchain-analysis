package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.object.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import info.blockchain.api.blockexplorer.Block;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class BlockAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private static final Logger logger = LoggerFactory.getLogger(BlockAnalyserController.class);

    @FXML
    private TextField selectedBlock;

    @FXML
    private Button analyseButton, visualiseButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressSpinner;

    @FXML
    private Label currentTask;

    /**
     * Initialise selected block hash field
     */
    @FXML
    private void initialize() {
        List<Block> blocks = BlockVisualiserController.blocks;
        if (blocks.size() > 1) {
            selectedBlock.setText("Multiple blocks selected");
        } else {
            selectedBlock.setText(blocks.get(0).getHash());
        }
    }

    /**
     * Analyse block for anomalous transactions
     */
    @FXML
    private void analyseBlock() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                progressSpinner.setVisible(true);
                currentTask.setLayoutX(51);
                analyseButton.setDisable(true);
                updateProgress(1, 5);

                updateTitle("Preparing analyser");
                AudioClip jobDone = new AudioClip(getClass().getResource("/audio/job-done.mp3").toString());
                updateProgress(2, 5);

                updateTitle("Finding anomalous transactions");
                BlockAnalyser blockAnalyser = new BlockAnalyser();
                String message = "";
                int counter = 1;
                for (BitcoinTransaction t : blockAnalyser.calculateAnomalousTransactions(BlockVisualiserController.blocks)) {
                    message += "Anomalous transaction " + counter + "\nHash: " + t.getHash() + " \nWeight: " + t.getWeight() + "\nBitcoins transferred: " + t.getTotalBitcoinsInput() * 0.00000001 + "BTC\n\n";
                    updateMessage(message);
                    logger.info("\n" + t.getHash() + " identified as anomalous\nWeight: " + t.getWeight() + "\nBitcoins transferred: " + t.getTotalBitcoinsInput() * 0.00000001 + "BTC\n");
                    counter++;
                }
                updateProgress(4, 5);

                updateTitle("Done");
                jobDone.play();
                progressSpinner.setVisible(false);
                currentTask.setLayoutX(26);
                visualiseButton.setDisable(false);
                updateProgress(5, 5);

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.titleProperty());
        outputTextArea.textProperty().bind(task.messageProperty());
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    /**
     * Open anomaly visualiser page
     * @param event Event from button
     * @throws IOException  FXML file cannot be found
     */
    @FXML
    private void openAnomalyVisualiser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/AnomalyVisualiser.fxml"));
        scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Open main menu page
     * @param event Event from button
     * @throws IOException  FXML file cannot be found
     */
    @FXML
    private void returnToMainMenu(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
