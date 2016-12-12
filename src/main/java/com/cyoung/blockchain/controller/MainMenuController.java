package com.cyoung.blockchain.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuController {
    private Stage stage;

    public MainMenuController() {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void openBlockVisualiser(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/BlockVisualiser.fxml"));
        Scene scene = new Scene(parent);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void openGraphAnalyser(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/view/GraphAnalyser.fxml"));
        Scene scene = new Scene(parent);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void exitProgram(ActionEvent event) {
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
