package com.example.final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HowToPlayController {

    @FXML
    public void onBackClick(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/start_screen.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);


        scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}