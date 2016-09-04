package com.daron;

import com.daron.haar.features.TiltedHaarPolygon;
import com.daron.utils.OpenCVIntegralImageCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)

public class OpenCVIntegralImageCreatorTest {

    @Test
    public void getSquareSumOfArea() throws Exception {
        int n = 36;
        byte[] orderedArray = new byte[n];
        int size = (int) Math.sqrt(n);

        IntStream.rangeClosed(1, n).forEach(val -> orderedArray[val - 1] = (byte) val);

        OpenCVIntegralImageCreator underTest = new OpenCVIntegralImageCreator(orderedArray, size, size);
        TiltedHaarPolygon tiltedHaarPolygon = new TiltedHaarPolygon();
        tiltedHaarPolygon.setNewSizeAndPosition(2, 1, 2, 3);
        tiltedHaarPolygon.rotate45();

        assertEquals(216, underTest.getSumOfArea(tiltedHaarPolygon));

        System.out.println("End Test!");
    }

}