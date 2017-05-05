package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.object.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import com.cyoung.blockchain.util.GraphGenerator;
import com.cyoung.blockchain.util.PropertyLoader;
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
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.io.IOException;
import java.util.List;

public class AnomalyVisualiserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;

    @FXML
    private TextField selectedBlock;

    @FXML
    private Button produceGraphButton, analyseButton;

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
     * Display dialog to confirm user wants to create graph of anomalous transactions and
     * overwrite any existing Neo4j nodes or relationships
     */
    @FXML
    private void confirmGenerateGraph() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete all nodes and relationships in the current neo4j graph, do you still want to continue?");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                generateGraph();
            }
        });
    }

    /**
     * Create Neo4j graph of anomalous transactions
     */
    private void generateGraph() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                progressSpinner.setVisible(true);
                currentTask.setLayoutX(51);

                updateMessage("Creating Neo4j session");
                AudioClip jobDone = new AudioClip(getClass().getResource("/audio/job-done.mp3").toString());
                produceGraphButton.setDisable(true);
                String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
                String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
                Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
                Session session = driver.session();
                updateProgress(1, 4);

                updateMessage("Creating nodes and relationships");
                GraphGenerator graphGenerator = new GraphGenerator(session);
                for (BitcoinTransaction t : BlockAnalyser.anomalousTransactions) {
                    graphGenerator.graphTransaction(t.getTransaction());
                }
                updateProgress(3, 4);

                updateMessage("Closing Neo4j session");
                session.close();
                driver.close();
                updateProgress(4, 4);

                updateMessage("Done");
                jobDone.play();
                progressSpinner.setVisible(false);
                currentTask.setLayoutX(26);
                analyseButton.setDisable(false);

                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    /**
     * Open anomaly analyser page
     * @param event Event from button
     * @throws IOException  FXML file cannot be found
     */
    @FXML
    private void openAnomalyAnalyser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/AnomalyAnalyser.fxml"));
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
