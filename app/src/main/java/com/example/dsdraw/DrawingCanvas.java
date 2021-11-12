package com.example.dsdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas extends View implements View.OnTouchListener {
    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float startX;
    private float stopY;
    private float stopX;
    private static final int THRESHOLD = 100;
    List<Point> points = new ArrayList<>();
    Context c;
    Paint paint = new Paint();

    public DrawingCanvas(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.BLACK);
        c = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 20, paint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int numPointers = event.getPointerCount();
        if (numPointers == 1) {
            Point point = new Point();
            point.x = event.getX();
            point.y = event.getY();
            points.add(point);
            invalidate();
        }
        else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    // This happens when you touch the screen with two fingers
                    mode = SWIPE;
                    startY = event.getY(0);
                    startX = event.getX(0);
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // This happens when you release the second finger
                    mode = NONE;
                    if (Math.abs(startX - stopX) > THRESHOLD && Math.abs(startY - stopY) < THRESHOLD) {
                        if (startX > stopX) {
                            // Swipe left
                            Toast toast = Toast.makeText(c, "Double Finger Left Swipe", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            //Swipe right
                            Toast toast = Toast.makeText(c, "Double Finger Right Swipe", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else if (Math.abs(startY - stopY) > THRESHOLD && Math.abs(startX - stopX) < THRESHOLD) {
                        if (startY > stopY) {
                            // Swipe up
                            Toast toast = Toast.makeText(c, "Double Finger Up Swipe", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            //Swipe down
                            Toast toast = Toast.makeText(c, "Double Finger Down Swipe", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    this.mode = NONE;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == SWIPE) {
                        stopY = event.getY(0);
                        stopX = event.getX(0);
                    }
                    break;
            }
        }
        return true;
    }
}
class Point{
    float x, y;
}
