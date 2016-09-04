package com.daron.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVIntegralImageCreator extends IntegralCreatorAbstract {

    public final long[][] tiltedMatrixTest;
    private final Mat sumTable;
    private final Mat sumPow2;
    private final Mat sumTilted;


    public OpenCVIntegralImageCreator(byte[] inputPixelsArray, int width, int height) {
        super(inputPixelsArray, width, height);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        sumPow2 = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_64F);
        sumTable = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_32F);
        sumTilted = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_32F);


        calculate();
        tiltedMatrixTest = helper(sumTilted);
    }


    @Override
    protected void calculate() {
        Mat inputImgMat = new Mat(imgHeight, imgWidth, CvType.CV_8U);
        inputImgMat.put(0, 0, inputPixelsArray);
        Imgproc.integral3(inputImgMat, sumTable, sumPow2, sumTilted);

    }

    @Override
    long getNormalValFrom(int x, int y) {
        return (long) (sumTable.get(y, x))[0];
    }

    @Override
    long getTiltedValFrom(int x, int y) {
        return (long) (sumTilted.get(y, x))[0];
    }

    @Override
    long getPow2tValFrom(int x, int y) {
        return (long) (sumPow2.get(y, x))[0];
    }

    private long[][] helper(Mat inputMatrix) {
        long[][] output = new long[imgHeight + 1][imgWidth + 1];

        for (int i = 0; i < imgHeight + 1; i++) {
            for (int j = 0; j < imgWidth + 1; j++) {
                output[i][j] = (int) (inputMatrix.get(i, j))[0];
            }
        }
        return output;
    }
}