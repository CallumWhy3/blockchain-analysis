package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.object.BitcoinTransaction;
import com.cyoung.blockchain.object.ReoccurringInputRow;
import com.cyoung.blockchain.object.ReoccurringOutputRow;
import com.cyoung.blockchain.util.BlockAnalyser;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
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
import java.util.*;

public class AnomalyAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;

    @FXML private Label totalTransactionsLabel, totalBitcoinsTransferredLabel, anomalyWeightThresholdLabel;
    @FXML private Label totalAnomalousTransactionsLabel, totalAnomalousBitcoinsTransferredLabel, percentageAnomalousTransactionsLabel;
    @FXML private TableView<BitcoinTransaction> anomalousTransactionTable;
    @FXML private TableColumn<BitcoinTransaction, String> weightColumn, hashColumn;
    @FXML private TableView<ReoccurringInputRow> reoccurringInputsTable;
    @FXML private TableColumn<ReoccurringInputRow, String> inputAddress, inputOccurrences;
    @FXML private TableView<ReoccurringOutputRow> reoccurringOutputsTable;
    @FXML private TableColumn<ReoccurringOutputRow, String> outputAddress, outputOccurrences;

    /**
     * Calculate and set values for stats section and tables
     */
    @FXML
    private void initialize() {

        // Set total transactions statistic
        int totalTransactions = 0;
        for (Block block : BlockVisualiserController.blocks) {
            totalTransactions += block.getTransactions().size();
        }
        totalTransactionsLabel.setText(String.valueOf(totalTransactions));

        // Set anomaly weight threshold statistic
        double anomalyWeightThreshold = OptionsMenuController.anomalyWeightValue;
        String formattedWeightThreshold = String.format("%.2f", anomalyWeightThreshold);
        anomalyWeightThresholdLabel.setText(formattedWeightThreshold);

        // Set total anomalous transactions statistic
        int totalAnomalousTransactions = BlockAnalyser.anomalousTransactions.size();
        totalAnomalousTransactionsLabel.setText(String.valueOf(totalAnomalousTransactions));

        // Set percentage anomalous transactions statistic
        double percentageAnomalousTransactions = ((double) totalAnomalousTransactions / (double) totalTransactions) * 100;
        String formattedPercentage = String.format("%.2f", percentageAnomalousTransactions) + "%";
        percentageAnomalousTransactionsLabel.setText(formattedPercentage);

        // Set total Bitcoins transferred in all transactions
        double totalBitcoinsTransferred = 0;
        for (Block block : BlockVisualiserController.blocks) {
            totalBitcoinsTransferred += calculateTotalBitcoinsTransferred(block.getTransactions());
        }
        totalBitcoinsTransferredLabel.setText(totalBitcoinsTransferred + "BTC");

        // Set total Bitcoins transferred in anomalous transactions
        totalBitcoinsTransferred = 0;
        List<Transaction> anomalousTransactions = new ArrayList<>();
        for (BitcoinTransaction t : BlockAnalyser.anomalousTransactions) {
            anomalousTransactions.add(t.getTransaction());
        }
        System.out.println(anomalousTransactions.size() + "###################\n##########\n#######\n#######\n########");
        totalBitcoinsTransferred += calculateTotalBitcoinsTransferred(anomalousTransactions);
        totalAnomalousBitcoinsTransferredLabel.setText(totalBitcoinsTransferred + "BTC");

        // Populate table of anomalous transactions
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        hashColumn.setCellValueFactory(new PropertyValueFactory<>("hash"));
        anomalousTransactionTable.getItems().setAll(BlockAnalyser.anomalousTransactions);

        // Populate table of reoccurring inputs
        inputAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        inputOccurrences.setCellValueFactory(new PropertyValueFactory<>("occurrences"));
        reoccurringInputsTable.getItems().setAll(listReoccurringInputs(anomalousTransactions));

        // Populate table of reoccurring outputs
        outputAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        outputOccurrences.setCellValueFactory(new PropertyValueFactory<>("occurrences"));
        reoccurringOutputsTable.getItems().setAll(listReoccurringOutputs(anomalousTransactions));
    }

    /**
     * Find all inputs that occur more than once in a list of transactions
     * @param transactions  List of transactions to search through for inputs
     * @return  List of inputs that occur more than once
     */
    private List<ReoccurringInputRow> listReoccurringInputs(List<Transaction> transactions) {
        // Create list of all inputs
        List<String> inputs = new ArrayList<>();
        for (Transaction t : transactions) {
            for (Input i : t.getInputs()) {
                inputs.add(i.getPreviousOutput().getAddress());
            }
        }

        // Create list of all inputs that occur more than once
        List<ReoccurringInputRow> reoccurringInputs = new ArrayList<>();
        for (String s : inputs) {
            int occurrences = Collections.frequency(inputs, s);
            boolean contains = false;
            for (ReoccurringInputRow r : reoccurringInputs) {
                if (r.getAddress().equals(s)) {
                    contains = true;
                    break;
                }
            }
            if (occurrences > 1 && !contains) {
                ReoccurringInputRow r = new ReoccurringInputRow(s, occurrences);
                reoccurringInputs.add(r);
            }
        }

        return reoccurringInputs;
    }

    /**
     * Find all outputs that occur more than once in a list of transactions
     * @param transactions  List of transactions to search through for outputs
     * @return  List of outputs that occur more than once
     */
    private List<ReoccurringOutputRow> listReoccurringOutputs(List<Transaction> transactions) {
        // Create list of all outputs
        List<String> outputs = new ArrayList<>();
        for (Transaction t : transactions) {
            for (Output o : t.getOutputs()) {
                outputs.add(o.getAddress());
            }
        }

        // Create list of all outputs that occur more than once
        List<ReoccurringOutputRow> reoccurringOutputs = new ArrayList<>();
        for (String s : outputs) {
            int occurrences = Collections.frequency(outputs, s);
            boolean contains = false;
            for (ReoccurringOutputRow r : reoccurringOutputs) {
                if (r.getAddress().equals(s)) {
                    contains = true;
                    break;
                }
            }
            if (occurrences > 1 && !contains) {
                ReoccurringOutputRow r = new ReoccurringOutputRow(s, occurrences);
                reoccurringOutputs.add(r);
            }
        }

        return reoccurringOutputs;
    }

    /**
     * Calculate total number of Bitcoins transferred in a lift of transactions
     * @param transactions  List of transactions you that transfer Bitcoins
     * @return  Total Bitcoins transferred
     */
    private double calculateTotalBitcoinsTransferred(List<Transaction> transactions) {
        double totalBitcoinsTransferred = 0;

        // Loop through the output of each transaction and add the value transferred to the total
        for (Transaction t : transactions ) {
            for (Output o : t.getOutputs()) {
                totalBitcoinsTransferred += o.getValue();
            }
        }

        // Format value from Satoshi to Bitcoin
        return totalBitcoinsTransferred * 0.00000001;
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
