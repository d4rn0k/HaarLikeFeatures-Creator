package com.daron.haar.features;

import com.daron.utils.RectangleBound;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TiltedRectangleHaarFeature extends Polygon implements HaarFeature {

    private final Property<Color> fillColor;
    private final StringProperty name;
    private final RectangleBound rectangleBound;
    private int x = 100;
    private int y = 100;
    private int width = 100;
    private int height = 60;

    public TiltedRectangleHaarFeature() {
        rectangleBound = new RectangleBound();
        this.name = new SimpleStringProperty("Rotated ");
        this.fillColor = new SimpleObjectProperty<>(Color.RED.deriveColor(1, 1, 1, 0.5d));
        this.setFill(fillColor.getValue().deriveColor(1, 1, 1, 0.7));

        this.fillColor.addListener((observable, oldValue, newValue) ->
                this.setFill(newValue.deriveColor(1, 1, 1, 0.7)));

        setNewPosition(x, y);
        setNewSize(width, height);
    }

    @Override
    public int getArea() {
        return 2 * this.width * this.height;
    }

    @Override
    public void setNewPosition(int newX, int newY) {
        if (newX != -1) {
            this.x = newX;
        }

        if (newY != -1) {
            this.y = newY;
        }

        setMyBounds();
        updateABCDPoints();
    }

    @Override
    public void setNewSize(int newWidth, int newHeight) {

        if (newWidth != -1) {
            this.width = newWidth;
        }

        if (newHeight != -1) {
            this.height = newHeight;
        }

        setMyBounds();
        updateABCDPoints();
    }

    @Override
    public RectangleBound getBoundsPoints() {
        setMyBounds();

        return rectangleBound;
    }

    @Override
    public int getStartPointX() {
        return x;
    }

    @Override
    public int getStartPointY() {
        return y;
    }

    @Override
    public int getWidthInteger() {
        return width;
    }

    @Override
    public int getHeightInteger() {
        return height;
    }

    @Override
    public boolean isRotated() {
        return true;
    }

    @Override
    public Property<Color> getColorProperty() {
        return fillColor;
    }

    @Override
    public StringProperty getNameProperty() {
        return name;
    }

    private void setMyBounds() {
        rectangleBound.a.setX(x);
        rectangleBound.a.setY(y);

        rectangleBound.b.setX(x + width);
        rectangleBound.b.setY(y + width);

        rectangleBound.c.setX(x + width - height);
        rectangleBound.c.setY(y + width + height);

        rectangleBound.d.setX(x - height);
        rectangleBound.d.setY(y + height);
    }

    private void updateABCDPoints() {
        this.getPoints().clear();
        this.getPoints().addAll(
                ((double) rectangleBound.a.getX()), ((double) rectangleBound.a.getY()),
                ((double) rectangleBound.b.getX()), ((double) rectangleBound.b.getY()),
                ((double) rectangleBound.c.getX()), ((double) rectangleBound.c.getY()),
                ((double) rectangleBound.d.getX()), ((double) rectangleBound.d.getY())
        );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void rotate45() {
        // To rotate change width with height
        setNewSize(height, width);
    }

}