package com.cupchucker.nativeandroidtoyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Michael on 11/16/2017.
 */

public class CanvasView extends View {
    /**
     * A list of all the active particles in the canvas
     */
    private ArrayList<Particle> mParticleList;

    /**
     * A list that preserves inactive offscreen particles for reuse into the
     * particleList when needed
     */
    private ArrayList<Particle> mRecycleList;

    public int width;

    public int height;

    private Bitmap bitmap;

    private Canvas canvas;

    private Context context;

    private Paint paint;

    private static final float TOLERANCE = 5;

    /**
     * An array that holds the possible particle bitmaps
     */
    private Bitmap particleImages[] = new Bitmap[3];


    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRecycleList = new ArrayList<Particle>();
        mParticleList = new ArrayList<Particle>();

        particleImages[0] =((BitmapDrawable)context.getResources().getDrawable(R.drawable.red_particle)).getBitmap();
        particleImages[1] =((BitmapDrawable)context.getResources().getDrawable(R.drawable.green_particle)).getBitmap();
        particleImages[2] =((BitmapDrawable)context.getResources().getDrawable(R.drawable.blue_particle)).getBitmap();

        context = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mParticleList.size(); i++) {
            Particle p = mParticleList.get(i);
            p.update();
            canvas.drawBitmap(particleImages[p.color], p.x-10, p.y-10, paint);

            // kill if out of bounds, add to recycle list for better memory usage
            // decrement i so we don't get an out of bounds error
            if (p.x < 0 || p.x > width || p.y < 0 || p.y > height) {
                mRecycleList.add(mParticleList.remove(i));
                i--;
            }
        }

        if (mParticleList.size() > 0) {
            invalidate();
        }
    }

    private void startTouch(float x, float y) {
        // TODO
    }

    private void moveTouch(float x, float y) {
        // TODO
    }

    private void upTouch() {
        // TODO
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        Particle p;
        int recycleCount = 0;

        if (mRecycleList == null || mParticleList == null) {
            return false;
        }

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
        invalidate();
        return true;
    }

    public void clearCanvas() {
        invalidate();
    }

    }
