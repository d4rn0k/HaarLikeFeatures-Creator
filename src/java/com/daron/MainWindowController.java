package com.daron;

import com.daron.haar.features.*;
import com.daron.utils.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.daron.utils.GrayScaleConverter.convertToGrayScale;


@XmlRootElement
public class MainWindowController {

    private IntegralCreatorAbstract imageCreator;
    private double imgWidth;
    private double imgHeight;
    private Canvas initialPoint;
    private final NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

    @FXML
    public Label imgStatusLabel;
    @FXML
    public ImageView imageView;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public Pane imageViewPane;
    @FXML
    public Label mouseMonitor;
    @FXML
    public Label pointALabel;
    @FXML
    public Label pointBLabel;
    @FXML
    public Label pointCLabel;
    @FXML
    public Label pointDLabel;
    @FXML
    public Label rectangleArealabel;
    @FXML
    public Label sumOfPixelsLabel;
    @FXML
    public Label rectangleWidthHeightLabel;
    @FXML
    public AnchorPane anchorPaneForImageView;
    @FXML
    public ListView<IHaar> haarListView;
    @FXML
    public Label initialPointLabel;
    @FXML
    public Label fromInitialPointDistanceLabel;
    @FXML
    public Label avgLabel;

    @FXML
    private void initialize() {
        openImage(new File("example-input-image.jpg"));

        DragResizeMod.setMainWindowController(this);

        haarListView.setCellFactory(lv -> new HaarListViewCell());

        imageViewPane.addEventFilter(MouseEvent.ANY, event -> {
            IHaar selectedItem = haarListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setCurrentHaarRectangleLabels(selectedItem);
            }
        });

        setInitialPointGraphicsContext();

        initialPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, (t) -> setInitialPointLabel());

        DragResizeMod initialPointResizer = new DragResizeMod(initialPoint, imgWidth, imgHeight);
        initialPointResizer.setMarginsForDrag(initialPoint.getWidth() / 2, initialPoint.getHeight() / 2)
                .makeOnlyDraggable();


        imageViewPane.getChildren().add(initialPoint);
        handleAddNewHaar(null);
    }

    private void setInitialPointGraphicsContext() {
        initialPoint = new Canvas(10, 10);
        initialPoint.setLayoutX(10);
        initialPoint.setLayoutY(10);

        GraphicsContext gc = initialPoint.getGraphicsContext2D();

        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.4));
        gc.fillRect(0, 0, 10, 10);

        gc.setLineWidth(1.0);

        gc.moveTo(0, 5);
        gc.lineTo(10, 5);
        gc.stroke();

        gc.moveTo(5, 0);
        gc.lineTo(5, 10);
        gc.stroke();
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        openImage(fileChooser.showOpenDialog(null));
    }

    @FXML
    public void handleSaveHaarFeatureAsFile(ActionEvent actionEvent) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(XMLHaarFeature.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        List<XMLHaar> xmlList = new ArrayList<>();

        for (IHaar singleHaar : haarListView.getItems()) {

            MyPoint distanceFromInitialPoint = getDistanceFromInitialPoint(singleHaar);

            xmlList.add(new XMLHaar(
                    distanceFromInitialPoint.getX(),
                    distanceFromInitialPoint.getY(),
                    singleHaar.getIntegerWidth(),
                    singleHaar.getIntegerHeight(),
                    singleHaar.isRotated()
            ));
        }

        XMLHaarFeature xmlHaarFeature = new XMLHaarFeature(
                ((int) (initialPoint.getLayoutX() + initialPoint.getWidth() / 2)), // 10/2=5
                ((int) (initialPoint.getLayoutY() + initialPoint.getHeight() / 2)), //10/2=5
                xmlList
        );

        marshaller.marshal(xmlHaarFeature, System.out);

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("."));
        fc.setTitle("Zapisz cechy haara do pliku XML!");

        File file = fc.showSaveDialog(null);
        if (file == null) {
            return;
        }

        marshaller.marshal(xmlHaarFeature, file);
        file.createNewFile();
    }

    @FXML
    public void handleAddNewHaar(ActionEvent actionEvent) {
        addNewHaarAndSetListeners(false);
    }

    @FXML
    public void handleAddNewRotatedHaar(ActionEvent actionEvent) {
        addNewHaarAndSetListeners(true);
    }

    void setListViewSelection(IHaar selection) {
        haarListView.getSelectionModel().select(selection);
    }

    private void addNewHaarAndSetListeners(boolean isRotated) {

        if (!isRotated) {
            HaarFeature newHaarFeature = new HaarFeature();

            (new DragResizeMod(newHaarFeature, imgWidth, imgHeight)).setNodeRotated(false).makeDraggableAndResizable();

            imageViewPane.getChildren().add(newHaarFeature);
            haarListView.getItems().add(newHaarFeature);

        } else {
            TiltedHaarPolygon tiltedHaarPolygon = new TiltedHaarPolygon();

            (new DragResizeMod(tiltedHaarPolygon, imgWidth, imgHeight)).setNodeRotated(true).makeDraggableAndResizable();

            imageViewPane.getChildren().add(tiltedHaarPolygon);
            haarListView.getItems().add(tiltedHaarPolygon);
        }

        haarListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            IHaar selectedItem = haarListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setCurrentHaarRectangleLabels(selectedItem);
            }
        });

        haarListView.refresh();
        haarListView.getSelectionModel().selectLast();
    }

    @FXML
    public void handleDeleteHaar(ActionEvent actionEvent) {
        IHaar selectedItem = haarListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            selectedItem.getColorProperty().unbind();
            selectedItem.getNameProperty().unbind();

            haarListView.getItems().remove(selectedItem);
            imageViewPane.getChildren().remove(selectedItem);

            haarListView.refresh();
        }
    }

    private void openImage(File file) {
        if (file == null) {
            return;
        }
        try {
            Image myImg = new Image(file.toURI().toString());

            imgWidth = myImg.getWidth();
            imgHeight = myImg.getHeight();

            imgStatusLabel.setText(String.format("w: %.0f x h: %.0f", imgWidth, imgHeight));

            imageViewPane.setPrefSize(imgWidth, imgHeight);

            imageViewPane.setMinWidth(imgWidth);
            imageViewPane.setMaxWidth(imgWidth);

            imageViewPane.setMaxHeight(imgHeight);
            imageViewPane.setMinHeight(imgHeight);

            imageView.setFitWidth(imgWidth);
            imageView.setFitHeight(imgHeight);

            imageView.setImage(transformImage(myImg));
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    private Image transformImage(Image inputImage) throws Exception {

        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();

        Image grayScaleImage = convertToGrayScale(inputImage);
        byte[] grayScaleByteArray = GrayScaleConverter.getGrayScaleByteArray();

        imageCreator = new IntegralImage(grayScaleByteArray, imgWidth, imgHeight);

        return grayScaleImage;
    }

    private void setCurrentHaarRectangleLabels(IHaar haarFeature) {

        MyBounds bounds = haarFeature.getBoundsPoints();

        rectangleWidthHeightLabel.setText(
                String.format(" %4d  x  %4d", haarFeature.getIntegerWidth(), haarFeature.getIntegerHeight())
        );

        pointALabel.setText(bounds.a.toString());
        pointBLabel.setText(bounds.b.toString());
        pointCLabel.setText(bounds.c.toString());
        pointDLabel.setText(bounds.d.toString());

        int area = haarFeature.getArea();
        long sumOfArea = imageCreator.getSumOfArea(haarFeature);

        rectangleArealabel.setText(nf.format(area));
        sumOfPixelsLabel.setText(nf.format(sumOfArea));

        avgLabel.setText(String.format("%4.1f", (double) sumOfArea / area));

        fromInitialPointDistanceLabel.setText(getDistanceFromInitialPoint(haarFeature).toString());
    }

    private MyPoint getDistanceFromInitialPoint(IHaar haar) {
        return new MyPoint(haar.getIntegerX() - initialPoint.getLayoutX() - 5, haar.getIntegerY() - initialPoint
                .getLayoutY() - 5);
    }

    private <T extends Event> void setInitialPointLabel() {

        MyPoint centerOfInitialPoint = new MyPoint(
                initialPoint.getLayoutX() + (initialPoint.getWidth() / 2),
                initialPoint.getLayoutY() + (initialPoint.getHeight() / 2)
        );

        initialPointLabel.setText((centerOfInitialPoint.toString()));
    }


    private static class HaarListViewCell extends ListCell<IHaar> {

        HBox hbox;
        TextField nameField;
        ColorPicker colorPicker;
        Label isRotatedLabel;

        HaarListViewCell() {
            super();
        }

        @Override
        public void updateItem(IHaar item, boolean isEmpty) {
            super.updateItem(item, isEmpty);

            if (isEmpty || item == null) {

                this.setText("");
                this.setGraphic(null);

            } else {

                isRotatedLabel = new Label(" ");
                isRotatedLabel.prefWidth(50);
                isRotatedLabel.minWidth(50);
                isRotatedLabel.maxWidth(50);

                nameField = new TextField(item.getNameProperty().get());
                nameField.prefWidth(100);
                nameField.setMinWidth(100);
                nameField.setMaxWidth(100);

                hbox = new HBox();
                colorPicker = new ColorPicker(item.getColorProperty().getValue());
                hbox.getChildren().addAll(isRotatedLabel, colorPicker, nameField);

                item.getColorProperty().bind(colorPicker.valueProperty());

                item.getNameProperty().bind(nameField.textProperty());

                if (item.isRotated()) {
                    isRotatedLabel.setText("R");
                } else {
                    isRotatedLabel.setText(" ");
                }

                this.setGraphic(hbox);
            }
        }

        @Override
        public void commitEdit(IHaar newValue) {
            super.commitEdit(newValue);

        }
    }
}