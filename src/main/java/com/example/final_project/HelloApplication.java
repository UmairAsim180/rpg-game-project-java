package com.example.final_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // The slash (/) at the front tells Java to start at the absolute root of the resources folder
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/final_project/start_screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        // Use the absolute path for the CSS as well
        scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

        stage.setTitle("Path of the Silent Sword");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}