package com.daron.haar.features;

import com.daron.utils.MyBounds;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class RotatedHaarPolygon extends Polygon implements IHaar {

    int x = 100;
    int y = 100;

    int width = 100;
    int height = 60;

    //true => //; false  = \\
    boolean rotateSide = false;
    MyBounds myBounds;
    private Property<Color> color;

    private StringProperty name;

    public RotatedHaarPolygon() {
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


        if (newX != -1 && newY != -1) {
            this.x = newX;
            this.y = newY;
        }


        if (newWidth != -1 && newHeight != -1) {
            this.width = newWidth;
            this.height = newHeight;
        }

//        a = new Point2D(newX, newY );
//        b = new Point2D(newX + newWidth, newY + newWidth);
//        c = new Point2D(newX + newWidth - newHeight, newY + newWidth + newHeight);
//        d = new Point2D(newX - newHeight, newY + newHeight );
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

        myBounds.b.setX(x + width);
        myBounds.b.setY(y + width);

        myBounds.c.setX(x + width - height);
        myBounds.c.setY(y + width + height);

        myBounds.d.setX(x - height);
        myBounds.d.setY(y + height);
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

    public void swapSide() {
        // width <--> height
        if (!rotateSide) {
            setNewSizeAndPosition(x, y, height, width);
            rotateSide = true;
        } else {
            setNewSizeAndPosition(x, y, width, height);
            rotateSide = false;
        }

    }

}