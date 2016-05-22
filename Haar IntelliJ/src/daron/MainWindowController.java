package daron;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;

public class MainWindowController {

    @FXML
    public Label imgStatusLabel;
    @FXML
    public ImageView imageView;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public Pane pane;
    public Label mouseMonitor;


    @FXML
    private void initialize() {
        openImage(new File("obrazek.jpg"));
    }


    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);

        openImage(file);
    }

    private void openImage(File file) {
        if (file == null) {
            return;
        }
        try {
            Image myImg = new Image(file.toURI().toString());

            double imgWidth = myImg.getWidth();
            double imgHeight = myImg.getHeight();

            imgStatusLabel.setText(String.format("w: %.0f x h: %.0f", imgWidth, imgHeight));
            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);
            imageView.setImage(myImg);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
