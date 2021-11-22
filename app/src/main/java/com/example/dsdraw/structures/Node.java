package com.example.dsdraw.structures;

public class Node
{
    public char label;
    public Node left, right;
    private CanvasPoint point;
    private boolean selected;
    private boolean prompt;

    public Node(char _label)
    {
        label = _label;
        left = right = null;
        selected = prompt = false;
    }

    public CanvasPoint getPoint() { return point; }
    public void setPoint(CanvasPoint pt) {
        point = pt;
    }

    public boolean isPrompt() {
        return prompt;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setPrompt(boolean prompt) {
        this.prompt = prompt;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
