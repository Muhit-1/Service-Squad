package com.example.service_squade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private AnchorPane Home;

    @FXML
    private TilePane tilePane;

    @FXML
    private TextField searchField;

    private int loggedInUserId;

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void initialize() {
        loadAllJobPosts(null);
    }

    private void loadAllJobPosts(String searchQuery) {
        tilePane.getChildren().clear();

        List<Integer> jobPostIds = getJobPostIdsFromDatabase(searchQuery);

        for (int postId : jobPostIds) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("job_temp.fxml"));
                Pane jobPane = loader.load();

                job_temp_Controller controller = loader.getController();
                controller.initialize(postId);

                tilePane.getChildren().add(jobPane);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> getJobPostIdsFromDatabase(String searchQuery) {
        List<Integer> jobPostIds = new ArrayList<>();

        String url = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";
        String query = "SELECT jp.post_id FROM Job_Post jp ";

        if (searchQuery != null && !searchQuery.isEmpty()) {
            query += "JOIN Service s on s.service_id = jp.service_id AND category like ?";
        }

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            if (searchQuery != null && !searchQuery.isEmpty()) {
                preparedStatement.setString(1, "%" + searchQuery + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                jobPostIds.add(resultSet.getInt("post_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jobPostIds;
    }

    @FXML
    private void load_addjob() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Job_post.fxml"));
            Scene jobPostScene = new Scene(fxmlLoader.load());
            Stage jobPostStage = new Stage();
            jobPostStage.setTitle("Job Post");
            jobPostStage.setScene(jobPostScene);
            jobPostStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadHome() {
        mainPane.setCenter(Home);
    }

    @FXML
    public void loadProviders() {
        loadPage("Provider_page.fxml");
    }

    @FXML
    public void loadSetting() {
        loadPage("About_page.fxml");
    }

    @FXML
    void loadProfile(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("User_Profile.fxml"));
            Node userProfilePage = loader.load();

            // Pass the logged-in user ID to the User_Profile_Controller
            User_Profile_Controller controller = loader.getController();
            controller.setUserId(loggedInUserId); // Use setter to pass the ID
            controller.initialize(); // Explicitly initialize after setting the userId

            mainPane.setCenter(userProfilePage);
            System.out.println("Loaded user profile for userId: " + loggedInUserId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadLogout() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login_page.fxml"));
            BorderPane root = loader.load();
            mainPane.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node newPage = loader.load();

            if ("User_Profile.fxml".equals(fxmlFile)) {
                User_Profile_Controller controller = loader.getController();
                controller.setUserId(getLoggedInUserId());
                controller.initialize();
            }

            mainPane.setCenter(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSearchFieldEnter() {
        String searchQuery = searchField.getText().trim();
        loadAllJobPosts(searchQuery);
    }

    @FXML
    void handleRefresh(MouseEvent event) {
        loadAllJobPosts(null);
    }
}