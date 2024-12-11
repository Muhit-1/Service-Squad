package com.example.service_squade;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Provider_profile_Controller {

    @FXML
    private Label cityLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    public void initialize(int userId) {
        loadData(userId);

    }

    private void loadData(int userId) {
        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        String query = """
                SELECT u.name, u.email, u.phone , l.city
                                FROM User u
                                JOIN Location l ON u.location_id = l.location_id
                                WHERE u.user_id = ?;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                nameLabel.setText(resultSet.getString("name"));
                emailLabel.setText(resultSet.getString("email"));
                phoneLabel.setText(resultSet.getString("phone"));
                cityLabel.setText(resultSet.getString("city"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
