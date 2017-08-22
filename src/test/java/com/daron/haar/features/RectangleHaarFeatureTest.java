package com.daron.haar.features;

import com.daron.utils.RectangleBound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RectangleHaarFeatureTest {

    private RectangleHaarFeature rectangleHaarFeature;

    @Before
    public void before() throws Exception {
        rectangleHaarFeature = new RectangleHaarFeature();
    }

    @Test
    public void getArea_SetValidNewSize_ShouldReturnArea() throws Exception {
        rectangleHaarFeature.setNewSize(10, 5);

        assertEquals(rectangleHaarFeature.getArea(), 50);
    }

    @Test
    public void setNewPosition_SetNewPosition_ShouldReturnAxAndAyPoints() throws Exception {
        rectangleHaarFeature.setNewPosition(5, 10);

        assertEquals(rectangleHaarFeature.getStartPointX(), 5);
        assertEquals(rectangleHaarFeature.getStartPointY(), 10);
    }

    @Test
    public void setNewSize_SetValidNewSize_ShouldReturnAxAndAyPoints() throws Exception {
        rectangleHaarFeature.setNewSize(100, 100);

        assertEquals(rectangleHaarFeature.getWidthInteger(), 100);
        assertEquals(rectangleHaarFeature.getWidthInteger(), 100);
    }

    @Test
    public void getBoundsPoints_SetValidNewPositionAndSetValidNewSize_ShouldReturnValidBounds() throws Exception {

        int newX = 0, newY = 5, newWidth = 10, newHeight = 15;

        rectangleHaarFeature.setNewPosition(newX, newY);
        rectangleHaarFeature.setNewSize(newWidth, newHeight);

        RectangleBound boundsPoints = rectangleHaarFeature.getBoundsPoints();

        assertEquals(boundsPoints.a.getX(), newX);
        assertEquals(boundsPoints.a.getY(), newY);

        assertEquals(boundsPoints.b.getX(), newX + newWidth);
        assertEquals(boundsPoints.b.getY(), newY);

        assertEquals(boundsPoints.c.getX(), newX + newWidth);
        assertEquals(boundsPoints.c.getY(), newY + newHeight);

        assertEquals(boundsPoints.d.getX(), newX);
        assertEquals(boundsPoints.d.getY(), newY + newHeight);

    }
}