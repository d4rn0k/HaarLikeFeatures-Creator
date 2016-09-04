package com.daron;

import com.daron.haar.features.IHaar;
import com.daron.haar.features.TiltedHaarPolygon;
import com.daron.utils.GrayScaleConverter;
import com.daron.utils.IntegralImage;
import com.daron.utils.OpenCVIntegralImageCreator;
import javafx.scene.image.Image;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.testfx.api.FxToolkit;

import java.io.File;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;

@RunWith(MockitoJUnitRunner.class)
public class IntegralTest {
    private int n;
    private byte[] byteTestArray;
    private int size;
    private long sum;

    private IntegralImage integralImage;
    private OpenCVIntegralImageCreator openCVIntegralImageCreator;

    @Before
    public void init() {
        n = 36;
        byteTestArray = new byte[n];

        sum = (long) (((1 + n) / 2.0) * n);

        IntStream.rangeClosed(1, n).forEach(val -> byteTestArray[val - 1] = (byte) val);

        size = (int) Math.sqrt(n);
    }

    @Before
    public void before() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }


    @Test
    public void testMyIntegralCreatorForTiltedHaar() throws Exception {
        Image myImg = new Image(new File("obrazek.jpg").toURI().toString());
        GrayScaleConverter.convertToGrayScale(myImg);
        byte[] byteArraya = GrayScaleConverter.getGrayScaleByteArray();

        int width = (int) myImg.getWidth();
        int height = (int) myImg.getHeight();

        openCVIntegralImageCreator = new OpenCVIntegralImageCreator(byteArraya, width, height);
        integralImage = new IntegralImage(byteArraya, width, height);

        IHaar tiltedHaar = new TiltedHaarPolygon();

        tiltedHaar.setNewSizeAndPosition(342, 222, 54, 40);

        long[][] openCVMatrix = openCVIntegralImageCreator.tiltedMatrixTest;
        long[][] myMatrix = integralImage.integralSumTilted;

        showDiffs(openCVMatrix, myMatrix);

        TestCase.assertEquals(openCVIntegralImageCreator.getSumOfArea(tiltedHaar), integralImage.getSumOfArea(tiltedHaar));
    }

    @Test
    public void simpleTest() {
        int width = 10;
        int height = 4;

        byte[] inputArray = generateInputArray(width, height);

        openCVIntegralImageCreator = new OpenCVIntegralImageCreator(inputArray, width, height);
        integralImage = new IntegralImage(inputArray, width, height);

        long[][] openCVMatrix = openCVIntegralImageCreator.tiltedMatrixTest;
        long[][] myMatrix = integralImage.integralSumTilted;

        showDiffs(openCVMatrix, myMatrix);

        assertArrayEquals(openCVMatrix, myMatrix);
    }

    private void showDiffs(long[][] openCVMatrix, long[][] myMatrix) {
        for (int y = 0; y < openCVMatrix.length; y++) {
            for (int x = 0; x < openCVMatrix[0].length; x++) {

                if (openCVMatrix[y][x] != myMatrix[y][x]) {
                    System.err.format("Diffrence in: y=[%d] x=[%d]  openCVVal = [%d] myVal = [%d]\n",
                            y, x, openCVMatrix[y][x], myMatrix[y][x]);
                }

            }
        }
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