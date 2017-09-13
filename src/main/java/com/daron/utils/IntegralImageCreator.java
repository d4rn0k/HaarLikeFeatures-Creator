package com.daron.utils;

public class IntegralImageCreator extends IntegralCreatorAbstract {

    private final long[][] integralSum;
    private final long[][] integralSumTilted;
    private final long[][] integralSumPow2;

    public IntegralImageCreator(byte[] inputPixelsArray, int width, int height) {
        super(inputPixelsArray, width, height);

        integralSum = new long[height + 1][width + 1];
        integralSumPow2 = new long[height + 1][width + 1];
        integralSumTilted = new long[height + 1][width + 1];

        calculate();
    }

    @Override
    public void calculate() {

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

                long rSatVal1 = getTiltedSummedValueForPoint(x - 1, y - 1);
                long rSatVal2 = getTiltedSummedValueForPoint(x + 1, y - 1);
                long rSatVal3 = getTiltedSummedValueForPoint(x, y - 2);
                long valFromByteArray1 = getValFromByteArray(iXIndex % imgWidth, iYIndex);
                long valFromByteArray2 = getValFromByteArray(iXIndex % imgWidth, iYIndex - 1);

                long l = rSatVal1
                        + rSatVal2
                        - rSatVal3
                        + valFromByteArray1
                        + valFromByteArray2;

                integralSumTilted[y][x] = l;

                if (y > 2 && x == imgWidth) {
                    integralSumTilted[y][imgWidth] = rSatVal1
                            + getTiltedSummedValueForPoint(imgWidth, y - 2)
                            - rSatVal3
                            + valFromByteArray1
                            + valFromByteArray2;
                }
                iXIndex++;
            }
            iYIndex++;
        }
    }

    @Override
    public long getSummedValueForPoint(int x, int y) {
        return integralSum[y][x];
    }

    @Override
    public long getTiltedSummedValueForPoint(int x, int y) {
        if (x == -1 || y == -1 || y == -2) {
            return 0L;
        }

        if (x > imgWidth) {
            return 0L;
        }

        if (y > imgHeight) {
            return 0L;
        }

        return integralSumTilted[y][x];
    }

    @Override
    public long getPower2SummedValueForPoint(int x, int y) {
        return integralSumPow2[y][x];
    }

    private long getValFromByteArray(int x, int y) {

        if (x < 0 || y < 0) {
            return 0L;
        }

        if (x > imgWidth - 1 || y > imgHeight - 1) {
            return 0L;
        }

        return Byte.toUnsignedLong(inputPixelsArray[(y) * imgWidth + x]);
    }

}
