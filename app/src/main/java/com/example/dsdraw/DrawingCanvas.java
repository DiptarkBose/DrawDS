package com.example.dsdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.dsdraw.structures.BinaryTree;
import com.example.dsdraw.structures.CanvasPoint;
import com.example.dsdraw.structures.Node;

import java.util.ArrayList;
import java.util.List;

public class DrawingCanvas extends View implements View.OnTouchListener {
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

    public DrawingCanvas(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
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
                Log.d(TAG, "getNodeOverlappingPoint handleTap");
                handleTap(mStrokeManager.getTapPoint());
                break;
        }

        invalidate();
        return true;
//        int numPointers = event.getPointerCount();
//        if (numPointers == 1) {
//            CanvasPoint touchPoint = new CanvasPoint(event.getX(), event.getY());
//            points.add(touchPoint);
//
//            if(event.getAction() == 0) {
//                Node touchedNode = tree.getNodeOverlappingPoint(touchPoint);
//                if (touchedNode != null) {
//                    Toast toast = Toast.makeText(c, "Touched node: " + touchedNode.label, Toast.LENGTH_SHORT);
//                    toast.show();
//                    Log.e(TAG, "Touched node: " + touchedNode.label + ". " + event.getAction());
//                } else {
//                    Log.e(TAG, "No overlapping node found for touch. " + event.getAction());
//                }
//            }
//            invalidate();
//        }
//        else {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    // This happens when you touch the screen with two fingers
//                    mode = SWIPE;
//                    startY = event.getY(0);
//                    startX = event.getX(0);
//                    break;
//
//                case MotionEvent.ACTION_POINTER_UP:
//                    // This happens when you release the second finger
//                    mode = NONE;
//                    if (Math.abs(startX - stopX) > THRESHOLD && Math.abs(startY - stopY) < THRESHOLD) {
//                        if (startX > stopX) {
//                            // Swipe left
//                            Toast toast = Toast.makeText(c, "Double Finger Left Swipe", Toast.LENGTH_SHORT);
//                            toast.show();
//                        } else {
//                            //Swipe right
//                            Toast toast = Toast.makeText(c, "Double Finger Right Swipe", Toast.LENGTH_SHORT);
//                            toast.show();
//                        }
//                    } else if (Math.abs(startY - stopY) > THRESHOLD && Math.abs(startX - stopX) < THRESHOLD) {
//                        if (startY > stopY) {
//                            // Swipe up
//                            Toast toast = Toast.makeText(c, "Double Finger Up Swipe", Toast.LENGTH_SHORT);
//                            toast.show();
//                        } else {
//                            //Swipe down
//                            Toast toast = Toast.makeText(c, "Double Finger Down Swipe", Toast.LENGTH_SHORT);
//                            toast.show();
//                        }
//                    }
//                    this.mode = NONE;
//                    break;
//
//                case MotionEvent.ACTION_MOVE:
//                    if (mode == SWIPE) {
//                        stopY = event.getY(0);
//                        stopX = event.getX(0);
//                    }
//                    break;
//            }
//        }
//        return true;
    }

    private void handleTap(CanvasPoint tapPoint) {
        Node touchedNode = tree.getNodeOverlappingPoint(tapPoint);
        if (touchedNode != null) {
//            Toast toast = Toast.makeText(c, "Touched node: " + touchedNode.label, Toast.LENGTH_SHORT);
//            toast.show();
            Log.e(TAG, "Touched node tap: " + touchedNode.label);
            if (touchedNode.isSelected()) {
//                touchedNode = null;
                tree.removeNode(touchedNode.label);
                tree.removePrompts();
            } else if (touchedNode.isPrompt()) {
                touchedNode.setPrompt(false);
                tree.deselectNodes();
                tree.removePrompts();
            } else {
                touchedNode.setSelected(true);
            }
        } else {
            Log.e(TAG, "No overlapping node found for touch. tap");
            tree.deselectNodes();
            tree.removePrompts();
        }
    }
}