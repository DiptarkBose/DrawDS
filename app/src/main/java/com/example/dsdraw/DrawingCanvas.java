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

    Paint paint = new Paint();
    DrawingManager mDrawingManager;
    StrokeManager mStrokeManager;

    BinaryTree tree;
    CanvasPoint org;
    List<List<CanvasPoint>> drawnPoints;

    private final static int TRUE_ORIGIN_X = 500;
    private final static int TRUE_ORIGIN_Y = 300;

    public DrawingCanvas(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
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
            for (int i = 0; i < points.size() - 2; i++) {
                canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
            }
        }
        //Draw current stroke
        List<CanvasPoint> points = mStrokeManager.getCurrentStroke();
        for (int i = 0; i < points.size() - 2; i++) {
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
        }

        mDrawingManager.drawTree(canvas, tree, org);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mStrokeManager.getCurrentStroke().size() > 0) {
                drawnPoints.add(new ArrayList<CanvasPoint>(mStrokeManager.getCurrentStroke()));
                Log.d(TAG, "drawn stroke added true new size:" + drawnPoints.size());
            } else {
                Log.d(TAG, "drawn stroke added false old size:"  + drawnPoints.size());
            }
        }
        mStrokeManager.onTouch(event);
        Log.d(TAG, "tap strokemanager type = " + mStrokeManager.getStrokeType().name());

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
    }

    private void handleTap(CanvasPoint tapPoint) {
        Node touchedNode = tree.getNodeOverlappingPoint(tapPoint);
        if (touchedNode != null) {
            Log.e(TAG, "Touched node tap: " + touchedNode.label);
            if (touchedNode.isSelected()) {
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