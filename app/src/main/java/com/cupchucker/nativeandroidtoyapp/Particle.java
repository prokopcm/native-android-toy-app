package com.cupchucker.nativeandroidtoyapp;

import java.util.Random;

/**
 * Created by Michael on 11/16/2017.
 */

public class Particle {
    public int distFromOrigin;
    public int x;
    public int y;
    public int color;

    private double direction;
    private double directionCosine;
    private double directionSine;
    private int initX;
    private int initY;

    private final static int NUM_OF_DIRECTIONS = 400;

    public Particle(int x, int y) {
        init(x, y);
        direction = 2*Math.PI * new Random().nextInt(NUM_OF_DIRECTIONS)/NUM_OF_DIRECTIONS;
        directionSine = Math.sin(direction);
        directionCosine = Math.cos(direction);
        color = new Random().nextInt(3);
    }

    public void init(int x, int y) {
        distFromOrigin = 0;
        this.initX = x;
        this.initY = y;
    }

    public synchronized void update() {
        distFromOrigin += 2;
        x = (int) (initX + (distFromOrigin * directionCosine));
        y = (int) (initY + (distFromOrigin * directionSine));
    }

}
