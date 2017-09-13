package com.daron.haar.features;

import com.daron.utils.RectangleBound;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleHaarFeature extends Rectangle implements HaarFeature {

    private final Property<Color> fillColor;
    private final StringProperty name;
    private final RectangleBound rectangleBound;
    private int x;
    private int y;

    public RectangleHaarFeature() {
        super(100, 50);
        rectangleBound = new RectangleBound();
        this.name = new SimpleStringProperty("Rectangle ");
        this.fillColor = new SimpleObjectProperty<>(Color.RED.deriveColor(1, 1, 1, 0.5d));

        this.setFill(fillColor.getValue().deriveColor(1, 1, 1, 0.7));
        this.fillColor.addListener((observable, oldValue, newValue) ->
                this.setFill(newValue.deriveColor(1, 1, 1, 0.7)));

        setNewPosition(100, 100);
        setNewSize(100, 50);
    }

    @Override
    public int getArea() {
        return (this.getHeightInteger() * this.getWidthInteger());
    }

    @Override
    public void setNewPosition(int newX, int newY) {
        x = newX;
        y = newY;
        this.setLayoutX(newX);
        this.setLayoutY(newY);

        setMyBounds();
    }

    @Override
    public void setNewSize(int newWidth, int newHeight) {
        this.setWidth(newWidth);
        this.setHeight(newHeight);

        setMyBounds();
    }

    @Override
    public RectangleBound getBoundsPoints() {
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
        return (int) getWidth();
    }

    @Override
    public int getHeightInteger() {
        return (int) getHeight();
    }

    @Override
    public boolean isRotated() {
        return false;
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

        rectangleBound.b.setX(x + getWidthInteger());
        rectangleBound.b.setY(y);

        rectangleBound.c.setX(x + getWidthInteger());
        rectangleBound.c.setY(y + getHeightInteger());

        rectangleBound.d.setX(x);
        rectangleBound.d.setY(y + getHeightInteger());
    }
}