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

    public DrawingManager() {
        mTreePaint = new Paint();
        mTreePaint.setStyle(Paint.Style.STROKE);
        mTreePaint.setColor(Color.BLACK);
    }

    public void drawTree(Canvas canvas, BinaryTree tree, CanvasPoint origin) {
        Log.d(TAG, "drawTree");
        mTreePaint.setTextSize(getTreeNodeFontSize());
        Stack<Node> nodeStack = new Stack<>();
        Stack<CanvasPoint> pointStack = new Stack<>();
        nodeStack.push(tree.root);
        pointStack.push(origin);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            CanvasPoint pnt = pointStack.peek();
            cur.setPoint(pnt);

            canvas.drawCircle(pnt.x, pnt.y, getTreeNodeRadius(), mTreePaint);
            canvas.drawText(String.valueOf(cur.label), pnt.x, pnt.y, mTreePaint);

            Log.d(TAG, "Drawing node: " + cur.label + " " + pnt.x + ":" + pnt.y);

            nodeStack.pop();
            pointStack.pop();

//            Log.d(TAG, "tryDetectZoom TREE_X_OFFSET:" + getTreeXOffset());

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                nodeStack.push(cur.right);
                pointStack.push(new CanvasPoint(pnt.x + getTreeXOffset(), pnt.y + getTreeYOffset()));
                canvas.drawLine(pnt.x, pnt.y, pnt.x + getTreeXOffset(), pnt.y + getTreeYOffset(), mTreePaint);
            }
            if (cur.left != null) {
                nodeStack.push(cur.left);
                pointStack.push(new CanvasPoint(pnt.x - getTreeXOffset(), pnt.y + getTreeYOffset()));
                canvas.drawLine(pnt.x, pnt.y, pnt.x - getTreeXOffset(), pnt.y + getTreeYOffset(), mTreePaint);
            }
        }
    }
}