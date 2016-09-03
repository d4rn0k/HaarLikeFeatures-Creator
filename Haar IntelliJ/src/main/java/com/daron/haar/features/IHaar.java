package com.daron.haar.features;

import com.daron.utils.MyBounds;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public interface IHaar {

    void setNewSizeAndPosition(int newX, int newY, int newWidth, int newHeight);

    MyBounds getBoundsPoints();

    int getArea();

    int getIntegerX();

    int getIntegerY();

    int getIntegerWidth();

    int getIntegerHeight();

    boolean isRotated();

    Property<Color> getColorProperty();

    StringProperty getNameProperty();
}
