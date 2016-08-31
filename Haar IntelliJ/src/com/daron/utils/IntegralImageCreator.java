package com.daron.utils;

import com.daron.haar.features.IHaar;

public class IntegralImageCreator {

    int imgWidth;
    int imgHeight;

    private long integralSum[][];
    private long integralSumSquared[][];
    private long integralSumTilted[][];

    private byte[] inputPixelsArray;

    public IntegralImageCreator(byte[] inputPixelsArray, int width, int height) {
        this.inputPixelsArray = inputPixelsArray;

        this.imgWidth = width;
        this.imgHeight = height;

        integralSum = new long[height + 1][width + 1];
        integralSumSquared = new long[height + 1][width + 1];
        integralSumTilted = new long[height + 1][width + 1];
        calculate();
    }

    public void calculate() {

        for (int y = 1; y <= imgHeight; y++) {

            long sum = 0;
            long sum2 = 0;

            for (int x = 1; x <= imgWidth; x++) {

                int pixelIndex = (y - 1) * (imgWidth) + x - 1;

                long currentVal = Byte.toUnsignedLong(inputPixelsArray[pixelIndex]);
                sum += currentVal;
                sum2 += Math.pow(currentVal, 2);

                integralSum[y][x] = integralSum[y - 1][x] + sum;
                integralSumSquared[y][x] = integralSumSquared[y - 1][x] + sum2;


            }

        }

        int iYIndex = 0;
        int iXIndex = 0;

        for (int y = 1; y <= imgHeight; y++) {

            long sum = 0;
            long sum2 = 0;

            integralSumTilted[y][0] = integralSumTilted[y - 1][1];

            for (int x = 1; x <= imgWidth; x++) {


                long rsatVal1 = getRsatVal(x - 1, y - 1);
                long rsatVal2 = getRsatVal(x + 1, y - 1);
                long rsatVal3 = getRsatVal(x, y - 2);
                long valFromByteArray1 = getValFromByteArray(iXIndex % imgWidth, iYIndex);
                long valFromByteArray2 = getValFromByteArray(iXIndex % imgHeight, iYIndex - 1);


                long l = rsatVal1
                        + rsatVal2
                        - rsatVal3
                        + valFromByteArray1
                        + valFromByteArray2;

                integralSumTilted[y][x] = l;

                if (y > 2 && x == imgWidth) {
                    long ll = rsatVal1
                            + getRsatVal(imgWidth, y - 2) //tu Różnica!
                            - rsatVal3
                            + valFromByteArray1
                            + valFromByteArray2;

                    integralSumTilted[y][imgWidth] = ll;
                }

                iXIndex++;
            }

            iYIndex++;
        }
        System.out.println("Calculated!");
    }

    private long getValFromByteArray(int x, int y) {
        if (x < 0 || y < 0) {
            System.out.println("Błąd getValFromByteArray x lub y Mniejsze 0: x=" + x + " y=" + y);
            return 0L;
        }
        if (x > imgWidth - 1 || y > imgHeight - 1) {
            System.out.println("!!Błąd getValFromByteArray x lub y Większe 0: x=" + x + " y=" + y);
            return 0L;
        }
        return Byte.toUnsignedLong(inputPixelsArray[(y) * imgWidth + x]);

    }

    private long getRsatVal(int x, int y) {
        if (x == -1 || y == -1 || y == -2) {
            return 0L;
        }

        if (x > imgWidth) {
            System.out.println("x > imgWidth : x= " + x);
            return 0L;
        }

        if (x > imgHeight) {
            System.out.println("y > imgHeight : y= " + y);
            return 0L;
        }

        return integralSumTilted[y][x];
    }


    public long getSumOfArea(IHaar haar) {
        if (haar.isRotated()) {
            return getTiltedSumOfArea(haar);
        } else {
            return getNormalSumOfArea(haar);
        }
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

            sumArea = integralSum[SEY][SEX]
                    + integralSum[NWY][NWX]
                    - integralSum[NWY][SEX]
                    - integralSum[SEY][NWX];

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return sumArea;
    }

    private long getTiltedSumOfArea(IHaar haarFeature) {
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
            answer = integralSumTilted[y][x];
            System.out.println("MyCreator:" + answer);
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
