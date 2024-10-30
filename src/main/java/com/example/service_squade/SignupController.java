package com.example.service_squade;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<String> roleChoiceBox;

    public void initialize() {
        // Initialize the ChoiceBox with options
        roleChoiceBox.setItems(FXCollections.observableArrayList("Provider", "Customer"));
    }

    @FXML
    private void handleSignUp() {
        String name = nameField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String role = roleChoiceBox.getValue();

        if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || password.isEmpty() || role == null) {
            showAlert("All fields are required.");
            return;
        }

        try (Connection conn = Database.DatabaseConnection.connect()) {
            String sql = "INSERT INTO User (name, email, phone, address, password, role, registration_date) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setString(4, address);
                pstmt.setString(5, password);
                pstmt.setString(6, role);
                pstmt.executeUpdate();
                showAlert("Registration successful!");
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Registration failed. Error: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        addressField.clear();
        phoneField.clear();
        passwordField.clear();
        roleChoiceBox.setValue(null);
    }
}
