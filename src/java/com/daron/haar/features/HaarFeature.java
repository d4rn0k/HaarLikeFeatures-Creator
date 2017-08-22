package com.daron.haar.features;

import com.daron.utils.RectangleBound;

import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public interface HaarFeature {

    void setNewPosition(int newX, int newY);

    void setNewSize(int newWidth, int newHeight);

    RectangleBound getBoundsPoints();

    int getArea();

    int getStartPointX();

    int getStartPointY();

    int getWidthInteger();

    int getHeightInteger();

    boolean isRotated();

    Property<Color> getColorProperty();

    StringProperty getNameProperty();
}
