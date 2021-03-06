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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.AudioClip;
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
    private BlockExplorer blockExplorer = new BlockExplorer();

    // List of blocks made static so they can be accessed by different controller classes
    public static List<Block> blocks;

    @FXML private ToggleButton inputModeToggleButton;
    @FXML private Text inputModeText;
    @FXML private TextField selectedFileField, blockHashField;
    @FXML private Button fileSelectButton, validateBlockHashButton, addBlockButton, produceGraphButton, analyseButton;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressSpinner;
    @FXML private Label currentTask;
    @FXML private TableView selectedBlockTable;
    @FXML private TableColumn<Block, String> blockHashColumn;

    /**
     * Initialise file chooser and input mode toggle button
     */
    @FXML
    private void initialize() {
        blocks = new ArrayList<>();

        // Initialise file chooser and add extension filter so only .dat files can be selected
        fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Block files (*.dat)", "*.dat");
        fc.getExtensionFilters().add(extFilter);

        params = MainNetParams.get();
        context = new Context(params);

        initializeToggleButton();
    }

    /**
     * Set behaviour of toggle button in its different states
     */
    private void initializeToggleButton() {
        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
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
        inFileMode = false;
        inputModeText.setText("Enter block hash");

        // Reset and hide file select ui
        selectedFileField.setText("");
        selectedFileField.setVisible(false);
        fileSelectButton.setVisible(false);

        // Show hash input ui
        blockHashField.setVisible(true);
        validateBlockHashButton.setVisible(true);
        addBlockButton.setDisable(true);
    }

    private void useBlockFileMode() {
        inFileMode = true;
        inputModeText.setText("Select .dat file");

        // Rest and hide hash input ui
        blockHashField.setText("");
        blockHashField.setVisible(false);
        validateBlockHashButton.setVisible(false);

        // Show file select ui
        selectedFileField.setVisible(true);
        fileSelectButton.setVisible(true);
        addBlockButton.setDisable(true);
    }

    /**
     * Opens file browser so block files can be selected
     */
    @FXML
    private void openFileBrowser() {
        blockFile = fc.showOpenDialog(stage);
        if (blockFile != null) {
            addBlockButton.setDisable(false);
            selectedFileField.setText(blockFile.toString());
        } else {
            selectedFileField.setText("");
        }
    }

    /**
     * Display dialog to confirm user wants to create graph of a blocks transactions and
     * overwrite any existing Neo4j nodes or relationships
     */
    @FXML
    private void confirmProduceGraph() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will delete all nodes and relationships in the current neo4j graph, do you still want to continue?");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produceGraph();
            }
        });
    }

    /**
     * Create Neo4j graph of transactions in user specified block
     */
    private void produceGraph() {
        Task<Void> task = new Task<Void>() {
            @Override public Void call() throws Exception {

                currentTask.setLayoutX(51);
                progressSpinner.setVisible(true);
                // Scale progress bar with number of blocks being visualised
                int numberOfBlocks = blocks.size();
                int maxProgress = numberOfBlocks + 5;
                updateProgress(1, maxProgress);

                updateMessage("Preparing API");
                AudioClip jobDone = new AudioClip(getClass().getResource("/audio/job-done.mp3").toString());
                // Disable buttons while graph is being produced
                inputModeToggleButton.setDisable(true);
                fileSelectButton.setDisable(true);
                validateBlockHashButton.setDisable(true);
                produceGraphButton.setDisable(true);
                analyseButton.setDisable(true);
                Context.propagate(context);
                updateProgress(2, maxProgress);

                updateMessage("Creating Neo4j session");
                // Load Neo4j credentials from config file to create a Neo4j session
                String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
                String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
                Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
                Session session = driver.session();
                updateProgress(3, maxProgress);

                updateMessage("Creating nodes and relationships");
                // Create list of block hashes for use in bock visualiser
                List<String> hashes = new ArrayList<>();
                for (Block block : blocks) {
                    hashes.add(block.getHash());
                }
                BlockVisualiser blockVisualiser = new BlockVisualiser();
                blockVisualiser.produceGraphFromBlockHashes(session, hashes);
                updateProgress(maxProgress - 1, maxProgress);

                updateMessage("Closing Neo4j session");
                session.close();
                driver.close();
                updateProgress(maxProgress, maxProgress);

                updateMessage("Done");
                jobDone.play();
                progressSpinner.setVisible(false);
                currentTask.setLayoutX(26);
                analyseButton.setDisable(false);

                return null;
            }
        };

        // If blocks have been selected then start the above task
        if (blocks.size() > 0) {
            progressBar.progressProperty().bind(task.progressProperty());
            currentTask.textProperty().bind(task.messageProperty());
            Thread uiThread = new Thread(task);
            uiThread.setDaemon(true);
            uiThread.start();
        } else {
            currentTask.setText("No blocks selected");
        }
    }

    /**
     * Add block to list of blocks that will be visualised
     */
    @FXML
    private void addBlock() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                currentTask.setLayoutX(51);
                progressSpinner.setVisible(true);
                // Disable buttons while searching for block
                inputModeToggleButton.setDisable(true);
                fileSelectButton.setDisable(true);
                validateBlockHashButton.setDisable(true);
                addBlockButton.setDisable(true);

                updateMessage("Finding block");
                updateProgress(1, 4);
                Context.propagate(context);

                // Check if user hash provided a block file or hash
                if (inFileMode) {
                    updateMessage("Adding block");
                    updateProgress(2, 4);

                    // Load block
                    List<File> blockFiles = new ArrayList<File>();
                    blockFiles.add(blockFile);
                    BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);

                    // Add block if deemed valid by BlockFileLoader
                    if (blockFileLoader.hasNext()) {
                        String hash = blockFileLoader.next().getHash().toString();
                        blocks.add(blockExplorer.getBlock(hash));
                    } else {
                        progressSpinner.setVisible(false);
                        currentTask.setLayoutX(26);
                        // Re-enable buttons so user can try another input
                        inputModeToggleButton.setDisable(false);
                        fileSelectButton.setDisable(false);
                        validateBlockHashButton.setDisable(false);
                        updateMessage("Invalid file, no block added");
                        updateProgress(4, 4);
                        return null;
                    }
                } else {
                    blocks.add(blockExplorer.getBlock(blockHashField.getText()));
                }
                progressSpinner.setVisible(false);
                currentTask.setLayoutX(26);
                // Re-enable buttons once block has been added successfully
                inputModeToggleButton.setDisable(false);
                fileSelectButton.setDisable(false);
                validateBlockHashButton.setDisable(false);
                // Once there is at least one valid block selected the user can produce a graph or analyse the data
                produceGraphButton.setDisable(false);
                analyseButton.setDisable(false);
                updateMessage("Block added");
                updateProgress(4, 4);
                updateBlockTable();

            return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
        Thread addBlockThread = new Thread(task);
        addBlockThread.setDaemon(true);
        addBlockThread.start();
    }

    /**
     * Update table of blocks
     */
    private void updateBlockTable() {
        blockHashColumn.setCellValueFactory(new PropertyValueFactory<>("hash"));
        selectedBlockTable.getItems().setAll(blocks);
    }

    /**
     * Validate that block exists with user provided hash
     */
    @FXML
    private void validateBlockHash() {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                // Hash must be 64 characters exactly
                if (blockHashField.getText().length() == 64) {
                    currentTask.setLayoutX(51);
                    progressSpinner.setVisible(true);
                    updateProgress(1,2);
                    updateMessage("Finding block");
                    try {
                        // Use block explorer to search for block with provided hash
                        BlockExplorer blockExplorer = new BlockExplorer();
                        blockExplorer.getBlock(blockHashField.getText());
                        addBlockButton.setDisable(false);
                        updateProgress(2,2);
                        updateMessage("Block found");

                    } catch (APIException | IOException e) {
                        // Exception will be thrown if block with specified hash does not exist
                        addBlockButton.setDisable(true);
                        updateProgress(2,2);
                        updateMessage("Invalid block hash");
                    }
                } else {
                    // Safe to assume hash is invalid if not exactly 64 characters long
                    addBlockButton.setDisable(true);
                    updateProgress(2,2);
                    updateMessage("Invalid block hash");
                }
                progressSpinner.setVisible(false);
                currentTask.setLayoutX(26);
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        currentTask.textProperty().bind(task.messageProperty());
        Thread findBlockThread = new Thread(task);
        findBlockThread.setDaemon(true);
        findBlockThread.start();
    }

    /**
     * Open block analyser page
     * @param event Event from button
     * @throws IOException  FXML file cannot be found
     */
    @FXML
    private void openBlockAnalyser(ActionEvent event) throws IOException {
        parent = FXMLLoader.load(getClass().getResource("/view/BlockAnalyser.fxml"));
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
