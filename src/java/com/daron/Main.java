package com.daron;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("MainWindow.fxml"));
        Parent root = (Pane) loader.load();

        primaryStage.setTitle("Haar-like features creator tool");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }
}
