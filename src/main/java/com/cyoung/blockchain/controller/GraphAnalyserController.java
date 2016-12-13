package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.util.PropertyLoader;
import com.cyoung.blockchain.util.SubdueResultParser;
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
import org.bitcoinj.core.NetworkParameters;

import java.io.File;
import java.io.IOException;

public class GraphAnalyserController {
    private FileChooser fc = new FileChooser();
    private String graphFilePath;
    private Stage stage;
    private NetworkParameters params;

    @FXML
    private TextField selectedFile;

    @FXML
    private Button executeSubdueButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label currentTask;

    public GraphAnalyserController() {
    }

    @FXML
    private void initialize(){
        fc = new FileChooser();
        String graphFileOutputLocation = PropertyLoader.LoadProperty("graphFileOutputLocation");
        File f = new File(graphFileOutputLocation);

        if(f.exists() && !f.isDirectory()) {
            graphFilePath = graphFileOutputLocation;
            selectedFile.setText(graphFileOutputLocation);
            executeSubdueButton.setDisable(false);
        }
    }

    @FXML
    private void openFileBrowser(){
        File graphFile = fc.showOpenDialog(stage);
        if(graphFile != null){
            graphFilePath = graphFile.getPath();
            executeSubdueButton.setDisable(false);
            selectedFile.setText(graphFile.toString());
        } else {
            executeSubdueButton.setDisable(true);
            selectedFile.setText("");
        }
    }

    @FXML
    public void executeSubdue() throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

            updateMessage("Retrieving subdue location");
            String subdueLocation = PropertyLoader.LoadProperty("subdueLocation");
            updateProgress(1, 4);

            updateMessage("Executing subdue");
            String command = subdueLocation + "./subdue " + graphFilePath;
            String result = executeCommand(command);
            updateProgress(3, 4);

            updateMessage("Analysing results");
            SubdueResultParser subdueResultParser = new SubdueResultParser(result);
            outputTextArea.appendText("Pattern 1:" + subdueResultParser.getResult(0) + "\n\n");
            outputTextArea.appendText("Pattern 2:" + subdueResultParser.getResult(1) + "\n\n");
            outputTextArea.appendText("Pattern 3:" + subdueResultParser.getResult(2) + "\n\n");
            updateProgress(4, 4);
            updateMessage("Done");
            return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private String executeCommand(String cmd) throws java.io.IOException {
        Runtime runtime = Runtime.getRuntime();
        java.util.Scanner s = new java.util.Scanner(runtime.exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
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
