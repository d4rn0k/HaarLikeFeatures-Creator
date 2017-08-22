package com.daron.utils;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class IntegralImageCreatorTest {

    private IntegralImageCreator integralImageCreator;

    @Before
    public void before() throws Exception {
        int width = 10;
        int height = 4;
        byte[] inputArray = generateInputArray(width, height);
        integralImageCreator = new IntegralImageCreator(inputArray, width, height);
    }

    @Test
    public void getSummedValueForPoint_ForLastIndex_ShouldReturnSumForAllElements() {
        long val = integralImageCreator.getSummedValueForPoint(10, 4);

        assertEquals(val, 180);
    }

    @Test
    public void getTiltedSummedValueForPoint_ForGivenIndex_ShouldReturnTiltedPixelsSumForThatElements()
            throws Exception {
        long val = integralImageCreator.getTiltedSummedValueForPoint(4, 4);

        assertEquals(val, 48);
    }

    @Test
    public void getPower2SummedValueForPoint_ForGivenIndex_ShouldReturnSumOfPowers2ForThatElements() throws Exception {
        long val = integralImageCreator.getPower2SummedValueForPoint(4, 4);

        assertEquals(val, 56);
    }


    private byte[] generateInputArray(int width, int height) {
        byte[] output = new byte[(height + 1) * (width + 1)];

        for (int y = 0; y < height + 1; y++) {
            for (int x = 0; x < width + 1; x++) {
                output[y * width + x] = (byte) x;
            }
        }
        return output;
    }
}