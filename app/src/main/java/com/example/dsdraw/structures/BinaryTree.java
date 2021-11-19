package com.example.dsdraw.structures;

import android.graphics.Canvas;
import android.util.Log;

import java.util.Stack;

public class BinaryTree
{
    private static final String TAG = "BinaryTree";
    public Node root;

    public final static int TREE_X_OFFSET = 100;
    public final static int TREE_Y_OFFSET = 100;
    public final static int TREE_NODE_RADIUS = 50;
    public final static float TREE_NODE_FONT_SIZE = 48f;

    BinaryTree(char ch)
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
        tree.root.left.left = new Node('D');
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

            if(isPointInCircle(pnt, touchPoint, TREE_NODE_RADIUS)) {
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
}
