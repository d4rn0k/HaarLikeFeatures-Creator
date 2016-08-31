package com.daron.tests;

import com.daron.haar.features.HaarFeature;
import com.daron.haar.features.IHaar;
import com.daron.haar.features.TiltedHaarPolygon;
import com.daron.utils.IntegralImageCreator;
import com.daron.utils.OpenCVIntegralImageCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class IntegralImageCreatorTest {
    int n;
    byte[] byteTestArray;
    int size;
    long sum;
    //@InjectMocks
    private IntegralImageCreator underTest;

    @Before
    public void init() {
        n = 36;
        byteTestArray = new byte[n];

        sum = (long) (((1 + n) / 2.0) * n);

        IntStream.rangeClosed(1, n).forEach(val -> byteTestArray[val - 1] = (byte) val);

        size = (int) Math.sqrt(n);
    }

    @Test
    public void calculateTest() throws Exception {

        OpenCVIntegralImageCreator openCVIntegralImageCreator = new OpenCVIntegralImageCreator(byteTestArray, size, size);

        underTest = new IntegralImageCreator(byteTestArray, size, size);

        IHaar testHaarRect = new HaarFeature();
        testHaarRect.setNewSizeAndPosition(0, 0, size, size);

        assertEquals(sum, underTest.getSumOfArea(testHaarRect));

    }

    @Test
    public void testAllValues() {

        TiltedHaarPolygon testRect = new TiltedHaarPolygon();
        testRect.setNewSizeAndPosition(0, 0, 2, 2);

        OpenCVIntegralImageCreator openCV = new OpenCVIntegralImageCreator(byteTestArray, size, size);
        IntegralImageCreator myCreator = new IntegralImageCreator(byteTestArray, size, size);


        assertEquals(openCV.getSumOfArea(testRect), myCreator.getSumOfArea(testRect));
    }

}