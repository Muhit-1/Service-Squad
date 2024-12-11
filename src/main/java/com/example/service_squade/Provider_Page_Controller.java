package com.example.service_squade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Provider_Page_Controller {


    @FXML
    private TextField searchField;

    @FXML
    private TilePane tilePane;

    public void initialize() {
        loadAllUser(null);
    }

    private void loadAllUser(String searchQuery) {

        tilePane.getChildren().clear();

        List<Integer> UserProfileIds = getUserIdsFromDatabase(searchQuery);

        for (int userId : UserProfileIds) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Provider_profile.fxml"));
                Pane userPane = loader.load();

                Provider_profile_Controller controller = loader.getController();
                controller.initialize(userId);

                tilePane.getChildren().add(userPane);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> getUserIdsFromDatabase(String searchQuery) {
        List<Integer> UserProfileIds = new ArrayList<>();

        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";
        String query = "SELECT user_id FROM User WHERE role = 'Provider'";

        if (searchQuery != null && !searchQuery.isEmpty()) {
            query += "AND name like ?";
        }

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (searchQuery != null && !searchQuery.isEmpty()) {
                preparedStatement.setString(1, "%" + searchQuery + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserProfileIds.add(resultSet.getInt("user_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return UserProfileIds;
    }

    @FXML
    private void onSearchFieldEnter() {
        String searchQuery = searchField.getText().trim();
        loadAllUser(searchQuery);
    }


}
