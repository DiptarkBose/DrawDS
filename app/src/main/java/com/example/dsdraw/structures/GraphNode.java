package com.example.dsdraw.structures;

import java.util.ArrayList;
import java.util.List;

public class GraphNode
{
    public char label;
    public List<GraphNode> nebs;
    private CanvasPoint point;
    private boolean selected;
    private boolean prompt;

    public GraphNode(char _label, CanvasPoint canvasPoint)
    {
        label = _label;
        nebs = new ArrayList<>();
        selected = prompt = false;
        point = new CanvasPoint(canvasPoint);
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
