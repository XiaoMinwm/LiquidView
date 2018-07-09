package com.phicode.xm.liquidview.model;

public class Bubble {
    private int size;
    private float XMiddleRatio;
    private int v;

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

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "Bubble{" +
                "size=" + size +
                ", XMiddleRatio=" + XMiddleRatio +
                ", v=" + v +
                '}';
    }
}
