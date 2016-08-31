package com.daron.haar.features;

import com.daron.utils.MyBounds;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TiltedHaarPolygon extends Polygon implements IHaar {

    private int x = 100;
    private int y = 100;
    private int width = 100;
    private int height = 60;

    private Property<Color> color;
    private StringProperty name;

    private MyBounds myBounds;

    public TiltedHaarPolygon() {
        myBounds = new MyBounds();
        this.name = new SimpleStringProperty("Rotated ");
        this.color = new SimpleObjectProperty<>(Color.RED.deriveColor(1, 1, 1, 0.5d));

        this.setFill(color.getValue().deriveColor(1, 1, 1, 0.7));
        this.color.addListener((observable, oldValue, newValue) -> {
            this.setFill(newValue.deriveColor(1, 1, 1, 0.7));
        });

        setNewSizeAndPosition(x, y, width, height);
    }

    @Override
    public int getArea() {
        return 2 * this.width * this.height;
    }

    @Override
    public MyBounds getBoundsPoints() {
        setMyBounds();

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
        return width;
    }

    @Override
    public int getIntegerHeight() {
        return height;
    }

    @Override
    public boolean isRotated() {
        return true;
    }

    @Override
    public void setNewSizeAndPosition(int newX, int newY, int newWidth, int newHeight) {

        if (newX != -1) {
            this.x = newX;
        }

        if (newY != -1) {
            this.y = newY;
        }

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

        myBounds.b.setX(x + width - 1);
        myBounds.b.setY(y + width - 1);

        myBounds.c.setX(x + width - height - 1);
        myBounds.c.setY(y + width + height - 1);

        myBounds.d.setX(x - height - 1);
        myBounds.d.setY(y + height - 1);
    }

    private void updateABCDPoints() {
        this.getPoints().clear();
        this.getPoints().addAll(
                ((double) myBounds.a.getX()), ((double) myBounds.a.getY()),
                ((double) myBounds.b.getX()), ((double) myBounds.b.getY()),
                ((double) myBounds.c.getX()), ((double) myBounds.c.getY()),
                ((double) myBounds.d.getX()), ((double) myBounds.d.getY())
        );
    }

    public void rotate45() {
        // width <--> height
        setNewSizeAndPosition(x, y, height, width);
    }

}