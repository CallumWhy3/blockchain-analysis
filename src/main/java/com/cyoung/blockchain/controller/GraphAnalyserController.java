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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphAnalyserController {
    private FileChooser fc;
    private File graphFile;
    private String graphFilePath;
    private Stage stage;
    private String pattern1, pattern2, pattern3;
    private static final Logger logger = LoggerFactory.getLogger(GraphAnalyserController.class);

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

    @FXML
    private Button removeCommonCaseButton;

    public GraphAnalyserController() {
    }

    @FXML
    private void initialize(){
        fc = new FileChooser();
        graphFilePath = PropertyLoader.LoadProperty("graphFileOutputLocation");
        graphFile = new File(graphFilePath);

        if(graphFile.exists() && !graphFile.isDirectory()) {
            selectedFile.setText(graphFilePath);
            executeSubdueButton.setDisable(false);
        }
    }

    @FXML
    private void openFileBrowser(){
        graphFile = fc.showOpenDialog(stage);
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
            pattern1 = subdueResultParser.getResult(1).replace("    ", "");
            pattern2 = subdueResultParser.getResult(2).replace("    ", "");
            pattern3 = subdueResultParser.getResult(3).replace("    ", "");
            outputTextArea.clear();
            outputTextArea.appendText("Pattern 1: " + pattern1 + "\n\n");
            outputTextArea.appendText("Pattern 2: " + pattern2 + "\n\n");
            outputTextArea.appendText("Pattern 3: " + pattern3 + "\n\n");
            updateProgress(4, 4);
            updateMessage("Done");
            removeCommonCaseButton.setDisable(false);
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
    private void removeCommonCase() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(graphFilePath));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            String fileAsString = sb.toString();
            FileWriter fileWriter = new FileWriter(graphFile, false);
            fileWriter.write(removeSubdueResultsFromString(fileAsString));
            fileWriter.close();
        } finally {
            br.close();
            removeCommonCaseButton.setDisable(true);
        }
    }

    private String removeSubdueResultsFromString(String fileAsString) {
        logger.info("Initial number of structures: " + getNumberOfStructuresInString(fileAsString));
        fileAsString = fileAsString.replace(pattern1, "");
        fileAsString = fileAsString.replace(pattern2, "");
        fileAsString = fileAsString.replace(pattern3, "");
        fileAsString = fileAsString.replace("XP\n\n", "");
        logger.info("Remaining number of structures: " + getNumberOfStructuresInString(fileAsString));
        return fileAsString;
    }

    private int getNumberOfStructuresInString(String file) {
        int numberOfStuctures = 0;
        Pattern pattern = Pattern.compile("XP");
        Matcher matcher = pattern.matcher(file);

        while (matcher.find()) {
            numberOfStuctures++;
        }

        return numberOfStuctures;
    }

    @FXML
    public void returnToMainMenu(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        Scene scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
