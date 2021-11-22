package com.example.dsdraw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dsdraw.structures.CanvasPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import top.defaults.colorpicker.ColorPickerPopup;

public class TreeDraw extends RelativeLayout implements View.OnTouchListener {
    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float startX;
    private float stopY;
    private float stopX;
    public static float hx1, hx2, hy1, hy2;
    private static final int THRESHOLD = 100;
    public static int numNodes = 1;
    public static int addNodes = 0;
    public static int deleteNodes = 0;
    public static boolean animate = false;
    public static int highlightBox, mDefaultColor;

    private Button mPickColorButton;

    List<Character> list1Nodes = new ArrayList<>();
    List<Character> list2Nodes = new ArrayList<>();
    List<CanvasPoint> points = new ArrayList<>();
    List<CanvasPoint> curStroke = new ArrayList<>();
    List<ColoredStroke> allStrokes = new ArrayList<>();

    Context c;
    Paint paint = new Paint();
    Paint myPaint = new Paint();
    Paint arrowPaint = new Paint();
    Paint tentPaint = new Paint();
    Paint highlightPaint = new Paint();
    public Paint writePaint = new Paint();

    Button addNodeButton, deleteNodeButton, llTraversal;

    public TreeDraw(final Context context, AttributeSet attrs) {
        super(context);
        inflate(context, R.layout.treedraw, this);
        this.setWillNotDraw(false);
        addNodeButton = (Button) findViewById(R.id.node_increment);
        deleteNodeButton = (Button) findViewById(R.id.node_decrement);
        llTraversal = (Button) findViewById(R.id.ll_animate);

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


        this.setOnTouchListener(this);
        paint.setColor(Color.BLACK);
        c = context;
        for(char ch='A'; ch<'Z'; ch++)
            list1Nodes.add(ch);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numNodes++;
                invalidate();
            }
        });

        deleteNodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                numNodes--;
                invalidate();
            }
        });

        llTraversal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                animate = true;
                highlightBox = 0;
                hx1 = 100+(100*highlightBox);
                hx2 = hx1+100; hy1 = 200; hy2 = 300;
                invalidate();
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        float x1 = 100, y1 = 200, x2 = 200, y2 = 300;
        myPaint.setColor(Color.rgb(0, 0, 0));

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(10);

        arrowPaint.setColor(Color.rgb(0, 0, 0));
        arrowPaint.setStrokeWidth(10);

        tentPaint.setColor(Color.rgb(211,211,211));
        tentPaint.setStyle(Paint.Style.STROKE);
        tentPaint.setStrokeWidth(10);

        writePaint.setColor(Color.BLACK);
        writePaint.setTextSize(70);
        writePaint.setStrokeWidth(10);

        highlightPaint.setColor(Color.rgb(0, 255, 0));
        highlightPaint.setStrokeWidth(10);

        canvas.drawRect(x1, y1, x2, y2, myPaint);
        canvas.drawText(list1Nodes.get(0)+"", x1+25, y2-25, writePaint);

        x1 += 200; x2 += 200;
        for(int i=0; i<numNodes-deleteNodes-1; i++) {
            canvas.drawLine(x1-100, (y1+y2)/2, x1, (y1+y2)/2, myPaint);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(x1-30, ((y1+y2)/2)+25);
            path.lineTo(x1-30,((y1+y2)/2)-25);
            path.lineTo(x1,((y1+y2)/2));
            path.close();
            canvas.drawPath(path, arrowPaint);
            canvas.drawRect(x1, y1, x2, y2, myPaint);
            writePaint.setColor(Color.BLACK);
            writePaint.setTextSize(70);
            writePaint.setStrokeWidth(10);
            canvas.drawText(list1Nodes.get(i+1)+"", x1+25, y2-25, writePaint);
            x1 += 200; x2 += 200;
        }
        for(int i=0; i<addNodes; i++) {
            canvas.drawLine(x1-100, (y1+y2)/2, x1, (y1+y2)/2, tentPaint);
            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(x1-30, ((y1+y2)/2)+25);
            path.lineTo(x1-30,((y1+y2)/2)-25);
            path.lineTo(x1,((y1+y2)/2));
            path.close();
            canvas.drawPath(path, tentPaint);
            canvas.drawRect(x1, y1, x2, y2, tentPaint);
            writePaint.setColor(Color.rgb(211,211,211));
            writePaint.setTextSize(70);
            writePaint.setStrokeWidth(10);
            canvas.drawText(list1Nodes.get(numNodes+i)+"", x1+25, y2-25, writePaint);
            x1 += 200; x2 += 200;
        }
        if(list2Nodes.size()>0)
        {
            float list2X1, list2X2, list2Y1, list2Y2;
            list2X1 = 100; list2Y1 = 400; list2X2 = 200; list2Y2 = 500;
            canvas.drawRect(list2X1, list2Y1, list2X2, list2Y2, myPaint);
            canvas.drawText(list2Nodes.get(0)+"", list2X1+25, list2Y2-25, writePaint);

            list2X1 += 200; list2X2 += 200;
            for(int i=0; i<list2Nodes.size()-1; i++) {
                canvas.drawLine(list2X1-100, (list2Y1+list2Y2)/2, list2X1, (list2Y1+list2Y2)/2, myPaint);
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(list2X1-30, ((list2Y1+list2Y2)/2)+25);
                path.lineTo(list2X1-30,((list2Y1+list2Y2)/2)-25);
                path.lineTo(list2X1,((list2Y1+list2Y2)/2));
                path.close();
                canvas.drawPath(path, arrowPaint);
                canvas.drawRect(list2X1, list2Y1, list2X2, list2Y2, myPaint);
                writePaint.setColor(Color.BLACK);
                writePaint.setTextSize(70);
                writePaint.setStrokeWidth(10);
                canvas.drawText(list2Nodes.get(i+1)+"", list2X1+25, list2Y2-25, writePaint);
                list2X1 += 200; list2X2 += 200;
            }
        }
        if(curStroke.size()>2) {
            for (int i = 0; i < curStroke.size() - 2; i++) {
                canvas.drawLine(curStroke.get(i).x, curStroke.get(i).y, curStroke.get(i+1).x, curStroke.get(i+1).y, paint);
            }
        }
        for (ColoredStroke thisStroke : allStrokes) {
            List<CanvasPoint> strokePoints = thisStroke.stroke;
            Paint strokePainter = new Paint();
            strokePainter.setColor(thisStroke.color);
            strokePainter.setStrokeWidth(10);

            for (int i = 0; i < strokePoints.size() - 2; i++) {
                canvas.drawLine(strokePoints.get(i).x, strokePoints.get(i).y, strokePoints.get(i+1).x, strokePoints.get(i+1).y, strokePainter);
            }
        }
        if(animate)
        {
            if(highlightBox == numNodes){
                highlightBox = 0;
                animate = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                canvas.drawRect(hx1, hy1, hx2, hy2, highlightPaint);
                writePaint.setColor(Color.BLACK);
                writePaint.setTextSize(70);
                writePaint.setStrokeWidth(10);
                canvas.drawText(list1Nodes.get(highlightBox)+"", hx1+25, hy2-25, writePaint);
                highlightBox++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hx1 += 200; hx2 = hx1+100;
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        CanvasPoint point;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                point = new CanvasPoint();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                point = new CanvasPoint();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
                List<CanvasPoint> strokeInfo = new ArrayList<>();
                for(CanvasPoint strokePoint : curStroke)
                    strokeInfo.add(strokePoint);
                ColoredStroke thisStroke = new ColoredStroke(strokeInfo, mDefaultColor);
                if(strokeInfo.size()>2)
                    allStrokes.add(thisStroke);
                invalidate();
                curStroke.clear();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // This happens when you touch the screen with two fingers
                mode = SWIPE;
                startY = event.getY(0);
                startX = event.getX(0);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // This happens when you release the second finger
                mode = NONE;
                if (Math.abs(startX - stopX) > THRESHOLD && Math.abs(startY - stopY) < THRESHOLD) {
                    if (startX > stopX) {
                        // Swipe left
                        //Toast toast = Toast.makeText(c, "Double Finger Left Swipe", Toast.LENGTH_SHORT);
                        //toast.show();
                        numNodes = Math.max(1, numNodes-deleteNodes);
                        deleteNodes = 0;
                        invalidate();
                    } else {
                        //Swipe right
                        //Toast toast = Toast.makeText(c, "Double Finger Right Swipe", Toast.LENGTH_SHORT);
                        //toast.show();
                        numNodes += addNodes;
                        addNodes = 0;
                        invalidate();
                    }
                } else if (Math.abs(startY - stopY) > THRESHOLD && Math.abs(startX - stopX) < THRESHOLD) {
                    if (startY > stopY) {
                        // Swipe up
                        //Toast toast = Toast.makeText(c, "Double Finger Up Swipe", Toast.LENGTH_SHORT);
                        //toast.show();
                        if(allStrokes.size()>0)
                            allStrokes.remove(allStrokes.size()-1);
                        invalidate();
                    } else {
                        //Swipe down
                        float lastNodeX = 100+(200*numNodes);
                        if(stopX<=lastNodeX && stopX>=100) {
                            final int curNode = (int)Math.max(1, (Math.ceil((stopX-100)/200)));
                            String nodeName = list1Nodes.get(curNode-1)+"";
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                            String[] options = new String[]{"Delete Node "+nodeName, "Delete List from Node "+nodeName, "Make new list from Node "+nodeName};
                            alertDialog.setTitle("Actions for Node "+nodeName).
                                    setItems(options, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which == 0)
                                            {
                                                list1Nodes.remove(curNode-1);
                                                numNodes--;
                                                invalidate();
                                            }
                                            else if(which == 1) {
                                                numNodes = curNode - 1;
                                                invalidate();
                                            }
                                            else
                                            {
                                                int i;
                                                for(i=curNode-1; i<numNodes; i++)
                                                    list2Nodes.add(list1Nodes.get(i));
                                                for(i=curNode-1; i<numNodes; i++)
                                                    list1Nodes.remove(curNode-1);
                                                numNodes = curNode - 1;
                                                invalidate();
                                            }
                                        }
                                    });
                            alertDialog.show();
                        }
                    }
                }
                this.mode = NONE;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == SWIPE) {
                    stopY = event.getY(0);
                    stopX = event.getX(0);

                    if(stopX >= startX) {
                        deleteNodes = 0;
                        addNodes = (int) ((stopX - startX) / 100);
                    }
                    else {
                        addNodes = 0;
                        deleteNodes = (int) ((startX - stopX) / 100);
                    }
                    invalidate();
                }
                else
                {
                    point = new CanvasPoint();
                    point.x = event.getX();
                    point.y = event.getY();
                    curStroke.add(point);
                    invalidate();
                }
                break;
        }
        return true;
    }
}
