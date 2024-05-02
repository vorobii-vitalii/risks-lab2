package com.example.riskslab2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MyApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        var rootPanel = new RootPane(new Model());
        var scene = new Scene(rootPanel);
        primaryStage.setTitle("Lab 2");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}