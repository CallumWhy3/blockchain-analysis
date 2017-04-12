package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.domain.BitcoinTransaction;
import com.cyoung.blockchain.util.BlockAnalyser;
import com.cyoung.blockchain.util.GraphGenerator;
import com.cyoung.blockchain.util.PropertyLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.io.IOException;

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

    @FXML
    private void initialize() {
        selectedBlock.setText(BlockVisualiserController.block.getHash());
    }


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

    private void generateGraph() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

            progressSpinner.setVisible(true);
            currentTask.setLayoutX(51);
            updateMessage("Creating Neo4j session");
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
            progressSpinner.setVisible(false);
            currentTask.setLayoutX(26);
            produceGraphButton.setDisable(false);
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

    @FXML
    private void openAnomalyAnalyser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/AnomalyAnalyser.fxml"));
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
