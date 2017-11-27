package com.cupchucker.nativeandroidtoyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Michael on 11/16/2017.
 */

public class ParticleView extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * Thread for drawing particles to a canvas
     */
    private ParticleDrawingThread mDrawingThread;

    /**
     * A list of all the active particles in the canvas
     */
    private ArrayList<Particle> mParticleList;

    /**
     * A list that preserves inactive offscreen particles for reuse into the
     * particleList when needed
     */
    private ArrayList<Particle> mRecycleList;

    /**
     * A variable with application context
     */
    private Context context;


    public ParticleView(Context context) {
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        this.context = context;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mDrawingThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawingThread = new ParticleDrawingThread(holder, context);
        mParticleList = mDrawingThread.getParticleList();
        mRecycleList = mDrawingThread.getRecycleList();
        mDrawingThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mDrawingThread.stopDrawing();
        while (retry) {
            try {
                mDrawingThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Particle p;
        int recycleCount = 0;

        if (mRecycleList.size() > 1) {
            recycleCount = 2;
        } else {
            recycleCount = mRecycleList.size();
        }

        for (int i = 0; i < recycleCount; i++) {
            p = mRecycleList.remove(0);
            p.init((int) e.getX(), (int) e.getY());
            mParticleList.add(p);
        }

        for (int i = 0; i < 2-recycleCount; i++) {
            mParticleList.add(new Particle((int) e.getX(), (int) e.getY()));
        }

        System.out.println("motion!");
        return super.onTouchEvent(e);
    }

}
