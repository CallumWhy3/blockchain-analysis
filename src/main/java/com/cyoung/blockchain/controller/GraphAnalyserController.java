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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphAnalyserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private FileChooser fc;
    private File graphFile;
    private String graphFilePath;
    private String pattern1, pattern2, pattern3;
    private static final Logger logger = LoggerFactory.getLogger(GraphAnalyserController.class);

    @FXML
    private TextField selectedFile;

    @FXML
    private Button fileSelectButton, executeSubdueButton;

    @FXML
    private Button removeSubdueResult1Button, removeSubdueResult2Button, removeSubdueResult3Button, removeCommonCaseButton, visualiseResultsButton;

    @FXML
    private TextArea outputTextArea;

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

        graphFilePath = PropertyLoader.LoadProperty("graphFileOutputDirectory") + "/subdueGraph.g";
        graphFile = new File(graphFilePath);

        if (graphFile.exists() && !graphFile.isDirectory()) {
            selectedFile.setText(graphFilePath);
            executeSubdueButton.setDisable(false);
        }
    }

    @FXML
    private void openFileBrowser() {
        graphFile = fc.showOpenDialog(stage);
        if (graphFile != null) {
            graphFilePath = graphFile.getPath();
            executeSubdueButton.setDisable(false);
            selectedFile.setText(graphFile.toString());
        } else {
            executeSubdueButton.setDisable(true);
            selectedFile.setText("");
        }
    }

    @FXML
    private void executeSubdue() throws IOException {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

            updateMessage("Searching for subdue executable");
            fileSelectButton.setDisable(true);
            executeSubdueButton.setDisable(true);
            String subdueLocation = PropertyLoader.LoadProperty("subdueBaseDirectory") + "/bin/";
            File subdueExecutable = new File(subdueLocation + "subdue");
            if (!subdueExecutable.exists() || subdueExecutable.isDirectory()) {
                currentTask.setTextFill(Color.web("#f43636"));
                updateMessage("No subdue executable in \'" + subdueLocation + "\' reconfigure and restart");
                updateProgress(0, 4);
                return null;
            }
            updateProgress(1, 4);

            updateMessage("Executing subdue");
            String command = subdueLocation + "./subdue " + graphFilePath;
            String result = executeCommand(command);
            updateProgress(3, 4);

            updateMessage("Analysing results");
            parserSubdueResults(result);
            updateProgress(4, 4);

            updateMessage("Done");
            fileSelectButton.setDisable(false);
            executeSubdueButton.setDisable(false);
            removeCommonCaseButton.setDisable(false);
            removeSubdueResult1Button.setDisable(false);
            removeSubdueResult2Button.setDisable(false);
            removeSubdueResult3Button.setDisable(false);
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

    private void parserSubdueResults(String result) {
        SubdueResultParser subdueResultParser = new SubdueResultParser(result);
        pattern1 = subdueResultParser.getResult(1).replace("    ", "");
        pattern2 = subdueResultParser.getResult(2).replace("    ", "");
        pattern3 = subdueResultParser.getResult(3).replace("    ", "");
        outputTextArea.clear();
        outputTextArea.appendText("Pattern 1:\n" + pattern1 + "\n\n");
        outputTextArea.appendText("Pattern 2:\n" + pattern2 + "\n\n");
        outputTextArea.appendText("Pattern 3:\n" + pattern3 + "\n\n");
    }

    @FXML
    private void removeCommonStructures() throws IOException {
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
            fileWriter.write(removeCommonStructuresFromString(fileAsString));
            fileWriter.close();
        } finally {
            br.close();
            removeCommonCaseButton.setDisable(true);
            visualiseResultsButton.setDisable(false);
        }
    }

    private String removeCommonStructuresFromString(String fileAsString) {
        logger.info("Initial number of structures: " + getNumberOfStructuresInString(fileAsString));

        fileAsString = fileAsString.replace("v 1 transaction\n" +
                                            "v 2 input\n" +
                                            "v 3 output\n" +
                                            "d 2 1 input\n" +
                                            "d 1 3 output\n\n", "");

        fileAsString = fileAsString.replace("v 1 transaction\n" +
                                            "v 2 input\n" +
                                            "v 3 output\n" +
                                            "v 4 output\n" +
                                            "d 2 1 input\n" +
                                            "d 1 3 output\n" +
                                            "d 1 4 output\n\n", "");

        fileAsString = fileAsString.replace("v 1 transaction\n" +
                                            "v 2 input\n" +
                                            "v 3 input\n" +
                                            "v 4 output\n" +
                                            "d 2 1 input\n" +
                                            "d 3 1 input\n" +
                                            "d 1 4 output\n\n", "");

        fileAsString = fileAsString.replace("v 1 transaction\n" +
                                            "v 2 input\n" +
                                            "v 3 input\n" +
                                            "v 4 output\n" +
                                            "v 5 output\n" +
                                            "d 2 1 input\n" +
                                            "d 3 1 input\n" +
                                            "d 1 4 output\n" +
                                            "d 1 5 output\n\n", "");

        fileAsString = fileAsString.replaceAll("(XP\\n)+", "XP\n");
        fileAsString = fileAsString.replaceAll("XP\\n\\B", "");
        logger.info("Remaining number of structures: " + getNumberOfStructuresInString(fileAsString));

        return fileAsString;
    }

    @FXML
    private void removeSubdueResult(ActionEvent event) throws IOException {
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
            String buttonName = event.getSource().toString();
            int resultNumber = Integer.valueOf(buttonName.substring(buttonName.length()-2, buttonName.length()-1));

            if (resultNumber == 1) {
                fileWriter.write(removePatternFromString(pattern1, fileAsString));
                removeSubdueResult1Button.setDisable(true);
            } else if (resultNumber == 2) {
                fileWriter.write(removePatternFromString(pattern2, fileAsString));
                removeSubdueResult2Button.setDisable(true);
            } else if (resultNumber == 3) {
                fileWriter.write(removePatternFromString(pattern3, fileAsString));
                removeSubdueResult3Button.setDisable(true);
            }

            fileWriter.close();
        } finally {
            br.close();
            visualiseResultsButton.setDisable(false);
        }
    }

    private String removePatternFromString(String pattern, String fileAsString) {
        logger.info("Initial number of structures: " + getNumberOfStructuresInString(fileAsString));
        fileAsString = fileAsString.replace(pattern+"\n", "");
        fileAsString = fileAsString.replace("XP\n\n", "");
        logger.info("Remaining number of structures: " + getNumberOfStructuresInString(fileAsString));
        return fileAsString;
    }

    private int getNumberOfStructuresInString(String fileAsString) {
        int numberOfStuctures = 0;
        Pattern pattern = Pattern.compile("XP");
        Matcher matcher = pattern.matcher(fileAsString);

        while (matcher.find()) {
            numberOfStuctures++;
        }

        return numberOfStuctures;
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
