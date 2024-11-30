package com.example.service_squade;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //database conn
        Database.DatabaseConnection.initializeDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("Login_page.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 900, 600)); // Set size as needed
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
