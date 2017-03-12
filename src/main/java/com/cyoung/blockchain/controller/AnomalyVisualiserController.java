package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.util.AnomalyVisualiser;
import com.cyoung.blockchain.util.PropertyLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.io.File;
import java.io.IOException;

public class AnomalyVisualiserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private FileChooser fc;
    private File graphFile;

    @FXML
    private TextField selectedFile;

    @FXML
    private Button fileSelectButton;

    @FXML
    private Button produceGraphButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label currentTask;

    @FXML
    private void initialize() {
        fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Graph files (*.g)", "*.g");
        fc.getExtensionFilters().add(extFilter);

        String graphFilePath = PropertyLoader.LoadProperty("graphFileOutputDirectory") + "/subdueGraph.g";
        graphFile = new File(graphFilePath);

        if(graphFile.exists() && !graphFile.isDirectory()) {
            selectedFile.setText(graphFilePath);
            produceGraphButton.setDisable(false);
        }
    }

    @FXML
    public void openFileBrowser() {
        graphFile = fc.showOpenDialog(stage);
        if(graphFile != null){
            produceGraphButton.setDisable(false);
            selectedFile.setText(graphFile.toString());
        } else {
            produceGraphButton.setDisable(true);
            selectedFile.setText("");
        }
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

            updateMessage("Creating Neo4j session");
            fileSelectButton.setDisable(true);
            produceGraphButton.setDisable(true);
            String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
            String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
            Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
            Session session = driver.session();
            updateProgress(1, 4);

            updateMessage("Creating nodes and relationships");
            AnomalyVisualiser av = new AnomalyVisualiser(session);
            av.produceGraphFromGraphFile(graphFile);
            updateProgress(3, 4);

            updateMessage("Done");
            updateProgress(4, 4);
            fileSelectButton.setDisable(false);
            produceGraphButton.setDisable(false);

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
    public void returnToMainMenu(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
