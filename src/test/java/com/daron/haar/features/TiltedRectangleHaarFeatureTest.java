package com.daron.haar.features;

import com.daron.utils.RectangleBound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TiltedRectangleHaarFeatureTest {

    private TiltedRectangleHaarFeature tiltedRectangleHaarFeature;

    @Before
    public void before() throws Exception {
        tiltedRectangleHaarFeature = new TiltedRectangleHaarFeature();
    }

    @Test
    public void getArea_SetValidNewSize_ShouldReturnArea() throws Exception {
        tiltedRectangleHaarFeature.setNewSize(10, 5);

        assertEquals(tiltedRectangleHaarFeature.getArea(), 50 * 2);
    }

    @Test
    public void setNewPosition_SetValidNewPosition_ShouldReturnValidAxAndAyPoints() throws Exception {
        tiltedRectangleHaarFeature.setNewPosition(10, 20);

        assertEquals(tiltedRectangleHaarFeature.getStartPointX(), 10);
        assertEquals(tiltedRectangleHaarFeature.getStartPointY(), 20);
    }

    @Test
    public void setNewSize_SetValidNewSize_ShouldReturnAxAndAyPoints() throws Exception {
        tiltedRectangleHaarFeature.setNewSize(100, 500);

        assertEquals(tiltedRectangleHaarFeature.getWidthInteger(), 100);
        assertEquals(tiltedRectangleHaarFeature.getHeightInteger(), 500);
    }

    @Test
    public void getBoundsPoints_SetValidNewXNewYAndSize_ShouldReturnValidBoundPoints() throws Exception {
        int newX = 30, newY = 42, newWidth = 4, newHeight = 6;

        tiltedRectangleHaarFeature.setNewPosition(newX, newY);
        tiltedRectangleHaarFeature.setNewSize(newWidth, newHeight);

        RectangleBound boundsPoints = tiltedRectangleHaarFeature.getBoundsPoints();

        assertEquals(boundsPoints.a.getX(), newX);
        assertEquals(boundsPoints.a.getY(), newY);

        assertEquals(boundsPoints.b.getX(), newX + newWidth);
        assertEquals(boundsPoints.b.getY(), newY + newWidth);

        assertEquals(boundsPoints.c.getX(), boundsPoints.b.getX() - newHeight);
        assertEquals(boundsPoints.c.getY(), newY + newHeight + newWidth);

        assertEquals(boundsPoints.d.getX(), newX - newHeight);
        assertEquals(boundsPoints.d.getY(), newY + newHeight);
    }

}