package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.object.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import com.cyoung.blockchain.util.GraphGenerator;
import com.cyoung.blockchain.util.PropertyLoader;
import info.blockchain.api.APIException;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiveBlockAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private BlockExplorer blockExplorer;
    private String latestBlockAnalysedHash = "";
    private List<BitcoinTransaction> anomalousTransactions;

    @FXML
    private ProgressBar nextBlockProgressBar, visualiseProgressBar;

    @FXML
    private TextField latestBlockTextField;

    @FXML
    private Label nextBlockEstimateLabel, visualiseCurrentTaskLabel;

    @FXML
    private ProgressIndicator nextBlockSpinner, visualiseSpinner;

    @FXML
    private TableView<BitcoinTransaction> anomalousTransactionsTable;

    @FXML
    private TableColumn<BitcoinTransaction, String> anomalousTransactionHash, anomalousTransactionWeight;

    @FXML
    private Button visualiseButton;

    /**
     * Initialise live anomaly detction by checking for the latest block
     */
    @FXML
    private void initialize() {
        blockExplorer = new BlockExplorer();
        anomalousTransactionHash.setCellValueFactory(new PropertyValueFactory<>("hash"));
        anomalousTransactionWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        try {
            checkLatestBlock();
        } catch (APIException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for latest block and update estimate on when the next block will be added to the blockchain
     * @throws APIException Latest block cannot be found
     * @throws IOException  Latest block cannot be found
     */
    private void checkLatestBlock() throws APIException, IOException {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                updateMessage("Checking latest block...");
                nextBlockSpinner.setVisible(true);

                // Get latest block in Block format
                Block latestBlock = blockExplorer.getBlock(blockExplorer.getLatestBlock().getHash());

                Date latestBlockTime = new Date(latestBlock.getTime() * 1000);
                Date currentTime = new Date();

                int timeDiffInMinutes = (int)((currentTime.getTime() - latestBlockTime.getTime()) / (1000 * 60));
                updateMessage("Latest block was solved " + String.valueOf(timeDiffInMinutes) + " minutes ago");
                updateTitle(latestBlock.getHash());

                if (timeDiffInMinutes < 10) {
                    updateProgress(timeDiffInMinutes, 10);
                } else {
                    updateProgress(10, 10);
                }

                try {
                    return null;
                } finally {
                    analyseLatestBlock(latestBlock);
                }
            }
        };
        latestBlockTextField.textProperty().bind(task.titleProperty());
        nextBlockEstimateLabel.textProperty().bind(task.messageProperty());
        nextBlockProgressBar.progressProperty().bind(task.progressProperty());

        Thread checkLatestBlockThread = new Thread(task);
        checkLatestBlockThread.setDaemon(true);
        checkLatestBlockThread.start();
    }

    /**
     * Analyse latest block for anomalous transactions and then sleep for 15 seconds
     * @param latestBlock
     */
    private void analyseLatestBlock(Block latestBlock) {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                String latestBlockHash = latestBlock.getHash();
                if (!latestBlockAnalysedHash.equals(latestBlockHash)) {
                    BlockAnalyser blockAnalyser = new BlockAnalyser();

                    // Create list containing only block we want to analyse
                    List<Block> blockList = new ArrayList<>();
                    blockList.add(latestBlock);

                    // Get list of anomalous transactions from latest block
                    anomalousTransactions = blockAnalyser.calculateAnomalousTransactions(blockList);
                    anomalousTransactionsTable.getItems().setAll(anomalousTransactions);

                    latestBlockAnalysedHash = latestBlockHash;
                }

                try {
                    visualiseButton.setDisable(false);
                    // The blockchain api requests that values should be updated at max every 10 seconds so I chose 15 to be safe
                    Thread.sleep(15000);
                    return null;
                } finally {
                    checkLatestBlock();
                }
            }
        };
        Thread analyseLatestBlockThread = new Thread(task);
        analyseLatestBlockThread.setDaemon(true);
        analyseLatestBlockThread.start();
    }

    /**
     * Display dialog to confirm user wants to create graph of anomalous transactions and
     * overwrite any existing Neo4j nodes or relationships
     */
    @FXML
    private void confirmVisualiseTransactions() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete all nodes and relationships in the current neo4j graph, do you still want to continue?");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                visualiseTransactions();
            }
        });
    }

    /**
     * Create Neo4j graph of anomalous transactions found in the latest block
     */
    private void visualiseTransactions() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                visualiseSpinner.setVisible(true);
                visualiseCurrentTaskLabel.setLayoutX(51);

                updateMessage("Creating Neo4j session");
                visualiseButton.setDisable(true);
                String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
                String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
                Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
                Session session = driver.session();
                updateProgress(1, 4);

                updateMessage("Creating nodes and relationships");
                GraphGenerator graphGenerator = new GraphGenerator(session);
                for (BitcoinTransaction t : anomalousTransactions) {
                    graphGenerator.graphTransaction(t.getTransaction());
                }
                updateProgress(3, 4);

                updateMessage("Closing Neo4j session");
                session.close();
                driver.close();
                updateProgress(4, 4);

                updateMessage("Done");
                visualiseSpinner.setVisible(false);
                visualiseCurrentTaskLabel.setLayoutX(26);
                return null;
            }
        };

        visualiseProgressBar.progressProperty().bind(task.progressProperty());
        visualiseCurrentTaskLabel.textProperty().bind(task.messageProperty());

        Thread visualiseTransactionsThread = new Thread(task);
        visualiseTransactionsThread.setDaemon(true);
        visualiseTransactionsThread.start();
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
