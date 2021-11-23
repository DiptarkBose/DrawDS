package com.example.dsdraw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.example.dsdraw.structures.CanvasPoint;

import java.util.ArrayList;
import java.util.List;

public class StrokeManager {
    private String TAG = "";
    private MultiStrokeStore mMultiStrokeStore;

    private final int TOUCH_THRESHOLD = 10;
    private final int TOUCH_POINTS_THRESHOLD = 5;

    private enum LineDirection {
        U, D, L, R, UR, DR, UL, DL, NONE, TOUCH
    }

    public enum StrokeType {
        TBD, ZOOM_IN, ZOOM_OUT, PAN, TAP
    }

    private List<LineDirection> strokeDirections;
    private List<Integer> strokeLengths;

    private StrokeType mStrokeType = StrokeType.TBD;

    private final double MAX_ZOOM = 1.5;
    private final double MIN_ZOOM = 0.5;

    private double mZoomFactor;
    private CanvasPoint mTapPoint;

    public StrokeManager(String tag){
        TAG = "SM_" + tag;
        mMultiStrokeStore = new MultiStrokeStore();
        strokeDirections = new ArrayList<>(MultiStrokeStore.MAX_FINGERS);
        strokeLengths = new ArrayList<>(MultiStrokeStore.MAX_FINGERS);
        Log.d(TAG, "Lenght of strokeDirections:" + strokeDirections.size());
        Log.d(TAG, "Lenght of strokeLengths:" + strokeLengths.size());
        for(int i=0; i<MultiStrokeStore.MAX_FINGERS; i++) {
            strokeDirections.add(LineDirection.NONE);
            strokeLengths.add(0);
        }

        mZoomFactor = 1.0;
        mTapPoint = new CanvasPoint(0, 0);
    }

    public StrokeType getStrokeType() {
        return mStrokeType;
    }

    public double getZoomFactor() {
        return mZoomFactor;
    }

    public CanvasPoint getTapPoint() {
        return mTapPoint;
    }

    public CanvasPoint getPanOffset() {
        if (mMultiStrokeStore.getStrokeForFinger(2).size() > 1) {
            float xOff = mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 2).x -
                    mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 1).x;
            float yOff = mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 2).y -
                    mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 1).y;
            return new CanvasPoint(xOff, yOff);
        }
//        float xOff = mMultiStrokeStore.getStrokeForFinger(2).get(0).x -
//                mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 1).x;
//        float yOff = mMultiStrokeStore.getStrokeForFinger(2).get(0).y -
//                mMultiStrokeStore.getStrokeForFinger(2).get(mMultiStrokeStore.getStrokeForFinger(2).size() - 1).y;
//        return new CanvasPoint(xOff, yOff);
        return new CanvasPoint(0,0);
    }

    private void printStrokeData() {
        for(int i=0; i<MultiStrokeStore.MAX_FINGERS; i++) {
            Log.d(TAG, "printStrokeData Finger" + i + "-Dir:" + strokeDirections.get(i).name() + "-len:" + strokeLengths.get(i));
        }
    }

    private void updateStrokeDirections() {
        for(int i=0; i<mMultiStrokeStore.getMaxActiveFingers(); i++) {
            List<CanvasPoint> strokePoints = mMultiStrokeStore.getStrokeForFinger(i);
            int len = mMultiStrokeStore.getLengthForFingerStroke(i);

            if (strokePoints.size() < TOUCH_POINTS_THRESHOLD && len <= TOUCH_THRESHOLD) {
                strokeDirections.set(i, LineDirection.TOUCH);
                strokeLengths.set(i, 0);
            } else {
                strokeLengths.set(i, len);
                strokeDirections.set(i, calcDir(strokePoints.get(0), strokePoints.get(strokePoints.size()-1)));
            }
        }
        printStrokeData();
    }

    private void resetParamsForStrokeStart() {
        mStrokeType = StrokeType.TBD;
//        mZoomFactor = 1.0;
        mTapPoint.x = 0;
        mTapPoint.y = 0;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                resetParamsForStrokeStart();
                mMultiStrokeStore.beginStroke();
                mMultiStrokeStore.addTouchPoints(event);
                strokeDirections.set(0, LineDirection.TOUCH);
                break;

            case MotionEvent.ACTION_UP:
                mMultiStrokeStore.addTouchPoints(event);
                updateStrokeDirections();
                if (mStrokeType == StrokeType.TBD) {
                    tryDetectTap(event);
                }
                mMultiStrokeStore.endStroke();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // This happens when you touch the screen with two fingers
                mMultiStrokeStore.incCurrentActiveFingers();
                mMultiStrokeStore.addTouchPoints(event);
                updateStrokeDirections();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // This happens when you release the second finger
                mMultiStrokeStore.addTouchPoints(event);
                mMultiStrokeStore.decCurrentActiveFingers();
                updateStrokeDirections();
                break;

            case MotionEvent.ACTION_MOVE:
                mMultiStrokeStore.addTouchPoints(event);
                updateStrokeDirections();
                break;
        }
        if (mStrokeType == StrokeType.TBD) {
            tryDetectZoom();
        }
        if (mStrokeType == StrokeType.TBD) {
            tryDetectPan();
        }
    }

    private void tryDetectTap(MotionEvent event) {
        if (mMultiStrokeStore.getMaxActiveFingers() == 1 && strokeDirections.get(0) == LineDirection.TOUCH) {
            Log.d(TAG, "tryDetectTap detected tap");
            mStrokeType = StrokeType.TAP;
            mTapPoint = new CanvasPoint(event.getX(), event.getY());
        }
    }

    private void tryDetectPan() {
        if (mMultiStrokeStore.getCurrentActiveFingers() == 3
                && strokeDirections.get(2) != LineDirection.TOUCH
                && strokeDirections.get(2) != LineDirection.NONE) {

            Log.d(TAG, "tryDetectPan Pan detected");
            mStrokeType = StrokeType.PAN;
        }
    }

    private int mappingLineDirections(LineDirection ld) {
        switch (ld) {
            case L: return 1;
            case R: return -1;
            case U: return 2;
            case D: return -2;
            case UL: return 3;
            case DR: return -3;
            case UR: return 4;
            case DL: return -4;
            case NONE: return 0;
            case TOUCH: return 5;
            default: return 100;
        }
    }

    private void tryDetectZoom() {
        if (mMultiStrokeStore.getCurrentActiveFingers() == 2) {
            if (mappingLineDirections(strokeDirections.get(0)) == -mappingLineDirections(strokeDirections.get(1))) {
                int startDist = MultiStrokeStore.calcMag(mMultiStrokeStore.getStrokeForFinger(0).get(0),
                        mMultiStrokeStore.getStrokeForFinger(1).get(0));
                int endDist = MultiStrokeStore.calcMag(mMultiStrokeStore.getStrokeForFinger(0).get(mMultiStrokeStore.getStrokeForFinger(0).size() - 1),
                        mMultiStrokeStore.getStrokeForFinger(1).get(mMultiStrokeStore.getStrokeForFinger(1).size() - 1));
                if (startDist > endDist) {
                    Log.d(TAG, "tryDetectZoom found zoom out");
                    mStrokeType = StrokeType.ZOOM_OUT;
                    mZoomFactor -= 0.5;
                    mZoomFactor = Math.max(mZoomFactor, MIN_ZOOM);
                } else {
                    Log.d(TAG, "tryDetectZoom found zoom in");
                    mStrokeType = StrokeType.ZOOM_IN;
                    mZoomFactor += 0.5;
                    mZoomFactor = Math.min(mZoomFactor, MAX_ZOOM);
                }
            }
        }
    }

    private int approxSlope (double absSlope) {
        if(absSlope >= 0 && absSlope < 0.5) {
            return 0;
        } else if(absSlope >= 0.5 && absSlope <= 2) {
            return 1;
        } else {
            return 1000;
        }
    }

    private LineDirection calcDir(CanvasPoint _p1, CanvasPoint _p2) {
        CanvasPoint p1 = new CanvasPoint(_p1);
        CanvasPoint p2 = new CanvasPoint(_p2);
        p2.y = -p2.y;
        p1.y = -p1.y;
//        double mag =  Math.sqrt((double)((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x)));
//        Log.d(TAG, "Mag = " + mag);
        double slope = Integer.MAX_VALUE * Math.signum(p2.y - p1.y);
        if( p2.x != p1.x) {
            slope = (((double) p2.y - p1.y) / ((double) p2.x - p1.x));
        }
        Log.d(TAG, "Slope = " + slope);
//        slope /= mag;
        int appSlope = approxSlope(Math.abs(slope)) * (int) Math.signum(slope);
        Log.d(TAG, "Calc slope: " + appSlope);
        if(appSlope == 0) {
            if(p2.x > p1.x) {
                return LineDirection.R;
            }
            else {
                return LineDirection.L;
            }
        } else if(Math.abs(appSlope) == 1000) {
            if(p2.y > p1.y) {
                return LineDirection.U;
            } else {
                return LineDirection.D;
            }
        } else if(appSlope == 1) {
            if(p2.y > p1.y) {
                return LineDirection.UR;
            } else {
                return LineDirection.DL;
            }
        } else if(appSlope == -1) {
            if(p2.x > p1.x) {
                return LineDirection.DR;
            } else {
                return LineDirection.UL;
            }
        }
        return LineDirection.NONE;
    }

    public void drawStrokes(Canvas canvas) {
        Log.d(TAG, "drawStrokes");
        List<Paint> paints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            paints.add(new Paint());
        }
        paints.get(0).setColor(Color.BLACK);
        paints.get(1).setColor(Color.RED);
        paints.get(2).setColor(Color.GREEN);
        for (int i = 0; i < 3; i++) {
            List<CanvasPoint> strokePoints = mMultiStrokeStore.getStrokeForFinger(i);
            for (CanvasPoint point : strokePoints) {
                canvas.drawCircle(point.x, point.y, 20, paints.get(i));
                Log.d(TAG, "drawStrokes drawing point for finger " + i + " at x=" + point.x + ":y=" + point.y);
            }
        }
    }

    public List<CanvasPoint> getCurrentStroke() {
        if (mMultiStrokeStore.getMaxActiveFingers() == 1 && strokeDirections.get(0) != LineDirection.TOUCH) {
            return mMultiStrokeStore.getStrokeForFinger(0);
        } else {
            return new ArrayList<>();
        }
    }
}
