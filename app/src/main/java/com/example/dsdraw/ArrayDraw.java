package com.example.dsdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ArrayDraw extends View implements View.OnTouchListener {
    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float startX;
    private float stopY;
    private float stopX;
    private static final int THRESHOLD = 100;
    private static int numNodes = 1;
    private static int addNodes = 0;
    private static int deleteNodes = 0;

    List<Point> points = new ArrayList<>();
    List<Point> curStroke = new ArrayList<>();
    Context c;
    Paint paint = new Paint();

    public ArrayDraw(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        paint.setColor(Color.BLACK);
        c = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        float x1 = 100, y1 = 100, x2 = 200, y2 = 200;
        Paint myPaint = new Paint();
        Paint tentPaint = new Paint();

        myPaint.setColor(Color.rgb(0, 0, 0));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(10);

        tentPaint.setColor(Color.rgb(211,211,211));
        tentPaint.setStyle(Paint.Style.STROKE);
        tentPaint.setStrokeWidth(10);

        for(int i=0; i<Math.max(1, numNodes-deleteNodes); i++) {
            canvas.drawRect(x1, y1, x2, y2, myPaint);
            x1 += 100;
            x2 += 100;
        }
        for(int i=0; i<addNodes; i++) {
            canvas.drawRect(x1, y1, x2, y2, tentPaint);
            x1 += 100;
            x2 += 100;
        }
        if(curStroke.size()>2) {
            for(Point point : curStroke)
                points.add(point);
        }
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 20, paint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Point point;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                point = new Point();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                point = new Point();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
                invalidate();
                curStroke.clear();
                break;

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
                        numNodes = Math.max(1, numNodes-deleteNodes);
                        deleteNodes = 0;
                        invalidate();
                    } else {
                        //Swipe right
                        Toast toast = Toast.makeText(c, "Double Finger Right Swipe", Toast.LENGTH_SHORT);
                        toast.show();
                        numNodes += addNodes;
                        addNodes = 0;
                        invalidate();
                    }
                } else if (Math.abs(startY - stopY) > THRESHOLD && Math.abs(startX - stopX) < THRESHOLD) {
                    if (startY > stopY) {
                        // Swipe up
                        Toast toast = Toast.makeText(c, "Double Finger Up Swipe", Toast.LENGTH_SHORT);
                        toast.show();
                        points.clear();
                        invalidate();
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

                    if(stopX >= startX) {
                        deleteNodes = 0;
                        addNodes = (int) ((stopX - startX) / 100);
                    }
                    else {
                        addNodes = 0;
                        deleteNodes = (int) ((startX - stopX) / 100);
                    }
                    invalidate();
                }
                else
                {
                    point = new Point();
                    point.x = event.getX();
                    point.y = event.getY();
                    curStroke.add(point);
                    invalidate();
                }
                break;
        }
        return true;
    }
}