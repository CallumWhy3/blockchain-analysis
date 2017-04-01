package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.util.BlockVisualiser;
import com.cyoung.blockchain.util.PropertyLoader;
import info.blockchain.api.APIException;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.BlockExplorer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockVisualiserController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    private FileChooser fc;
    private File blockFile;
    private Context context;
    private NetworkParameters params;
    private boolean inFileMode = true;
    private String blockHash;

    @FXML
    private ToggleButton inputModeToggleButton;

    @FXML
    private Text inputModeText;

    @FXML
    private TextField selectedFileField;

    @FXML
    private TextField blockHashField;

    @FXML
    private Button fileSelectButton;

    @FXML
    private Button validateBlockHashButton;

    @FXML
    private Button produceGraphButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button analyseButton;

    @FXML
    private Label currentTask;

    @FXML
    private void initialize() {
        fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Block files (*.dat)", "*.dat");
        fc.getExtensionFilters().add(extFilter);

        params = MainNetParams.get();
        context = new Context(params);

        initializeToggleButton();
    }

    private void initializeToggleButton() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
                produceGraphButton.setDisable(true);
                if (new_toggle == null) {
                    useBlockFileMode();
                } else {
                    useBlockHashMode();
                }
            }
        });

        inputModeToggleButton.setToggleGroup(group);
    }

    private void useBlockHashMode() {
        inputModeText.setText("Enter block hash");
        selectedFileField.setText("");
        selectedFileField.setVisible(false);
        fileSelectButton.setVisible(false);
        blockHashField.setVisible(true);
        validateBlockHashButton.setVisible(true);
        inFileMode = false;
    }

    private void useBlockFileMode() {
        inputModeText.setText("Select .dat file");
        blockHashField.setText("");
        blockHashField.setVisible(false);
        validateBlockHashButton.setVisible(false);
        selectedFileField.setVisible(true);
        fileSelectButton.setVisible(true);
        inFileMode = true;
    }

    @FXML
    private void openFileBrowser() {
        blockFile = fc.showOpenDialog(stage);
        if(blockFile != null){
            produceGraphButton.setDisable(false);
            analyseButton.setDisable(true);
            selectedFileField.setText(blockFile.toString());
        } else {
            produceGraphButton.setDisable(true);
            selectedFileField.setText("");
        }
    }

    @FXML
    private void confirmProduceGraph() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete all nodes and relationships in the current neo4j graph, do you still want to continue?");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produceGraph();
            }
        });
    }

    @FXML
    private void produceGraph() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {
            Context.propagate(context);
            fileSelectButton.setDisable(true);
            produceGraphButton.setDisable(true);

            updateMessage("Creating Neo4j session");
            String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
            String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
            Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
            Session session = driver.session();
            updateProgress(4, 10);

            updateMessage("Creating nodes and relationships");
            BlockVisualiser blockVisualiser = new BlockVisualiser(session);
            blockHash = getInputHash();
            blockVisualiser.produceGraphFromBlockHash(blockHash);
            updateProgress(8, 10);

            updateMessage("Closing Neo4j session");
            session.close();
            driver.close();
            selectedFileField.setText("");
            updateProgress(10, 10);

            updateMessage("Done");
            fileSelectButton.setDisable(false);
            analyseButton.setDisable(false);

            return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
        Thread uiThread = new Thread(task);
        uiThread.setDaemon(true);
        uiThread.start();
    }

    private String getInputHash() {
        if (inFileMode) {
            List<File> blockFiles = new ArrayList<File>();
            blockFiles.add(blockFile);
            BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);
            return blockFileLoader.next().getHash().toString();
        } else {
            return blockHashField.getText();
        }
    }

    @FXML
    private void validateHashBlock() {
        if (blockHashField.getText().length() == 64) {
            try {
                BlockExplorer blockExplorer = new BlockExplorer();
                blockExplorer.getBlock(blockHashField.getText());
                produceGraphButton.setDisable(false);
                currentTask.setText("");
            } catch (APIException | IOException e) {
                produceGraphButton.setDisable(true);
                currentTask.setText("Invalid block hash, please try again");
            }
        } else {
            produceGraphButton.setDisable(true);
            currentTask.setText("Invalid block hash, please try again");
        }
    }

    @FXML
    private void disableProduceGraphButton() {
        produceGraphButton.setDisable(true);
    }

    @FXML
    public void openGraphAnalyser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/GraphAnalyser.fxml"));
        scene = new Scene(parent);
        scene.getStylesheets().add("/css/style.css");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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
