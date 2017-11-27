package com.cupchucker.nativeandroidtoyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.SurfaceHolder;

import java.util.ArrayList;

/**
 * Created by Michael on 11/16/2017.
 */

public class ParticleDrawingThread extends Thread {

    /**
     * Whether animations and drawing is running
     */
    private boolean isRunning = true;

    /**
     *
     */
    private SurfaceHolder mSurfaceHolder;

    /**
     * A list of all the active particles in the canvas
     */
    private ArrayList<Particle> mParticleList = new ArrayList<Particle>();

    /**
     * A list that preserves inactive offscreen particles for reuse into the
     * particleList when needed
     */
    private ArrayList<Particle> mRecycleList = new ArrayList();

    /**
     * The width of the canvas in pixels
     */
    private int mCanvasWidth;

    /**
     * The height of the canvas in pixels
     */
    private int mCanvasHeight;

    /**
     * A white paint style used to clear the screen
     */
    private Paint mPaint;

    /**
     * An array that holds the possible particle bitmaps
     */
    private Bitmap mImage[] = new Bitmap[3];


    public ParticleDrawingThread(SurfaceHolder mSurfaceHolder, Context mContext) {
        this.mSurfaceHolder = mSurfaceHolder;

        this.mPaint = new Paint();
        mPaint.setColor(Color.WHITE);

        mImage[0] =((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.red_particle)).getBitmap();
        mImage[1] =((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.green_particle)).getBitmap();
        mImage[2] =((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.blue_particle)).getBitmap();
    }

    @Override
    public void run() {
        while (isRunning) {
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    draw(c);
                }
            } finally {
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }

    private void draw(Canvas c) {
        c.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mPaint);

        synchronized (mParticleList) {
            for (int i = 0; i < mParticleList.size(); i++) {
                Particle p = mParticleList.get(i);
                p.update();
                c.drawBitmap(mImage[p.color], p.x-10, p.y-10, mPaint);

                // kill if out of bounds, add to recycle list for better memory usage
                // decrement i so we don't get an out of bounds error
                if (p.x < 0 || p.x > mCanvasWidth || p.y < 0 || p.y > mCanvasHeight) {
                    mRecycleList.add(mParticleList.remove(i));
                    i--;
                }
            }
        }
    }

    /**
     * Stops drawing, animation, and adding new points
     */
    public void stopDrawing() {
        this.isRunning = false;
    }

    /**
     * Gets the list of currently active particles on the screen
     * @return the list of currently active particles on the screen
     */
    public ArrayList getParticleList() {
        return mParticleList;
    }

    /**
     * Gets the list of inactive particles
     * @return the list of inactive particles
     */
    public ArrayList getRecycleList() {
        return mRecycleList;
    }

    /**
     * Sets the size of the drawing canvas
     * @param width the x-width of the canvas
     * @param height the y-height of the canvas
     */
    public void setSurfaceSize(int width, int height) {
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

}
