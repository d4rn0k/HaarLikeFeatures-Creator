package daron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.Arrays;

public class Main extends Application {


    private MainWindowController mainWindowController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));

        Parent root = (Pane) loader.load();

        mainWindowController = loader.getController();

        primaryStage.setTitle("Haar like features");
        primaryStage.setScene(new Scene(root, 1000, 700));

        Rectangle rect = createDraggableRectangle(25, 25, 40, 40);
        rect.setFill(Color.NAVY);


        root.setOnMouseMoved(event -> {
            String msg =
                    "Pozycja kursora: (x: " + event.getX() + ", y: " + event.getY() + ") -- " +
                            "(sceneX: " + event.getSceneX() + ", sceneY: " + event.getSceneY() + ") -- " +
                            "(screenX: " + event.getScreenX() + ", screenY: " + event.getScreenY() + ")";

            mainWindowController.mouseMonitor.setText(msg);
        });
        mainWindowController.pane.getChildren().add(rect);

        primaryStage.show();
    }

    private Rectangle createDraggableRectangle(double x, double y, double width, double height) {
        final double handleRadius = 4;

        Rectangle rect = new Rectangle(x, y, width, height);

        rect.getStyleClass().add("haarLikeFeature");

        // top left resize handle:
        Circle resizeCircleNW = new Circle(handleRadius, Color.WHITE);
        resizeCircleNW.getStyleClass().add("resizeCircle");
        resizeCircleNW.centerXProperty().bind(rect.xProperty());
        resizeCircleNW.centerYProperty().bind(rect.yProperty());


        // bottom right resize handle:
        Circle resizeCircleSE = new Circle(handleRadius, Color.BLUE);
        resizeCircleSE.getStyleClass().add("resizeCircle");
        resizeCircleSE.centerXProperty().bind(rect.xProperty().add(rect.widthProperty()));
        resizeCircleSE.centerYProperty().bind(rect.yProperty().add(rect.heightProperty()));


        // force circles to live in same parent as rectangle:
        rect.parentProperty().addListener((obs, oldParent, newParent) -> {
            for (Circle c : Arrays.asList(resizeCircleNW, resizeCircleSE)) {
                Pane currentParent = (Pane) c.getParent();
                if (currentParent != null) {
                    currentParent.getChildren().remove(c);
                }
                ((Pane) newParent).getChildren().add(c);
            }
        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();

        setUpDragging(resizeCircleNW, mouseLocation);
        setUpDragging(resizeCircleSE, mouseLocation);
        setUpDragging(rect, mouseLocation);

        resizeCircleNW.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();

                double newX = rect.getX() + deltaX;
                if (newX >= 0 && newX <= rect.getX() + rect.getWidth()) {
                    rect.setX(newX);
                    rect.setWidth(rect.getWidth() - deltaX);
                }

                double newY = rect.getY() + deltaY;
                if (newY >= 0 && newY <= rect.getY() + rect.getHeight()) {
                    rect.setY(newY);
                    rect.setHeight(rect.getHeight() - deltaY);
                }

                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        resizeCircleSE.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();

                double newMaxX = rect.getX() + rect.getWidth() + deltaX;
                if (newMaxX >= rect.getX() && newMaxX <= rect.getParent().getBoundsInLocal().getWidth()) {
                    rect.setWidth(rect.getWidth() + deltaX);
                }

                double newMaxY = rect.getY() + rect.getHeight() + deltaY;
                if (newMaxY >= rect.getY() && newMaxY <= rect.getParent().getBoundsInLocal().getHeight()) {
                    rect.setHeight(rect.getHeight() + deltaY);
                }

                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }
        });

        rect.setOnMouseDragged(event -> {
            if (mouseLocation.value != null) {
                double deltaX = event.getSceneX() - mouseLocation.value.getX();
                double deltaY = event.getSceneY() - mouseLocation.value.getY();


                double newX = rect.getX() + deltaX;
                double newMaxX = newX + rect.getWidth();
                if (newX >= 0 && newMaxX <= rect.getParent().getBoundsInLocal().getWidth()) {
                    rect.setX(newX);
                }

                double newY = rect.getY() + deltaY;
                double newMaxY = newY + rect.getHeight();
                if (newY >= 0 && newMaxY <= rect.getParent().getBoundsInLocal().getHeight()) {
                    rect.setY(newY);
                }

                mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
            }

        });

        return rect;
    }

    private void setUpDragging(Shape circle, Wrapper<Point2D> mouseLocation) {

        circle.setOnDragDetected(event -> {
            circle.getParent().setCursor(Cursor.CLOSED_HAND);
            mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
        });

        circle.setOnMouseReleased(event -> {
            circle.getParent().setCursor(Cursor.DEFAULT);
            mouseLocation.value = null;
        });
    }

    private static class Wrapper<T> {
        T value;
    }

}
