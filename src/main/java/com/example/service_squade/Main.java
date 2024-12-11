package com.example.service_squade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Database.DatabaseConnection.initializeDatabase();

        Parent root = FXMLLoader.load(getClass().getResource("Login_page.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
