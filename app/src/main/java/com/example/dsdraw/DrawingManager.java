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
    Paint mWhitePaint;
    Paint mNodeSelectedPaint;
    Paint mNodePromptPaint;

    public DrawingManager() {
        mTreePaint = new Paint();
        mTreePaint.setStyle(Paint.Style.STROKE);
        mTreePaint.setColor(Color.BLACK);
        mTreePaint.setStrokeWidth(2);
        mWhitePaint = new Paint(mTreePaint);
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(Color.WHITE);
        mNodeSelectedPaint = new Paint(mTreePaint);
        mNodeSelectedPaint.setColor(Color.RED);
        mNodePromptPaint = new Paint(mTreePaint);
        mNodePromptPaint.setColor(Color.BLUE);
    }

    public void drawTree(Canvas canvas, BinaryTree tree, CanvasPoint origin) {
        Log.d(TAG, "drawTree");

        //updated here to handle zoom
        mTreePaint.setTextSize(getTreeNodeFontSize());
        mNodeSelectedPaint.setTextSize(getTreeNodeFontSize());
        mNodePromptPaint.setTextSize(getTreeNodeFontSize());

        Stack<Node> nodeStack = new Stack<>();
        Stack<CanvasPoint> pointStack = new Stack<>();
        nodeStack.push(tree.root);
        pointStack.push(origin);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            CanvasPoint pnt = pointStack.peek();
            cur.setPoint(pnt);

            Paint selectedPaint = mTreePaint;
            if (cur.isPrompt()) {
                selectedPaint = mNodePromptPaint;
            } else if(cur.isSelected()) {
                selectedPaint = mNodeSelectedPaint;
            }


            Log.d(TAG, "Drawing node: " + cur.label + " " + pnt.x + ":" + pnt.y);

            nodeStack.pop();
            pointStack.pop();

//            Log.d(TAG, "tryDetectZoom TREE_X_OFFSET:" + getTreeXOffset());
            int level = binlog(cur.label - 'A' + 1);
            int disp = Math.min(getTreeXOffset()/2, getTreeNodeRadius() * (level - 1));

            Log.d(TAG, "Level calc for label:" + cur.label + ":" + level + ":disp=" + disp);

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                nodeStack.push(cur.right);
                CanvasPoint cp = new CanvasPoint(pnt.x + getTreeXOffset() - disp, pnt.y + getTreeYOffset());
                pointStack.push(cp);
                canvas.drawLine(pnt.x, pnt.y, cp.x, cp.y, mTreePaint);
            }
            if (cur.left != null) {
                nodeStack.push(cur.left);
                CanvasPoint cp = new CanvasPoint(pnt.x - getTreeXOffset() + disp, pnt.y + getTreeYOffset());
                pointStack.push(cp);
                canvas.drawLine(pnt.x, pnt.y, cp.x, cp.y, mTreePaint);
            }

            canvas.drawCircle(pnt.x, pnt.y, getTreeNodeRadius(), mWhitePaint);
            canvas.drawCircle(pnt.x, pnt.y, getTreeNodeRadius(), selectedPaint);
            canvas.drawText(String.valueOf(cur.label), pnt.x - getTreeNodeRadius() / 3, pnt.y + getTreeNodeRadius() / 3, selectedPaint);

        }
    }

    public static int binlog( int bits ) // returns 0 for bits=0
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }
}