package com.daron.utils;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import java.nio.ByteBuffer;

public class GrayScaleConverter {

    // RGB weight for RGB -> 8bit grayscale conversion
    // Scales from ImagePlus web page weighted conversion
    // Could be: 1/3, 1/3, 1/3
    private static final double rw = 0.299;
    private static final double gw = 0.587;
    private static final double bw = 0.114;

    private static byte[] grayScaleByteArray;

    public static Image convertToGrayScale(Image inputImage) {

        final int imgWidth = (int) inputImage.getWidth();
        final int imgHeight = (int) inputImage.getHeight();
        final int channels = 4;

        WritableImage outputImage = new WritableImage(imgWidth, imgHeight);
        WritablePixelFormat<ByteBuffer> byteBufferWritablePixelFormat = WritablePixelFormat.getByteBgraInstance();

        ByteBuffer readBuffer = ByteBuffer.allocate(channels * imgWidth);
        ByteBuffer writeBuffer = ByteBuffer.allocate(imgWidth * imgHeight);

        for (int rowIndex = 0; rowIndex < imgHeight; rowIndex++) {
            inputImage.getPixelReader().getPixels(0, rowIndex, imgWidth, 1, byteBufferWritablePixelFormat,
                    readBuffer, 0);

            for (int pixelIndex = 0; pixelIndex < imgWidth; pixelIndex++) {

                int i = pixelIndex * channels;
                byte[] array = readBuffer.array();

                byte blue = (array[i]);
                byte green = (array[i + 1]);
                byte red = (array[i + 2]);

                byte gray = (byte) ((red * rw + green * gw + blue * bw - 0.5));

                double doubleGray = (rw * Byte.toUnsignedInt(red) + gw * Byte.toUnsignedInt(green) + bw *
                        Byte.toUnsignedInt(blue));

                writeBuffer.put(gray);

                outputImage
                        .getPixelWriter()
                        .setColor(
                                pixelIndex,
                                rowIndex,
                                new Color(doubleGray / 256, doubleGray / 256, doubleGray / 256, 1)
                        );

            }
            readBuffer.clear();
        }

        grayScaleByteArray = writeBuffer.array();

        return outputImage;
    }

    public static byte[] getGrayScaleByteArray(Image inputImage) {
        if (grayScaleByteArray == null) {
            convertToGrayScale(inputImage);
        }

        return grayScaleByteArray;
    }
}
