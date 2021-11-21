package com.example.dsdraw;

import android.util.Log;
import android.view.MotionEvent;

import com.example.dsdraw.structures.CanvasPoint;

import java.util.ArrayList;
import java.util.List;

public class MultiStrokeStore {
    private static final String TAG = MultiStrokeStore.class.getSimpleName();

    public static  final int MIN_FINGERS = 0;
    public static final int MAX_FINGERS = 3;

    private int mCurrentActiveFingers = 0;
    private int mMaxActiveFingers = 0;

    private List<List<CanvasPoint>> curstrokes;

    public MultiStrokeStore(){
        curstrokes = new ArrayList<List<CanvasPoint>>(MAX_FINGERS);
        for (int i = 0; i < MAX_FINGERS; i++) {
            curstrokes.add(new ArrayList<CanvasPoint>());
        }
    }

    public int getCurrentActiveFingers() { return mCurrentActiveFingers; }
    public int getMaxActiveFingers() { return mMaxActiveFingers; }

    public void incCurrentActiveFingers() {
        mCurrentActiveFingers++;
        mCurrentActiveFingers = Math.min(mCurrentActiveFingers, MAX_FINGERS);
        mMaxActiveFingers = Math.max(mCurrentActiveFingers, mMaxActiveFingers);
    }

    public void decCurrentActiveFingers() {
        mCurrentActiveFingers--;
        mCurrentActiveFingers = Math.max(mCurrentActiveFingers, MIN_FINGERS);
    }

    private void clearCurrentStrokes() {
        for(List<CanvasPoint> stroke : curstrokes) {
            stroke.clear();
        }
        mCurrentActiveFingers = 0;
        mMaxActiveFingers = 0;
    }

    //0-indexed finger
    public List<CanvasPoint> getStrokeForFinger(int finger) {
        if(finger >= MIN_FINGERS && finger < MAX_FINGERS) {
            return curstrokes.get(finger);
        } else {
            return new ArrayList<>();
        }
    }

    public static int calcMag(CanvasPoint p1, CanvasPoint p2) {
        if(p1 == null || p2 == null) {
            return 0;
        }
        return (int) Math.sqrt((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x));
    }

    public int getLengthForFingerStroke(int finger) {
        if(finger >= MIN_FINGERS && finger < MAX_FINGERS && curstrokes.size() > 0 && curstrokes.get(finger).size() > 0) {
            return calcMag(curstrokes.get(finger).get(0), curstrokes.get(finger).get(curstrokes.get(finger).size() - 1));
        } else {
            return 0;
        }
    }

    public void addTouchPoints(MotionEvent event) {
//        Log.d(TAG, "addTouchPoints");
        for(int i=0; i<mCurrentActiveFingers; i++) {
            if (event.getPointerCount() > i) {
                curstrokes.get(i).add(new CanvasPoint(event.getX(i), event.getY(i)));
                Log.d(TAG, "addTouchPoints for finger " + i + " at x=" + event.getX(i) + ":y=" + event.getY(i));
                Log.d(TAG, "addTouchPoints size for finger " + i + "=" + curstrokes.get(i).size());
            }
        }
    }

    public void beginStroke() {
        clearCurrentStrokes();
        incCurrentActiveFingers();
    }

    public void endStroke() {
//        decCurrentActiveFingers();
//        clearCurrentStrokes();
    }

}
