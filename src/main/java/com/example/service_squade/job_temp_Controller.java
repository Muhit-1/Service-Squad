package com.example.service_squade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class job_temp_Controller {

    private int postId;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label statusLabel;

    public void initialize(int postId) {
        this.postId = postId;
        loadData(postId);
    }

    public void loadData(int postId) {
        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";

        String query = """
                SELECT s.category, jp.job_description, jp.status
                FROM Job_Post jp
                JOIN Service s ON jp.service_id = s.service_id
                WHERE jp.post_id = ?;
                """;

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, postId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                categoryLabel.setText(resultSet.getString("category"));
                descriptionLabel.setText(resultSet.getString("job_description"));
                statusLabel.setText(resultSet.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("job_post.fxml"));
            Pane jobPostPane = loader.load();

            Job_post_Controller controller = loader.getController();
            controller.loadData(postId);

            Stage stage = new Stage();
            stage.setScene(new Scene(jobPostPane));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
