package com.example.dsdraw.structures;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Stack;

public class BinaryTree
{
    private static final String TAG = "BinaryTree";
    public Node root;

    private static float mScalingFactor = 1.0F;

    public static void setScalingFactor(float mScalingFactor) {
        BinaryTree.mScalingFactor = mScalingFactor;
        Log.d(TAG, "tryDetectZoom setScalingFactor:" + mScalingFactor);
    }

    public static float getScalingFactor() {
        return mScalingFactor;
    }

    private static final int TREE_X_OFFSET = 100;
    private static final int TREE_Y_OFFSET = 150;
    private static final int TREE_NODE_RADIUS = 50;
    private static final float TREE_NODE_FONT_SIZE = 48f;

    public static int getTreeXOffset() {
        return (int) (TREE_X_OFFSET * mScalingFactor);
    }

    public static int getTreeYOffset() {
        return (int) (TREE_Y_OFFSET * mScalingFactor);
    }

    public static int getTreeNodeRadius() {
        return (int) (TREE_NODE_RADIUS * mScalingFactor);
    }

    public static float getTreeNodeFontSize() {
        return TREE_NODE_FONT_SIZE * mScalingFactor;
    }

    public BinaryTree(char ch)
    {
        root = new Node(ch);
    }

    public BinaryTree()
    {
        root = null;
    }

    public static BinaryTree getBasicTree() {
        BinaryTree tree = new BinaryTree();
        tree.root = new Node('A');
        tree.root.left = new Node('B');
        tree.root.right = new Node('C');
//        tree.root.right.setSelected(true);
//        tree.root.left.left = new Node('D');
//        tree.root.left.left.setPrompt(true);
        return tree;
    }

    public Node getNodeOverlappingPoint(CanvasPoint touchPoint) {
        Log.d(TAG, "getNodeOverlappingPoint");
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            CanvasPoint pnt = cur.getPoint();

            if(isPointInCircle(pnt, touchPoint, getTreeNodeRadius())) {
                return cur;
            }

            nodeStack.pop();

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                nodeStack.push(cur.right);
            }
            if (cur.left != null) {
                nodeStack.push(cur.left);
            }
        }
        return null;
    }

    private boolean isPointInCircle(CanvasPoint centre, CanvasPoint point, int radius) {
        return ((centre.x - point.x)*(centre.x - point.x) + (centre.y - point.y)*(centre.y - point.y) <= radius*radius);
    }

    public void removeNode(char label) {
        if (root != null && label == root.label) {
            Log.e(TAG, "Trying to remove node");
            return;
        }
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            nodeStack.pop();

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                if (cur.right.label == label) {
                    cur.right = null;
                    return;
                }
                nodeStack.push(cur.right);
            }
            if (cur.left != null) {
                if (cur.left.label == label) {
                    cur.left = null;
                    return;
                }
                nodeStack.push(cur.left);
            }
        }
    }

    public void deselectNodes() {
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            nodeStack.pop();
            cur.setSelected(false);

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                nodeStack.push(cur.right);
            }
            if (cur.left != null) {
                nodeStack.push(cur.left);
            }
        }
    }

    public void removePrompts() {
        Stack<Node> nodeStack = new Stack<>();
        nodeStack.push(root);

        while (!nodeStack.empty()) {

            // Pop the top item from stack and print it
            Node cur = nodeStack.peek();
            nodeStack.pop();

            // Push right and left children of the popped node to stack
            if (cur.right != null) {
                if (cur.right.isPrompt()) {
                    cur.right = null;
                } else {
                    nodeStack.push(cur.right);
                }
            }
            if (cur.left != null) {
                if (cur.left.isPrompt()) {
                    cur.left = null;
                } else {
                    nodeStack.push(cur.left);
                }
            }
        }
    }
}
