package com.example.service_squade;

import Database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void SignUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Signup_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            currentStage.setScene(scene);
            currentStage.setTitle("Register - Service Squad");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();

        String dbUrl = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Login Failed", "Please enter both email and password.");
            return;
        }

        try (Connection conn = DatabaseConnection.connect(dbUrl)) {
            String sql = "SELECT * FROM User WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userRole = rs.getString("role");
                    String userName = rs.getString("name");

                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Home.fxml"));
                    Scene homeScene = new Scene(fxmlLoader.load());
                    HomeController homeController = fxmlLoader.getController();
                    homeController.setLoggedInUserId(userId);


                    Stage currentStage = (Stage) usernameField.getScene().getWindow();
                    currentStage.setScene(homeScene);
                    currentStage.setTitle("Home - Service Squad");
                } else {

                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error during login. Please try again.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
