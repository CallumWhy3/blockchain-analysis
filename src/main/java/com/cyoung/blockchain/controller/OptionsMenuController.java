package com.cyoung.blockchain.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;

public class OptionsMenuController {
    private Parent parent;
    private Scene scene;
    private Stage stage;
    // Static value means the menu can retain this value after being opened and closed
    public static double anomalyWeightValue = 0.05;

    @FXML
    private Slider anomalyWeightThresholdSlider;

    @FXML
    private Label weightValueLabel;

    /**
     * Initialise anomaly weight threshold slider
     */
    @FXML
    private void initialize() {
        anomalyWeightThresholdSlider.setValue(anomalyWeightValue);
        updateWeightValue();
        anomalyWeightThresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                anomalyWeightValue = anomalyWeightThresholdSlider.getValue();
                updateWeightValue();
            }
        });
    }

    /**
     * Update label with value of anomaly weight threshold slider
     */
    @FXML
    private void updateWeightValue() {
        double weight = anomalyWeightThresholdSlider.getValue();
        weightValueLabel.setText(String.format("%.2f", weight));
    }

    /**
     * Open main manu page
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
