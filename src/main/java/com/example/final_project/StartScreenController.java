package com.example.final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreenController {

    @FXML
    private TextField usernameField;

    @FXML
    public void onClickStart(ActionEvent event) throws IOException {
        String playerName = usernameField.getText();

        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.println("Error: Player tried to start without a name!");
            return;
        }

        System.out.println("Starting game for: " + playerName);

        // Removed "views/"
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-game.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, 1280, 720);

        // Removed "styles/"
        newScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    public void onExitClick() {
        System.out.println("Closing the game. Goodbye!");
        System.exit(0);
    }
}