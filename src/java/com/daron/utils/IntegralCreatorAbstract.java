package com.daron.utils;

import com.daron.haar.features.HaarFeature;

public abstract class IntegralCreatorAbstract {

    final int imgWidth;
    final int imgHeight;

    final byte[] inputPixelsArray;

    IntegralCreatorAbstract(byte[] inputPixelsArray, int width, int height) {

        this.imgHeight = height;
        this.imgWidth = width;
        this.inputPixelsArray = inputPixelsArray;
    }

    abstract void calculate();

    abstract long getSummedValueForPoint(int x, int y);

    abstract long getTiltedSummedValueForPoint(int x, int y);

    abstract long getPower2SummedValueForPoint(int x, int y);

    public long getSumOfArea(HaarFeature haar) {
        if (haar.isRotated()) {
            return getTiltedSumOfArea(haar);
        } else {
            return getNormalSumOfArea(haar);
        }
    }

    private long getNormalSumOfArea(HaarFeature haar) {

        RectangleBound bounds = haar.getBoundsPoints();

        int nwX = bounds.a.getX();
        int nwY = bounds.a.getY();
        int seX = bounds.c.getX();
        int seY = bounds.c.getY();

        return getSumOfArea(nwX, nwY, seX, seY);
    }

    private long getSumOfArea(int nwX, int nwY, int seX, int seY) {
        long sumArea = 0;

        try {

            if (!isInBounds(nwX, nwY, seX, seY)) {
                System.err.println("Over bounds! nwX=" + nwX + " nwY=" + nwY + " seX= " + seX + " seY" + seY);
                return 0;
            }

            sumArea = getSummedValueForPoint(seX, seY) +
                    getSummedValueForPoint(nwX, nwY) -
                    getSummedValueForPoint(seX, nwY) -
                    getSummedValueForPoint(nwX, seY);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return sumArea;
    }

    private long getTiltedSumOfArea(HaarFeature haarFeature) {
        long sumArea = 0;

        try {

            int w = haarFeature.getWidthInteger();
            int h = haarFeature.getHeightInteger();

            int x = haarFeature.getStartPointX();
            int y = haarFeature.getStartPointY();

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
        long outputValue = 0;

        try {

            outputValue = getTiltedSummedValueForPoint(x, y);

        } catch (Exception exc) {
            System.err.println("Over bounds! x=" + x + " y=" + y);
        }

        return outputValue;
    }

    private boolean isInBounds(int nwX, int nwY, int seX, int seY) {
        return nwX >= 0 && nwY >= 0 && seX >= 0 && seY >= 0 &&
                nwX <= imgWidth && seX <= imgWidth &&
                nwY <= imgHeight && seY <= imgHeight;
    }
}
