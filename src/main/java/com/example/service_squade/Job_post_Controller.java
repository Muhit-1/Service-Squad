package com.example.service_squade;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.sql.*;

public class Job_post_Controller {

    @FXML
    private TextArea descriptionField;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    private static final String DB_URL = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

    private int loggedInUserId;

    public void initialize() {

        categoryChoiceBox.setItems(FXCollections.observableArrayList("Home Cooking", "Gardener", "Baby Sitting", "Interior designer"));
        statusChoiceBox.setItems(FXCollections.observableArrayList("Open", "Closed", "In Progress"));
    }


    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @FXML
    private void handleAdd() {
        String description = descriptionField.getText();
        String status = statusChoiceBox.getValue();
        String category = categoryChoiceBox.getValue();

        if (!validateInput(description, status, category)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill all fields correctly.");
            return;
        }

        int serviceId = getServiceId(category); // Map the category to a service ID

        if (serviceId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid category selected.");
            return;
        }

        String sql = "INSERT INTO Job_Post (user_id, service_id, job_description, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loggedInUserId);
            pstmt.setInt(2, serviceId);
            pstmt.setString(3, description);
            pstmt.setString(4, status);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job post added successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add job post.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        String description = descriptionField.getText();

        if (description.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Description field cannot be empty.");
            return;
        }
        String sql = "DELETE FROM Job_Post WHERE job_description = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, description);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job post deleted successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete job post. Make sure the description exists.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        String description = descriptionField.getText();
        String status = statusChoiceBox.getValue();
        String category = categoryChoiceBox.getValue();

        if (!validateInput(description, status, category)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill all fields correctly.");
            return;
        }


        int serviceId = getServiceId(category);

        if (serviceId == -1) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid category selected.");
            return;
        }

        String sql = "UPDATE Job_Post SET status = ?, service_id = ? WHERE job_description = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, serviceId);
            pstmt.setString(3, description);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Job post updated successfully!");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update job post. Make sure the description exists.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }


    private int getServiceId(String category) {
        switch (category) {
            case "Home Cooking":
                return 1;
            case "Gardener":
                return 2;
            case "Baby Sitting":
                return 3;
            case "Interior designer":
                return 4;
            default:
                return -1;
        }
    }

    private boolean validateInput(String description, String status, String category) {
        return !(description.isBlank() || status == null || category == null ||
                (!status.equalsIgnoreCase("Open") && !status.equalsIgnoreCase("Closed") && !status.equalsIgnoreCase("In Progress")));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        descriptionField.clear();
        statusChoiceBox.setValue(null);
        categoryChoiceBox.setValue(null);
    }

    public void loadData(int postId) {
        String query = """
                SELECT jp.job_description, jp.status, s.category
                FROM Job_Post jp
                JOIN Service s ON jp.service_id = s.service_id
                WHERE jp.post_id = ?;
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                descriptionField.setText(rs.getString("job_description"));
                statusChoiceBox.setValue(rs.getString("status"));
                categoryChoiceBox.setValue(rs.getString("category"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

}
