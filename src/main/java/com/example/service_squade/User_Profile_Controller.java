package com.example.service_squade;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User_Profile_Controller {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField oldPasswordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private Button saveButton;

    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void initialize() {
        if (userId > 0) {
            loadData();
        } else {
            System.out.println("Invalid user ID: " + userId);
        }
    }

    private void loadData() {
        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        String query = """
                SELECT name, email, phone, address
                FROM User
                WHERE user_id = ?
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Populate the fields with data from the database
                nameField.setText(resultSet.getString("name"));
                emailField.setText(resultSet.getString("email"));
                phoneField.setText(resultSet.getString("phone"));
                addressField.setText(resultSet.getString("address"));
            } else {
                System.out.println("User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveUserData() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (validateFields()) {
            if (!newPassword.isEmpty()) {
                newPassword = hashPassword(newPassword);
            }

            // First update user data, then update password if necessary
            updateUserData(name, email, phone, address, oldPassword, newPassword);
        } else {
            System.out.println("Validation failed. Please check your input.");
        }
    }

    private void updateUserData(String name, String email, String phone, String address, String oldPassword, String newPassword) {
        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        String query = """
                UPDATE User
                SET name = ?, email = ?, phone = ?, address = ?
                WHERE user_id = ? AND password = ?
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setInt(5, userId);
            preparedStatement.setString(6, hashPassword(oldPassword));

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User data updated successfully!");

                // If the password was changed, update the password in the database
                if (!newPassword.isEmpty()) {
                    updatePassword(newPassword);
                }
            } else {
                System.out.println("Failed to update user data. Check your old password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePassword(String newPassword) {
        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        String query = """
                UPDATE User
                SET password = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean validateFields() {
        // Simple validation check for fields
        return !nameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}
