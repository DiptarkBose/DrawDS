package com.example.dsdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.dsdraw.structures.BinaryTree;
import com.example.dsdraw.structures.CanvasPoint;

import java.util.ArrayList;
import java.util.List;

public class TreeDraw extends RelativeLayout implements View.OnTouchListener {
    private final String TAG = "DrawingCanvas";

    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float startX;
    private float stopY;
    private float stopX;
    private static final int THRESHOLD = 100;
    //    List<CanvasPoint> points = new ArrayList<>();
    Context c;
    Paint paint = new Paint();

    DrawingManager mDrawingManager;

    BinaryTree tree;
    CanvasPoint org;

    StrokeManager mStrokeManager;

    List<List<CanvasPoint>> drawnPoints;

    private final static int TRUE_ORIGIN_X = 500;
    private final static int TRUE_ORIGIN_Y = 300;

    public TreeDraw(Context context, AttributeSet attrs) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        inflate(context, R.layout.treedraw, this);

        Log.d("Tree class", "Yesss");

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        c = context;
        tree = BinaryTree.getBasicTree();
        org = new CanvasPoint(TRUE_ORIGIN_X,TRUE_ORIGIN_Y);

        mDrawingManager = new DrawingManager();
        mStrokeManager = new StrokeManager(TAG);

        drawnPoints = new ArrayList<>();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw drawnPoints size = " + drawnPoints.size());
        for(List<CanvasPoint> points : drawnPoints) {
//            for (CanvasPoint point : points) {
//                canvas.drawCircle(point.x, point.y, 20, paint);
//            }
            for (int i = 0; i < points.size() - 2; i++) {
                canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
            }
        }
        //Draw current stroke
//        for (CanvasPoint point : mStrokeManager.getCurrentStroke()) {
//            canvas.drawCircle(point.x, point.y, 20, paint);
//        }
        List<CanvasPoint> points = mStrokeManager.getCurrentStroke();
        for (int i = 0; i < points.size() - 2; i++) {
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
        }

        mDrawingManager.drawTree(canvas, tree, org);
//        mStrokeManager.drawStrokes(canvas);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP
//                || (event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//            invalidate();
//        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mStrokeManager.getCurrentStroke().size() > 0) {
                drawnPoints.add(new ArrayList<CanvasPoint>(mStrokeManager.getCurrentStroke()));
                Log.d(TAG, "drawn stroke added true new size:" + drawnPoints.size());
            } else {
                Log.d(TAG, "drawn stroke added false old size:"  + drawnPoints.size());
            }
        }
        mStrokeManager.onTouch(event);

        switch (mStrokeManager.getStrokeType()) {

            case TBD:
                break;
            case ZOOM_IN:
                BinaryTree.setScalingFactor((float) mStrokeManager.getZoomFactor());
//                Log.d(TAG, "tryDetectZoom onTouch zoom_in");
                break;
            case ZOOM_OUT:
                BinaryTree.setScalingFactor((float) mStrokeManager.getZoomFactor());
//                Log.d(TAG, "tryDetectZoom onTouch zoom_out");
                break;
            case PAN:
                CanvasPoint panOffset = mStrokeManager.getPanOffset();
//                org.x = TRUE_ORIGIN_X - panOffset.x;
//                org.y = TRUE_ORIGIN_Y - panOffset.y;
                org.x -= panOffset.x;
                org.y -= panOffset.y;
                break;
            case TAP:
                break;
        }

        invalidate();
        return true;
    }
}