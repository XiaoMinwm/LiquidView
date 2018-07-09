package com.phicode.xm.liquidview;

import com.phicode.xm.liquidview.model.Bubble;

import java.util.Random;

public class BubbleGenerator extends Thread {
    public static final int DEFAULT_BUBBLE_SIZE = 30;
    public static final int DEFAULT_BUBBLE_VELOCITY = 10;
    private boolean isRunning = true;
    private BubbleCallback bubbleCallback;

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public BubbleCallback getBubbleCallback() {
        return bubbleCallback;
    }

    public void setBubbleCallback(BubbleCallback bubbleCallback) {
        this.bubbleCallback = bubbleCallback;
    }

    @Override
    public synchronized void start() {
        super.start();
        while (isRunning) {
            Random random = new Random();
            float nextFloat = random.nextFloat();
            if (nextFloat > 0.5) {
                Bubble bubble = new Bubble();
                bubble.setSize((int) ((random.nextFloat() + 0.2f) * DEFAULT_BUBBLE_SIZE));
                bubble.setXMiddleRatio(random.nextFloat() - 0.5f);
                bubble.setV((int) (DEFAULT_BUBBLE_VELOCITY * (random.nextFloat() + 0.3f)));
                bubbleCallback.generatorBubble(bubble);
            }
        }
    }

    interface BubbleCallback {
        void generatorBubble(Bubble bubble);
    }
}
