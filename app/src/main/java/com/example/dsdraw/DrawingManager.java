package com.example.dsdraw;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.dsdraw.structures.BinaryTree;
import com.example.dsdraw.structures.CanvasPoint;
import com.example.dsdraw.structures.Node;

import java.util.Stack;

import static com.example.dsdraw.structures.BinaryTree.*;

class DrawingManager {
    private final String TAG = "DrawingManager";
    Paint mTreePaint;

    public enum LineDirection {
        U, D, L, R, UR, DR, UL, DL, WTF
    }

    public DrawingManager() {
        mTreePaint = new Paint();
        mTreePaint.setStyle(Paint.Style.STROKE);
        mTreePaint.setColor(Color.BLACK);
        mTreePaint.setTextSize(TREE_NODE_FONT_SIZE);
    }

    public void drawTree(Canvas canvas, BinaryTree tree, CanvasPoint origin) {
        Log.d(TAG, "drawTree");
        Stack<Node> nodeStack = new Stack<>();
        Stack<CanvasPoint> pointStack = new Stack<>();
        nodeStack.push(tree.root);
        pointStack.push(origin);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            CanvasPoint pnt = pointStack.peek();
            cur.setPoint(pnt);

            canvas.drawCircle(pnt.x, pnt.y, TREE_NODE_RADIUS, mTreePaint);
            canvas.drawText(String.valueOf(cur.label), pnt.x, pnt.y, mTreePaint);

            Log.d(TAG, "Drawing node: " + cur.label + " " + pnt.x + ":" + pnt.y);

            nodeStack.pop();
            pointStack.pop();

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                nodeStack.push(cur.right);
                pointStack.push(new CanvasPoint(pnt.x + TREE_X_OFFSET, pnt.y + TREE_Y_OFFSET));
                canvas.drawLine(pnt.x, pnt.y, pnt.x + TREE_X_OFFSET, pnt.y + TREE_Y_OFFSET, mTreePaint);
            }
            if (cur.left != null) {
                nodeStack.push(cur.left);
                pointStack.push(new CanvasPoint(pnt.x - TREE_X_OFFSET, pnt.y + TREE_Y_OFFSET));
                canvas.drawLine(pnt.x, pnt.y, pnt.x - TREE_X_OFFSET, pnt.y + TREE_Y_OFFSET, mTreePaint);
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

    public LineDirection calcDir(CanvasPoint p1, CanvasPoint p2) {
        p2.y = -p2.y;
        p1.y = -p1.y;
        double mag =  Math.sqrt((double)((p2.y - p1.y) * (p2.y - p1.y) + (p2.x - p1.x) * (p2.x - p1.x)));
        Log.d(TAG, "Mag = " + mag);
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
        return LineDirection.WTF;
    }
}
