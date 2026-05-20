package com.example.final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StartScreenController {

    @FXML
    private TextField usernameField;
    @FXML
    private Label startErrorLabel;

    @FXML
    public void onClickStart(ActionEvent event) throws IOException {
        String enteredName = usernameField.getText();

        if (enteredName == null || enteredName.trim().isEmpty()) {
            System.out.println("Error: Player tried to start without a name!");
            startErrorLabel.setText("Username is Required");
            return;
        }
        startErrorLabel.setText("");


        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-game.fxml"));
        Parent root = loader.load();


        BattleController battleController = loader.getController();


        battleController.setPlayerName(enteredName);


        Scene newScene = new Scene(root, 1280, 720);
        newScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(newScene);
        stage.show();
    }

    @FXML
    public void onHowToPlayClick(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/rules.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    public void onExitClick() {
        System.out.println("Closing the game. Goodbye!");
        System.exit(0);
    }
}