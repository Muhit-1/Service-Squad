package com.example.service_squade;

import Database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private ChoiceBox<String> cityChoiceBox;


    public void initialize() {

        roleChoiceBox.setItems(FXCollections.observableArrayList("Provider", "Customer"));
        cityChoiceBox.setItems(FXCollections.observableArrayList("Dhaka", "Chottogram", "Barishal", "Cumilla"));
    }


    @FXML
    private void handleSignUp() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String city = cityChoiceBox.getValue();
        String role = roleChoiceBox.getValue();

        if (!validateInput(name, email, password, phone, city, role)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill all fields correctly.");
            return;
        }

        String mainDbUrl = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";
        String specificDbUrl = determineDatabase(city, role);

        try {

            insertUser(mainDbUrl, name, email, password, phone, address, city, role);

            insertUser(specificDbUrl, name, email, password, phone, address, city, role);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully.");

            redirectToLogin();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error during registration. Please try again.");
        }
    }

    private boolean validateInput(String name, String email, String password, String phone, String city, String role) {
        return !(name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank() || city.isBlank() ||
                (!role.equalsIgnoreCase("Provider") && !role.equalsIgnoreCase("Customer")));
    }

    private String determineDatabase(String city, String role) {
        if (city.equalsIgnoreCase("Dhaka") && role.equalsIgnoreCase("Provider")) {
            return "jdbc:sqlite:src/main/java/Database/db_dhaka_provider.db";
        } else if (city.equalsIgnoreCase("Dhaka") && role.equalsIgnoreCase("Customer")) {
            return "jdbc:sqlite:src/main/java/Database/db_dhaka_customer.db";
        } else if (!city.equalsIgnoreCase("Dhaka") && role.equalsIgnoreCase("Provider")) {
            return "jdbc:sqlite:src/main/java/Database/db_other_provider.db";
        } else {
            return "jdbc:sqlite:src/main/java/Database/db_other_customer.db";
        }
    }

    private void insertUser(String dbUrl, String name, String email, String password, String phone,
                            String address, String city, String role) throws SQLException {
        String getLocationIdQuery = "SELECT location_id FROM Location WHERE city = ?;";
        String insertUserQuery = """
                    INSERT INTO User (name, email, password, phone, address, location_id, role)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DatabaseConnection.connect(dbUrl);
             PreparedStatement locationStmt = conn.prepareStatement(getLocationIdQuery);
             PreparedStatement userStmt = conn.prepareStatement(insertUserQuery)) {

            locationStmt.setString(1, city);
            var resultSet = locationStmt.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException("City not found in Location table: " + city);
            }
            int locationId = resultSet.getInt("location_id");


            userStmt.setString(1, name);
            userStmt.setString(2, email);
            userStmt.setString(3, password);
            userStmt.setString(4, phone);
            userStmt.setString(5, address);
            userStmt.setInt(6, locationId); // Include location_id
            userStmt.setString(7, role);

            userStmt.executeUpdate();
            System.out.println("User added to database: " + dbUrl);
        }
    }


    private void redirectToLogin() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login_page.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load());

        Stage currentStage = (Stage) nameField.getScene().getWindow();
        currentStage.setScene(loginScene);
        currentStage.setTitle("Login - Service Squade");
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());


            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();


            currentStage.setScene(scene);
            currentStage.setTitle("Login - Service Squade");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Unable to load the login page.");
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
