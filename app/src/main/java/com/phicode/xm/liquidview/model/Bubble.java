package com.phicode.xm.liquidview.model;

public class Bubble {
    private int size;
    private float XMiddleRatio;
    private float bounceHeight;
    private int alpha;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public float getXMiddleRatio() {
        return XMiddleRatio;
    }

    public void setXMiddleRatio(float XMiddleRatio) {
        this.XMiddleRatio = XMiddleRatio;
    }

    public float getBounceHeight() {
        return bounceHeight;
    }

    public void setBounceHeight(float bounceHeight) {
        this.bounceHeight = bounceHeight;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "size=" + size +
                ", XMiddleRatio=" + XMiddleRatio +
                ", bounceHeight=" + bounceHeight +
                ", alpha=" + alpha +
                '}';
    }
}
