package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.util.BlockVisualiser;
import com.cyoung.blockchain.util.PropertyLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockVisualiserController {
    private static final Logger logger = LoggerFactory.getLogger(BlockVisualiserController.class);
    private Context context;
    private FileChooser fc = new FileChooser();
    private File blockFile;
    private Stage stage;
    private NetworkParameters params;
    private String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
    private String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField selectedFile;

    @FXML
    private Button produceGraphButton;

    @FXML
    private Label currentTask;

    @FXML
    private Button analyseButton;

    public BlockVisualiserController(){
        params = MainNetParams.get();
        context = new Context(params);
    }

    @FXML
    private void initialize(){
        fc = new FileChooser();
    }

    @FXML
    private void openFileBrowser(){
        blockFile = fc.showOpenDialog(stage);
        if(blockFile != null){
            produceGraphButton.setDisable(false);
            analyseButton.setDisable(true);
            selectedFile.setText(blockFile.toString());
        } else {
            produceGraphButton.setDisable(true);
            selectedFile.setText("");
        }
    }

    @FXML
    private void generateGraph() throws Exception {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
            Context.propagate(context);
            produceGraphButton.setDisable(true);

            updateMessage("Adding block file");
            List<File> blockFiles = new ArrayList<File>();
            blockFiles.add(blockFile);
            updateProgress(1, 10);

            updateMessage("Creating block file loader");
            BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);
            updateProgress(2, 10);

            updateMessage("Creating Neo4j session");
            Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
            Session session = driver.session();
            updateProgress(4, 10);

            updateMessage("Creating nodes and relationships");
            BlockVisualiser blockVisualiser = new BlockVisualiser(session);
            blockVisualiser.produceGraphFromBlock(blockFileLoader.next());
            updateProgress(8, 10);

            updateMessage("Closing Neo4j session");
            session.close();
            driver.close();
            selectedFile.setText("");
            updateProgress(10, 10);
            updateMessage("Done");
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
    public void openGraphAnalyser(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/GraphAnalyser.fxml"));
        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void returnToMainMenu(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
