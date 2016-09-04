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

    private final DoubleProperty imagePadding = new SimpleDoubleProperty(10.0);

    private MainWindowController mainWindowController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("MainWindow.fxml"));

        Parent root = (Pane) loader.load();

        mainWindowController = loader.getController();

        primaryStage.setTitle("Haar like features creator tool");
        primaryStage.setScene(new Scene(root, 1000, 700));

        mainWindowController.imageViewPane.setOnMouseMoved(event -> {
            String msg =
                    String.format("(x: % 4d , y: % 4d) ",
                            Math.round(event.getX()),
                            Math.round(event.getY())
                    );

            mainWindowController.mouseMonitor.setText(msg);
        });

        mainWindowController.imageViewPane.setTranslateX(imagePadding.doubleValue());
        mainWindowController.imageViewPane.setTranslateY(imagePadding.doubleValue());

        primaryStage.show();
    }
}
