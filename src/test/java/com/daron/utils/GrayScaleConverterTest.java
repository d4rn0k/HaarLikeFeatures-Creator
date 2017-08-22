package com.daron.utils;

import com.daron.Main;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import java.io.File;

public class GrayScaleConverterTest {

    @Before
    public void before() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }

    @Test
    public void convertToGrayScale_ExampleInputImage_ShouldAllPixelsAreGrayScale() throws Exception {
        Image inputImage = new Image(new File("example-input-image.jpg").toURI().toString());
        Image outputImage = GrayScaleConverter.convertToGrayScale(inputImage);

        int imageHeight = (int) outputImage.getHeight();
        int imageWidth = (int) outputImage.getWidth();

        int byteLineWidth = imageWidth * 4;

        byte[] buffer = new byte[imageHeight * byteLineWidth];

        for (int offset = 0; offset < imageHeight * imageWidth * 4; offset += byteLineWidth) {
            // Format: Blue Green Red Alpha
            outputImage.getPixelReader().getPixels(
                    0,
                    0,
                    imageWidth,
                    imageHeight,
                    PixelFormat.getByteBgraInstance(),
                    buffer,
                    0,
                    byteLineWidth
            );
            if (!checkIfLineIsRGB(buffer)) {
                System.err.println("Line: " + offset / byteLineWidth);
                throw new Exception("Not RGB!");
            }
        }

    }

    private boolean checkIfLineIsRGB(byte[] inputArray) {
        for (int i = 0; i < inputArray.length; i += 4) {
            byte red = inputArray[i];
            byte green = inputArray[i + 1];
            byte blue = inputArray[i + 2];

            if (red != green && green != blue) {
                return false;
            }
        }

        return true;
    }

}