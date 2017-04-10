package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.domain.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.BlockExplorer;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class BlockAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private static final Logger logger = LoggerFactory.getLogger(BlockAnalyserController.class);

    @FXML
    private TextField selectedBlock;

    @FXML
    private Button analyseButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ProgressIndicator progressSpinner;

    @FXML
    private Label currentTask;

    @FXML
    private void initialize() {
        selectedBlock.setText(BlockVisualiserController.block.getHash());
    }

    @FXML
    private void analyseBlock() throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

            progressSpinner.setVisible(true);
            currentTask.setLayoutX(51);
            updateProgress(1, 5);

            updateTitle("Finding block");
            BlockExplorer blockExplorer = new BlockExplorer();
            String blockHash = BlockVisualiserController.block.getHash();
            Block block = blockExplorer.getBlock(blockHash);
            updateProgress(2, 5);

            updateTitle("Finding anomalous transactions");
            BlockAnalyser blockAnalyser = new BlockAnalyser(block);
            String message = "";
            int counter = 1;
            for (BitcoinTransaction t : blockAnalyser.calculateAnomalousTransactions()) {
                message += "Anomalous transaction " + counter + "\nHash: " + t.getHash() + " \nWeight: " + t.getWeight() + "\nBitcoins transferred: " + t.getTotalBitcoinsInput() * 0.00000001 + "BTC\n\n";
                updateMessage(message);
                logger.info("\n" + t.getHash() + " identified as anomalous\nWeight: " + t.getWeight() + "\nBitcoins transferred: " + t.getTotalBitcoinsInput() * 0.00000001 + "BTC\n");
            }
            updateProgress(4, 5);

            updateTitle("Done");
            progressSpinner.setVisible(false);
            currentTask.setLayoutX(26);
            analyseButton.setDisable(false);
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

    @FXML
    private void openAnomalyVisualiser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/AnomalyVisualiser.fxml"));
        scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

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
