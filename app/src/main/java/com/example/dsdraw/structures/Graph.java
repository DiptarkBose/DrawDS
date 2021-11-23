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

    public GraphNode getNodeOverlappingPoint(CanvasPoint touchPoint) {

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

    public GraphNode getSelectedNode() {
        for (GraphNode gn : nodes) {
            if (gn.isSelected()) {
                return gn;
            }
        }
        return null;
    }

    public void updateOffset(float x, float y) {
        for (GraphNode gn : nodes) {
            if (gn != null) {
                CanvasPoint cp = gn.getPoint();
                cp.x -= x;
                cp.y -= y;
            }
        }
    }

    public void updateDistancesFromRoot(float prevFactor, float newFactor) {
        if (prevFactor != newFactor) {
            float propChange = newFactor / prevFactor;
            for (GraphNode gn : nodes) {
                if (gn != null && gn.label != root.label) {
                    CanvasPoint cp = gn.getPoint();
                    Log.d(TAG, "updateDistancesFromRoot node " + gn.label + "x:" + cp.x + ",y:" + cp.y);
                    cp.x = root.getPoint().x + (cp.x - root.getPoint().x) * propChange;
                    cp.y = root.getPoint().y + (cp.y - root.getPoint().y) * propChange;
                    Log.d(TAG, "updateDistancesFromRoot node " + gn.label + "x:" + cp.x + ",y:" + cp.y);
                }
            }
        }
    }
}
