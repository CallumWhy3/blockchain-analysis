package com.cyoung.blockchain;

import com.cyoung.blockchain.controller.TransactionGrapherController;
import com.cyoung.blockchain.util.TransactionGrapher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws Exception{
        Application.launch(Main.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransactionGrapher.fxml"));
            loader.load();
            TransactionGrapherController controller = loader.getController();
            controller.setStage(primaryStage); // or what you want to do
            Parent root = FXMLLoader.load(getClass().getResource("/view/TransactionGrapher.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Blockchain analysis");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
