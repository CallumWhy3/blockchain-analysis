package com.cyoung.blockchain;

import com.cyoung.blockchain.controller.BlockVisualiserController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws Exception{
        Application.launch(Main.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BlockVisualiser.fxml"));
            loader.load();
            BlockVisualiserController controller = loader.getController();
            controller.setStage(primaryStage); // or what you want to do
            Parent root = FXMLLoader.load(getClass().getResource("/view/BlockVisualiser.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Blockchain analysis");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
