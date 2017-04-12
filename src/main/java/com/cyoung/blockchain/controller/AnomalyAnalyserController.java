package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.domain.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import info.blockchain.api.blockexplorer.Block;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class AnomalyAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;

    @FXML
    private Label totalTransactionsLabel, totalBitcoinsTransferredLabel;

    @FXML
    private Label totalAnomalousTransactionsLabel, totalAnomalousBitcoinsTransferredLabel, percentageAnomalousTransactionsLabel;

    @FXML
    private TableView<BitcoinTransaction> anomalousTransactionTable;

    @FXML
    private TableColumn<BitcoinTransaction, String> weightColumn, hashColumn;

    @FXML
    private void initialize() {
        Block block = BlockVisualiserController.block;
        int totalTransactions = block.getTransactions().size();
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));

        ArrayList<BitcoinTransaction> anomalousTransactions = BlockAnalyser.anomalousTransactions;
        int totalAnomalousTransactions = anomalousTransactions.size();
        totalAnomalousTransactionsLabel.setText(String.valueOf(totalAnomalousTransactions));

        double percentageAnomalousTransactions = ((double) totalAnomalousTransactions / (double) totalTransactions) * 100;
        String formattedPercentage = String.format("%.2f", percentageAnomalousTransactions) + "%";
        percentageAnomalousTransactionsLabel.setText(formattedPercentage);

        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        hashColumn.setCellValueFactory(new PropertyValueFactory<>("hash"));
        anomalousTransactionTable.getItems().setAll(anomalousTransactions);
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
