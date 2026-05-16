package com.example.final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class GameOverController {

    @FXML private Label resultTitle;     // Link to "Victory/Defeat" label
    @FXML private Label resultSubtitle;  // Link to "You have slain..." label

    // This custom method allows the BattleController to pass data to this screen!
    public void setGameResult(boolean playerWon, String message) {
        if (playerWon) {
            resultTitle.setText("VICTORY");
            resultTitle.setStyle("-fx-text-fill: #d4af37;"); // Gold color
        } else {
            resultTitle.setText("DEFEAT");
            resultTitle.setStyle("-fx-text-fill: #c0392b;"); // Crimson red color
        }
        resultSubtitle.setText(message);
    }

    @FXML
    public void onPlayAgainClick(ActionEvent event) throws IOException {
        // Reload the main game screen absolute path
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/main-game.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    public void onExitClick() {
        System.exit(0);
    }
}