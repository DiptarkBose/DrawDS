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
        if (this.selected) {
            if (left == null) {
                left = new Node((char) ('A' + 2 * (label - 'A') + 1));
                left.setPrompt(true);
            }
            if (right == null) {
                right = new Node((char) ('A' + 2 * (label - 'A') + 2));
                right.setPrompt(true);
            }
        } else {
            if (left != null && left.isPrompt()) {
                left = null;
            }
            if (right != null && right.isPrompt()) {
                right = null;
            }
        }
    }
}
