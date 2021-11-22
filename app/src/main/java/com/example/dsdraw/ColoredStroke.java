package com.example.dsdraw;

import com.example.dsdraw.structures.CanvasPoint;

import java.util.List;

public class ColoredStroke {
    List<CanvasPoint> stroke;
    int color;
    public ColoredStroke(List<CanvasPoint> points, int c)
    {
        color = c;
        stroke = points;
    }
}
