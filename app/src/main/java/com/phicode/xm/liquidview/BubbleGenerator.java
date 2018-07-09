package com.phicode.xm.liquidview;

import com.phicode.xm.liquidview.model.Bubble;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BubbleGenerator extends Thread {
    public static final int DEFAULT_BUBBLE_SIZE = 20;
    public static final int DEFAULT_BUBBLE_VELOCITY = 10;
    private boolean isRunning = true;
    private boolean isGenerate = false;
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

    public boolean isGenerate() {
        return isGenerate;
    }

    public void setGenerate(boolean generate) {
        isGenerate = generate;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            if (!isGenerate) {
                continue;
            }
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Random random = new Random();
            float nextFloat = random.nextFloat();
            if (nextFloat > 0.1) {
                int num = random.nextInt(4) + 1;
                ArrayList<Bubble> bubbles = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    Bubble bubble = new Bubble();
                    bubble.setSize((int) ((random.nextFloat() / 2 + 0.7f) * DEFAULT_BUBBLE_SIZE));
                    bubble.setXMiddleRatio(random.nextFloat() - 0.5f);
                    bubble.setBounceHeight((int) (DEFAULT_BUBBLE_VELOCITY * (random.nextFloat() + 0.3f)));
                    bubbles.add(bubble);
                }
                bubbleCallback.generatorBubble(bubbles);
            }
            try {
                sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public interface BubbleCallback {
        void generatorBubble(List<Bubble> bubbles);
    }
}
