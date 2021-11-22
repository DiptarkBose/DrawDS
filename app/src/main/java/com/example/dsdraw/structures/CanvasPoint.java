package com.example.dsdraw.structures;

public class CanvasPoint {
    public float x, y;
    public CanvasPoint(){}
    public CanvasPoint(float _x, float _y) {
        x = _x;
        y = _y;
    }
    public CanvasPoint(CanvasPoint p){
        x = p.x;
        y = p.y;
    }
}