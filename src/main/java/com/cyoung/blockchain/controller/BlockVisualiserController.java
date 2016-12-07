package com.cyoung.blockchain.controller;

import com.cyoung.blockchain.util.BlockVisualiser;
import com.cyoung.blockchain.util.PropertyLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private FileChooser fc = new FileChooser();
    private Stage stage;
    private File blockFile;

    @FXML
    private TextField selectedFile;

    @FXML
    private Button produceGraphButton;

    public BlockVisualiserController(){
    }

    @FXML
    private void initialize(){
        fc = new FileChooser();
        fc.setTitle("Select .dat file");
    }

    @FXML
    private void openFileBrowser(){
        blockFile = fc.showOpenDialog(stage);
        if(blockFile != null){
            produceGraphButton.setDisable(false);
            selectedFile.setText(blockFile.toString());
            loadBlockFile(blockFile.getPath());
        } else {
            produceGraphButton.setDisable(true);
            selectedFile.setText("");
        }
    }

    @FXML
    private void generateGraph() throws Exception {
        NetworkParameters params = MainNetParams.get();
        Context context = new Context(params);

        List<File> blockFiles = new ArrayList<File>();
        blockFiles.add(blockFile);

        BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);

        String neo4jName = PropertyLoader.LoadProperty("neo4jUsername");
        String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");

        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jName, neo4jPassword));
        Session session = driver.session();

        BlockVisualiser blockVisualiser = new BlockVisualiser(session);
        blockVisualiser.produceGraphFromBlock(blockFileLoader.next());

        session.close();
        driver.close();
    }

    public void returnToMainMenu(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void loadBlockFile(String path) {
        blockFile = new File(path);
    }
}
