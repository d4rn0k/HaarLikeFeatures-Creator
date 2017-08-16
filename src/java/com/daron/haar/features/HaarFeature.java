package com.daron.haar.features;

import com.daron.utils.MyBounds;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HaarFeature extends Rectangle implements IHaar {

    private final Property<Color> color;
    private final StringProperty name;
    private final MyBounds myBounds;
    private int x;
    private int y;

    public HaarFeature() {
        super(100, 50);
        myBounds = new MyBounds();
        this.name = new SimpleStringProperty("Rectangle ");
        this.color = new SimpleObjectProperty<>(Color.RED.deriveColor(1, 1, 1, 0.5d));

        this.setFill(color.getValue().deriveColor(1, 1, 1, 0.7));
        this.color.addListener((observable, oldValue, newValue) ->
                this.setFill(newValue.deriveColor(1, 1, 1, 0.7)));

        setNewSizeAndPosition(100, 100, 100, 50);
    }

    @Override
    public void setNewSizeAndPosition(int newX, int newY, int newWidth, int newHeight) {
        x = newX;
        y = newY;

        this.setLayoutX(newX);
        this.setLayoutY(newY);

        this.setWidth(newWidth);
        this.setHeight(newHeight);

        setMyBounds();
    }

    @Override
    public int getArea() {
        return (int) (getHeight() * getWidth());
    }

    @Override
    public MyBounds getBoundsPoints() {
        return myBounds;
    }

    @Override
    public int getIntegerX() {
        return x;
    }

    @Override
    public int getIntegerY() {
        return y;
    }

    @Override
    public int getIntegerWidth() {
        return (int) getWidth();
    }

    @Override
    public int getIntegerHeight() {
        return (int) getHeight();
    }

    @Override
    public boolean isRotated() {
        return false;
    }

    @Override
    public Property<Color> getColorProperty() {
        return color;
    }

    @Override
    public StringProperty getNameProperty() {
        return name;
    }

    private void setMyBounds() {
        myBounds.a.setX(x);
        myBounds.a.setY(y);

        myBounds.b.setX((int) (x + getWidth()));
        myBounds.b.setY(y);

        myBounds.c.setX(x);
        myBounds.c.setY((int) (y + getHeight()));

        myBounds.d.setX((int) (x + getWidth()));
        myBounds.d.setY((int) (y + getHeight()));
    }
}