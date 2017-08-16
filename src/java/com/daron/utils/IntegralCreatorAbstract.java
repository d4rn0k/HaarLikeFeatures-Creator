package com.daron.utils;

import com.daron.haar.features.IHaar;

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

    abstract long getNormalValFrom(int x, int y);

    abstract long getTiltedValFrom(int x, int y);

    abstract long getPow2tValFrom(int x, int y);

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

            sumArea = getNormalValFrom(SEX, SEY) +
                    getNormalValFrom(NWX, NWY) -
                    getNormalValFrom(SEX, NWY) -
                    getNormalValFrom(NWX, SEY);
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

            answer = getTiltedValFrom(x, y);

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
