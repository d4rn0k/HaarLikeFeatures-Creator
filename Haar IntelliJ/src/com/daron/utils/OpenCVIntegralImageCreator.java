package com.daron.utils;

import com.daron.haar.features.IHaar;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVIntegralImageCreator {

    private final Mat sumTable;
    private final Mat sumSquaredTable;
    private final Mat sumRotatedTable;
    public int[][] tiltedMatrixTest;
    private byte[] inputPixelsArray;
    private int imgWidth;
    private int imgHeight;

    public OpenCVIntegralImageCreator(byte[] inputPixelsArray, int width, int height) {
        this.imgWidth = width;
        this.imgHeight = height;
        this.inputPixelsArray = inputPixelsArray;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        sumTable = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_32F);
        sumSquaredTable = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_64F);
        sumRotatedTable = new Mat(imgHeight + 1, imgWidth + 1, CvType.CV_32F);
        calculate();

        tiltedMatrixTest = helper(sumRotatedTable);
    }

    private void calculate() {
        Mat inputImgMat = new Mat(imgHeight, imgWidth, CvType.CV_8U);
        inputImgMat.put(0, 0, inputPixelsArray);
        Imgproc.integral3(inputImgMat, sumTable, sumSquaredTable, sumRotatedTable);
    }

    public long getSumOfArea(IHaar haar) {
        if (haar.isRotated()) {
            return getSquareSumOfArea(haar);
        } else {
            return getNormalSumOfArea(haar);
        }
    }

    private int[][] helper(Mat inputMatrix) {
        int[][] output = new int[imgHeight + 1][imgWidth + 1];

        for (int i = 0; i < imgHeight + 1; i++) {
            for (int j = 0; j < imgWidth + 1; j++) {
                output[i][j] = (int) (inputMatrix.get(i, j))[0];
            }
        }
        return output;
    }

    private long getNormalSumOfArea(IHaar haar) {

        MyBounds bounds = haar.getBoundsPoints();

        int NWX = bounds.a.getX();
        int NWY = bounds.a.getY();
        int SEX = bounds.d.getX();
        int SEY = bounds.d.getY();

        return getSumOfArea(NWX, NWY, SEX, SEY);
    }

    private long getSumOfArea(int NWX, int NWY, int SEX, int SEY) {
        long sumArea = 0;

        try {

            if (!isInBounds(NWX, NWY, SEX, SEY)) {
                System.err.println("Over bounds! nwX=" + NWX + " nwY=" + NWY + " seX= " + SEX + " seY" + SEY);
                return 0;
            }

            sumArea = (long) (sumTable.get(SEY, SEX))[0] +
                    (long) (sumTable.get(NWY, NWX))[0] -
                    (long) (sumTable.get(NWY, SEX))[0] -
                    (long) (sumTable.get(SEY, NWX))[0];


        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return sumArea;
    }

    public long getSquareSumOfArea(IHaar haarFeature) {
        long sumArea = 0;

        try {

            int w = haarFeature.getIntegerWidth();
            int h = haarFeature.getIntegerHeight();

            int x = haarFeature.getIntegerX();
            int y = haarFeature.getIntegerY();

            sumArea = getSquaredValAt(x + w - h, y + w + h)
                    + getSquaredValAt(x, y)
                    - getSquaredValAt(x - h, y + h)
                    - getSquaredValAt(x + w, y + w);

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return sumArea;
    }

    private long getSquaredValAt(int x, int y) {
        if (x <= -1 || y <= -1) {
            return 0;
        }
        long answer = 0;

        try {
            answer = (long) (sumRotatedTable.get(y, x)[0]);
            System.out.println("OpenCVCreator:" + answer);
        } catch (Exception exc) {
            System.err.println("Over bounds! x=" + x + " y=" + y);
        }

        return answer;
    }

    private boolean isInBounds(int NWX, int NWY, int SEX, int SEY) {
        return NWX >= 0 && NWY >= 0 && SEX >= 0 && SEY >= 0 &&
                NWX <= imgWidth && SEX <= imgWidth &&
                NWY <= imgHeight && SEY <= imgHeight;
    }
}