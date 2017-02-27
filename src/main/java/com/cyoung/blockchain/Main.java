package com.cyoung.blockchain;

import com.cyoung.blockchain.controller.MainMenuController;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            loader.load();
            MainMenuController controller = loader.getController();
            controller.setStage(primaryStage);
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/style.css");
            primaryStage.setScene(scene);
            primaryStage.setTitle("Blockchain analysis");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
