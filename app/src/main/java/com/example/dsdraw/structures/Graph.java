package com.example.dsdraw.structures;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Graph
{
    private static final String TAG = "BinaryTree";
    public GraphNode root;
    public List<GraphNode> nodes;

    private static float mScalingFactor = 1.0F;

    public static void setScalingFactor(float mScalingFactor) {
        Graph.mScalingFactor = mScalingFactor;
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

    public Graph(char ch, CanvasPoint canvasPoint)
    {
        root = new GraphNode(ch, canvasPoint);
        nodes = new ArrayList<>();
        nodes.add(root);
    }

//    public static BinaryTree getBasicTree() {
//        BinaryTree tree = new BinaryTree();
//        tree.root = new Node('A');
//        tree.root.left = new Node('B');
//        tree.root.right = new Node('C');
////        tree.root.right.setSelected(true);
////        tree.root.left.left = new Node('D');
////        tree.root.left.left.setPrompt(true);
//        return tree;
//    }

    public GraphNode getNodeOverlappingPoint(CanvasPoint touchPoint) {

//        LinkedList<GraphNode> bfs = new LinkedList<>();
//        Map<Character, Boolean> visited = new HashMap<>();
//        bfs.add(root);
//        visited.put(root.label, true);
//
//        while (bfs.size() > 0) {
//            // Pop the top item from queue
//            GraphNode cur = bfs.poll();
//            if (cur != null && cur.getPoint() != null) {
//                CanvasPoint pnt = cur.getPoint();
//                if (isPointInCircle(pnt, touchPoint, Graph.getTreeNodeRadius())) {
//                    return cur;
//                }
//                // Push unvisited children to the queue
//                for(GraphNode gn : cur.nebs) {
//                    if (gn != null && !visited.get(gn.label)) {
//                        visited.put(gn.label, true);
//                        bfs.add(gn);
//                    }
//                }
//            }
//        }

        for (GraphNode gn : nodes) {
            if (gn != null && isPointInCircle(gn.getPoint(), touchPoint, Graph.getTreeNodeRadius())) {
                Log.d(TAG, "getNodeOverlappingPoint found:" + gn.label);
                return gn;
            }
        }
        Log.d(TAG, "getNodeOverlappingPoint null");
        return null;
    }

    private boolean isPointInCircle(CanvasPoint centre, CanvasPoint point, int radius) {
        return ((centre.x - point.x)*(centre.x - point.x) + (centre.y - point.y)*(centre.y - point.y) <= radius*radius);
    }

    private GraphNode getGnFromGnList(List<GraphNode> list, char label) {
        for (GraphNode gn : list) {
            if (gn.label == label) {
                return gn;
            }
        }
        return null;
    }

    private GraphNode getNodeWithLabel(char label) {
        return getGnFromGnList(nodes, label);
    }

    public void addNode(char label, CanvasPoint canvasPoint) {
        Log.d(TAG, "Adding node at point: " + canvasPoint.x + "," + canvasPoint.y);
        nodes.add(new GraphNode(label, canvasPoint));
        Log.d(TAG, "Graph nodes size = " + nodes.size());
    }

    public void removeNode(char label) {
        if (root != null && label == root.label) {
            Log.e(TAG, "Trying to remove node");
            return;
        }
        GraphNode remove = getNodeWithLabel(label);
        if (remove != null) {
            nodes.remove(remove);
            for (GraphNode gn : nodes) {
                gn.nebs.remove(remove);
            }
        }
    }

    public void deselectNodes() {
        for (GraphNode gn : nodes) {
            gn.setSelected(false);
        }
    }

    public void removePrompts() {
        for (GraphNode gn : nodes) {
            gn.setPrompt(false);
        }
    }

    public char getNextLabel() {
        char label = 'A';
        for (GraphNode gn : nodes) {
            if (gn.label >= label) {
                label = (char) (gn.label + 1);
            }
        }
        return label;
    }
}
