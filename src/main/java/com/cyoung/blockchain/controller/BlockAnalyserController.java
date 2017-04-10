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
    private Block block;
    private static final Logger logger = LoggerFactory.getLogger(BlockAnalyserController.class);

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
    private void analyseBlock() throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

            progressSpinner.setVisible(true);
            currentTask.setLayoutX(51);
            updateProgress(1, 5);

            updateMessage("Finding block");
            BlockExplorer blockExplorer = new BlockExplorer();
            String blockHash = BlockVisualiserController.block.getHash();
            block = blockExplorer.getBlock(blockHash);
            updateProgress(2, 5);

            updateMessage("Finding anomalous transactions");
            BlockAnalyser blockAnalyser = new BlockAnalyser(block);
            for (BitcoinTransaction t : blockAnalyser.calculateAnomalousTransactions()) {
                logger.info("\n" + t.getHash() + " identified as anomalous\nWeight: " + t.getWeight() + "\nBitcoins transferred: " + t.getTotalBitcoinsInput() * 0.00000001 + "BTC\n\n");
            }
            updateProgress(4, 5);

            updateMessage("Done");
            progressSpinner.setVisible(false);
            currentTask.setLayoutX(26);
            analyseButton.setDisable(false);
            updateProgress(5, 5);

            return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
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
