package com.example.dsdraw.structures;

public class Node
{
    public char label;
    public Node left, right;
    private CanvasPoint point;

    public Node(char _label)
    {
        label = _label;
        left = right = null;
    }

    public CanvasPoint getPoint() { return point; }
    public void setPoint(CanvasPoint pt) {
        point = pt;
    }
}
