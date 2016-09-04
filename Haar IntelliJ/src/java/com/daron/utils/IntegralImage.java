package com.daron.utils;

import java.time.Duration;
import java.time.Instant;

public class IntegralImage extends IntegralCreatorAbstract {

    public final long[][] integralSumTilted;
    private final long[][] integralSum;
    private final long[][] integralSumPow2;


    public IntegralImage(byte[] inputPixelsArray, int width, int height) {
        super(inputPixelsArray, width, height);

        integralSum = new long[height + 1][width + 1];
        integralSumPow2 = new long[height + 1][width + 1];
        integralSumTilted = new long[height + 1][width + 1];
        Instant start = Instant.now();

        calculate();

        Instant end = Instant.now();
        System.out.println("MyIntegralImage Calculate method: " + Duration.between(start, end).toMillis() + "ms");
    }

    @Override
    void calculate() {

        int iYIndex = 0;
        int iXIndex = 0;

        for (int y = 1; y <= imgHeight; y++) {

            long sum = 0;
            long sum2 = 0;

            integralSumTilted[y][0] = integralSumTilted[y - 1][1];

            for (int x = 1; x <= imgWidth; x++) {

                int pixelIndex = (y - 1) * (imgWidth) + x - 1;

                long currentVal = Byte.toUnsignedLong(inputPixelsArray[pixelIndex]);
                sum += currentVal;
                sum2 += Math.pow(currentVal, 2);

                integralSum[y][x] = integralSum[y - 1][x] + sum;
                integralSumPow2[y][x] = integralSumPow2[y - 1][x] + sum2;

                long rsatVal1 = getTiltedValFrom(x - 1, y - 1);
                long rsatVal2 = getTiltedValFrom(x + 1, y - 1);
                long rsatVal3 = getTiltedValFrom(x, y - 2);
                long valFromByteArray1 = getValFromByteArray(iXIndex % imgWidth, iYIndex);
                long valFromByteArray2 = getValFromByteArray(iXIndex % imgWidth, iYIndex - 1);

                long l = rsatVal1
                        + rsatVal2
                        - rsatVal3
                        + valFromByteArray1
                        + valFromByteArray2;

                integralSumTilted[y][x] = l;

                if (y > 2 && x == imgWidth) {
                    long ll = rsatVal1
                            + getTiltedValFrom(imgWidth, y - 2) //Diffrence!
                            - rsatVal3
                            + valFromByteArray1
                            + valFromByteArray2;

                    integralSumTilted[y][imgWidth] = ll;
                }
                iXIndex++;
            }
            iYIndex++;
        }
    }

    private long getValFromByteArray(int x, int y) {

        if (x < 0 || y < 0) {
            return 0L;
        }

        if (x > imgWidth - 1 || y > imgHeight - 1) {
//            System.err.println("Error: getValFromByteArray x or y more than: x=" + x + " y=" + y);
            return 0L;
        }

        return Byte.toUnsignedLong(inputPixelsArray[(y) * imgWidth + x]);
    }

    @Override
    long getNormalValFrom(int x, int y) {
        return integralSum[y][x];
    }

    @Override
    long getTiltedValFrom(int x, int y) {
        if (x == -1 || y == -1 || y == -2) {
            return 0L;
        }

        if (x > imgWidth) {
//            System.err.println("x > imgWidth : x= " + x);
            return 0L;
        }

        if (y > imgHeight) {
//            System.err.println("y > imgHeight : y= " + y);
            return 0L;
        }

        return integralSumTilted[y][x];
    }

    @Override
    long getPow2tValFrom(int x, int y) {
        return integralSumPow2[y][x];
    }


}
