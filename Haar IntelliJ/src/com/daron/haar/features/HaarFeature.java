package com.daron.haar.features;

import com.daron.utils.MyBounds;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HaarFeature extends Rectangle implements IHaar {

    int x;
    int y;

    private Property<Color> color;
    private StringProperty name;
    private MyBounds myBounds;

    public HaarFeature() {
        super(100, 50);
        myBounds = new MyBounds();
        this.name = new SimpleStringProperty("Nowa ");
        this.color = new SimpleObjectProperty<>(Color.RED.deriveColor(1, 1, 1, 0.5d));


        this.setFill(color.getValue().deriveColor(1, 1, 1, 0.7));
        this.color.addListener((observable, oldValue, newValue) -> {
            this.setFill(newValue.deriveColor(1, 1, 1, 0.7));
        });

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

//    public Point2D[] getBoundsPointDEPR() {
//
//        Point2D a, b, c, d;
//
//        Bounds bounds = getBoundsInParent();
//
//
//        if (isRotated.get()) {
//
//            double minRectangleX = Math.sqrt(Math.pow(getHeight(), 2) / 2);
//            double maxRectangleX = Math.sqrt(Math.pow(getWidth(), 2) / 2);
//
//            a = new Point2D(bounds.getMinX() + minRectangleX, bounds.getMinY());
//            b = new Point2D(bounds.getMaxX(), bounds.getMinY() + maxRectangleX);
//            c = new Point2D(bounds.getMaxX() - minRectangleX, bounds.getMinY() + maxRectangleX + minRectangleX);
//            d = new Point2D(bounds.getMinX(), bounds.getMinY() + minRectangleX);
//
//            //System.out.format("realWidth: %5.1f\nmyWidth: %5.1f\n", bounds.getWidth(), rectWidth);
//
//        } else {
//            a = new Point2D(bounds.getMinX(), bounds.getMinY());
//            b = new Point2D(bounds.getMaxX(), bounds.getMinY());
//            c = new Point2D(bounds.getMaxX(), bounds.getMaxY());
//            d = new Point2D(bounds.getMinX(), bounds.getMaxY());
//        }
//
//        return new Point2D[]{a, b, c, d};
//    }

//    public Bounds geValidBoundsDEPR() {
//        BoundingBox boundsToReturn;
//
//        Bounds rectBounds = getBoundsInParent();
////        if (isRotated.get()) {
////
////            boundsToReturn = new BoundingBox(
////                    rectBounds.getMinY(),
////                    rectBounds.getMinX(),
////                    getWidth(),
////                    getHeight()
////            );
////
////        } else {
////            boundsToReturn = new BoundingBox(
////                    rectBounds.getMinX(),
////                    rectBounds.getMinY(),
////                    getWidth(),
////                    getHeight()
////            );
////        }
//
//        return boundsToReturn;
//    }


    public Property<Color> getColor() {
        return color;
    }


    public StringProperty getName() {
        return name;
    }
}