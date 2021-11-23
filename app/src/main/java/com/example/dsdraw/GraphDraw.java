package com.example.dsdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.dsdraw.structures.CanvasPoint;
import com.example.dsdraw.structures.Graph;
import com.example.dsdraw.structures.GraphNode;

import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class GraphDraw extends RelativeLayout implements View.OnTouchListener {
    private final String TAG = "GraphDraw";

    Paint paint = new Paint();
    DrawingManager mDrawingManager;
    StrokeManager mStrokeManager;

    Graph graph;
    CanvasPoint org;
    List<ColoredStroke> allStrokes;
//    List<List<CanvasPoint>> drawnPoints;
    Button clear;

    private final static int TRUE_ORIGIN_X = 500;
    private final static int TRUE_ORIGIN_Y = 300;

    public static int highlightBox, mDefaultColor;
    private Button mPickColorButton;

    public GraphDraw(final Context context, AttributeSet attrs) {
        super(context);
        this.setWillNotDraw(false);
        setOnTouchListener(this);

        inflate(context, R.layout.graphdraw, this);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);
        org = new CanvasPoint(TRUE_ORIGIN_X,TRUE_ORIGIN_Y);
        graph = new Graph('A', org);

        mDrawingManager = new DrawingManager();
        mStrokeManager = new StrokeManager(TAG);

//        drawnPoints = new ArrayList<>();
        allStrokes = new ArrayList<>();

        mPickColorButton = findViewById(R.id.pick_color_button);
        mDefaultColor = 0;
        paint.setStrokeWidth(10);

        mPickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ColorPickerPopup.Builder(context).initialColor(Color.RED)
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                mDefaultColor = color;
                                paint.setColor(mDefaultColor);
                                mPickColorButton.setBackgroundColor(mDefaultColor);
                            }
                        });
            }
        });

        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked clear");
//                drawnPoints.clear();
                allStrokes.clear();
                invalidate();
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw allStrokes size = " + allStrokes.size());
        mDrawingManager.drawGraph(canvas, graph, org);

        //Draw current stroke
        List<CanvasPoint> points = mStrokeManager.getCurrentStroke();
        for (int i = 0; i < points.size() - 2; i++) {
            canvas.drawLine(points.get(i).x, points.get(i).y, points.get(i+1).x, points.get(i+1).y, paint);
        }

        Paint tempP = new Paint(paint);
        for(ColoredStroke cs : allStrokes) {
            tempP.setColor(cs.color);
            for (int i = 0; i < cs.stroke.size() - 2; i++) {
                canvas.drawLine(cs.stroke.get(i).x, cs.stroke.get(i).y, cs.stroke.get(i+1).x, cs.stroke.get(i+1).y, tempP);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            if (mStrokeManager.getCurrentStroke().size() > 0) {
                allStrokes.add(new ColoredStroke(new ArrayList<CanvasPoint>(mStrokeManager.getCurrentStroke()), paint.getColor()));
                Log.d(TAG, "drawn stroke added true new size:" + allStrokes.size());
            } else {
                Log.d(TAG, "drawn stroke added false old size:"  + allStrokes.size());
            }
        }
        mStrokeManager.onTouch(event);

        Log.d(TAG, "tap strokemanager type = " + mStrokeManager.getStrokeType().name());

        switch (mStrokeManager.getStrokeType()) {
            case TBD:
                break;
            case ZOOM_IN:
                Graph.setScalingFactor((float) mStrokeManager.getZoomFactor());
//                Log.d(TAG, "tryDetectZoom onTouch zoom_in");
                break;
            case ZOOM_OUT:
                Graph.setScalingFactor((float) mStrokeManager.getZoomFactor());
//                Log.d(TAG, "tryDetectZoom onTouch zoom_out");
                break;
            case PAN:
                CanvasPoint panOffset = mStrokeManager.getPanOffset();
                org.x -= panOffset.x;
                org.y -= panOffset.y;
                break;
            case TAP:
                Log.d(TAG, "getNodeOverlappingPoint handleTap");
                handleTap(mStrokeManager.getTapPoint());
                break;
        }

        invalidate();
        return true;
    }

    private void handleTap(CanvasPoint tapPoint) {
        GraphNode touchedNode = graph.getNodeOverlappingPoint(tapPoint);
        if (touchedNode != null) {
            Log.e(TAG, "Touched node tap: " + touchedNode.label);
            if (touchedNode.isSelected()) {
                graph.removeNode(touchedNode.label);
                graph.removePrompts();
            } else if (touchedNode.isPrompt()) {
                touchedNode.setPrompt(false);
                graph.deselectNodes();
                graph.removePrompts();
            } else {
                graph.deselectNodes();
                graph.removePrompts();
                touchedNode.setSelected(true);
            }
        } else {
            Log.e(TAG, "No overlapping node found for touch. tap");
            graph.deselectNodes();
            graph.removePrompts();
            graph.addNode(graph.getNextLabel(), tapPoint);
        }
    }
}