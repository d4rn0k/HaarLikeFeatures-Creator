package com.daron;

import com.daron.haar.features.*;
import com.daron.utils.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    private IntegralCreatorAbstract integralCreator;
    private double imgWidth;
    private double imgHeight;
    private Canvas initialPoint;
    private final NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());

    @FXML
    public Pane imageViewPane;

    @FXML
    private ListView<HaarFeature> haarListView;
    @FXML
    private ImageView imageView;

    @FXML
    private Label mouseMonitorLabel;
    @FXML
    private Label imgStatusLabel;
    @FXML
    private Label pointALabel, pointBLabel, pointCLabel, pointDLabel;
    @FXML
    private Label rectangleAreaLabel;
    @FXML
    private Label sumOfPixelsLabel;
    @FXML
    private Label rectangleWidthHeightLabel;
    @FXML
    private Label initialPointCordsLabel;
    @FXML
    private Label fromInitialPointDistanceLabel;
    @FXML
    private Label avgOfPixelsLabel;

    private DoubleProperty imageMargin = new SimpleDoubleProperty(5);

    @FXML
    private void initialize() {
        openImage(new File("example-input-image.jpg"));

        haarListView.setCellFactory(lv -> new HaarListViewCell());
        imageViewPane.addEventFilter(MouseEvent.ANY, event -> {
            HaarFeature selectedItem = haarListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setCurrentHaarRectangleLabels(selectedItem);
            }

            mouseMonitorLabel.setText(String.format("(x: % 4d , y: % 4d) ",
                    Math.round(event.getX()),
                    Math.round(event.getY())
            ));
        });

        imageViewPane.setTranslateX(imageMargin.doubleValue());
        imageViewPane.setTranslateY(imageMargin.doubleValue());

        addInitialPoint();
        handleAddNewHaar(null);
    }

    @FXML
    private void handleAddNewHaar(ActionEvent actionEvent) {
        setHaarFeatureListeners(new RectangleHaarFeature());
    }

    @FXML
    private void handleAddNewRotatedHaar(ActionEvent actionEvent) {
        setHaarFeatureListeners(new TiltedRectangleHaarFeature());
    }

    @FXML
    private void handleDeleteHaar(ActionEvent actionEvent) {
        HaarFeature selectedItem = haarListView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            selectedItem.getColorProperty().unbind();
            selectedItem.getNameProperty().unbind();

            haarListView.getItems().remove(selectedItem);
            imageViewPane.getChildren().remove(selectedItem);

            haarListView.refresh();
        }
    }

    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilterJPG =
                new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG =
                new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        haarListView.getItems().clear();
        imageViewPane.getChildren().retainAll(imageView);

        openImage(fileChooser.showOpenDialog(null));

        addInitialPoint();
    }

    @FXML
    private void handleSaveHaarFeaturesAsFile(ActionEvent actionEvent) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(XMLHaarFeature.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        List<XMLHaar> xmlList = new ArrayList<>();

        for (HaarFeature singleHaar : haarListView.getItems()) {

            Point distanceFromInitialPoint = getDistanceFromInitialPoint(singleHaar);

            xmlList.add(new XMLHaar(
                    distanceFromInitialPoint.getX(),
                    distanceFromInitialPoint.getY(),
                    singleHaar.getWidthInteger(),
                    singleHaar.getHeightInteger(),
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
        fc.setTitle("Save Haar-like features to .xml File");

        File file = fc.showSaveDialog(null);
        if (file == null) {
            return;
        }

        marshaller.marshal(xmlHaarFeature, file);

        if (file.createNewFile()) {
            System.out.println("Created new file");
        }
    }


    private void addInitialPoint() {
        setInitialPointGraphicsContext();
        setInitialPointLabel();
        setInitialPointListeners();
        imageViewPane.getChildren().add(initialPoint);
    }

    private void setInitialPointGraphicsContext() {
        initialPoint = new Canvas(10, 10);
        initialPoint.setLayoutX(10);
        initialPoint.setLayoutY(10);

        GraphicsContext gc = initialPoint.getGraphicsContext2D();

        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.7));
        gc.fillRect(0, 0, 10, 10);

        gc.setLineWidth(1.0);

        gc.moveTo(0, 5);
        gc.lineTo(10, 5);
        gc.stroke();

        gc.moveTo(5, 0);
        gc.lineTo(5, 10);
        gc.stroke();
    }

    private void setInitialPointLabel() {

        Point centerOfInitialPoint = new Point(
                initialPoint.getLayoutX() + (initialPoint.getWidth() / 2),
                initialPoint.getLayoutY() + (initialPoint.getHeight() / 2)
        );

        initialPointCordsLabel.setText(centerOfInitialPoint.toString());
    }

    private void setInitialPointListeners() {

        DragResizeUtil initialPointResizer = new DragResizeUtil(initialPoint, (int) imgWidth, (int) imgHeight);

        initialPointResizer.setMarginsForDrag((int) (initialPoint.getWidth() / 2),
                (int) (initialPoint.getHeight() / 2));
        initialPointResizer.makeOnlyDraggable();

        initialPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, (t) -> setInitialPointLabel());
    }

    private void setHaarFeatureListeners(HaarFeature newFeature) {

        ((Node) newFeature).addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
                setListViewSelection((HaarFeature) event.getSource()));

        DragResizeUtil dragResizeUtil = new DragResizeUtil((Node) newFeature, (int) imgWidth, (int) imgHeight);

        if (newFeature instanceof RectangleHaarFeature) {
            dragResizeUtil.setNodeRotated(false);
        } else if (newFeature instanceof TiltedRectangleHaarFeature) {
            dragResizeUtil.setNodeRotated(true);
        }

        dragResizeUtil.makeDraggableAndResizable();

        imageViewPane.getChildren().add((Node) newFeature);
        haarListView.getItems().add(newFeature);

        haarListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            HaarFeature selectedItem = haarListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setCurrentHaarRectangleLabels(selectedItem);
            }
        });

        haarListView.refresh();
        haarListView.getSelectionModel().selectLast();
    }

    private void setCurrentHaarRectangleLabels(HaarFeature haarFeature) {

        RectangleBound bounds = haarFeature.getBoundsPoints();

        rectangleWidthHeightLabel.setText(
                String.format(" %4d  x  %4d", haarFeature.getWidthInteger(), haarFeature.getHeightInteger())
        );

        pointALabel.setText(bounds.a.toString());
        pointBLabel.setText(bounds.b.toString());
        pointCLabel.setText(bounds.c.toString());
        pointDLabel.setText(bounds.d.toString());

        int area = haarFeature.getArea();
        long sumOfArea = integralCreator.getSumOfArea(haarFeature);

        rectangleAreaLabel.setText(nf.format(area));
        sumOfPixelsLabel.setText(nf.format(sumOfArea));

        avgOfPixelsLabel.setText(String.format("%4.1f", (double) sumOfArea / area));

        fromInitialPointDistanceLabel.setText(getDistanceFromInitialPoint(haarFeature).toString());
    }

    private void setListViewSelection(HaarFeature selection) {
        haarListView.getSelectionModel().select(selection);
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
            System.err.println("Cannot open img!");
        }
    }

    private Image transformImage(Image inputImage) throws Exception {

        int imgWidth = (int) inputImage.getWidth();
        int imgHeight = (int) inputImage.getHeight();

        Image grayScaleImage = convertToGrayScale(inputImage);
        byte[] grayScaleByteArray = GrayScaleConverter.getGrayScaleByteArray(inputImage);

        integralCreator = new IntegralImageCreator(grayScaleByteArray, imgWidth, imgHeight);

        return grayScaleImage;
    }

    private Point getDistanceFromInitialPoint(HaarFeature haar) {
        return new Point(haar.getStartPointX() - initialPoint.getLayoutX() - 5, haar.getStartPointY() - initialPoint
                .getLayoutY() - 5);
    }

    private static class HaarListViewCell extends ListCell<HaarFeature> {

        private HBox hbox;
        private TextField nameField;
        private ColorPicker colorPicker;
        private Label isRotatedLabel;

        HaarListViewCell() {
            super();
        }

        @Override
        public void updateItem(HaarFeature item, boolean isEmpty) {
            super.updateItem(item, isEmpty);

            if (isEmpty || item == null) {

                this.setText("");
                this.setGraphic(null);

            } else {
                hbox = new HBox();

                colorPicker = new ColorPicker(item.getColorProperty().getValue());
                isRotatedLabel = new Label(" ");
                nameField = new TextField(item.getNameProperty().get());

                isRotatedLabel.prefWidth(50);
                isRotatedLabel.minWidth(50);
                isRotatedLabel.maxWidth(50);

                nameField.prefWidth(95);
                nameField.setMinWidth(95);
                nameField.setMaxWidth(95);

                item.getColorProperty().bind(colorPicker.valueProperty());
                item.getNameProperty().bind(nameField.textProperty());

                if (item.isRotated()) {
                    isRotatedLabel.setText("R");
                } else {
                    isRotatedLabel.setText(" ");
                }

                hbox.getChildren().addAll(isRotatedLabel, colorPicker, nameField);

                this.setGraphic(hbox);
            }
        }

        @Override
        public void commitEdit(HaarFeature newValue) {
            super.commitEdit(newValue);
        }
    }
}