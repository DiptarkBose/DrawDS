package com.example.dsdraw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LLDraw extends RelativeLayout implements View.OnTouchListener {
    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float startX;
    private float stopY;
    private float stopX;
    private static final int THRESHOLD = 100;
    public static int numNodes = 1;
    public static int addNodes = 0;
    public static int deleteNodes = 0;

    List<Point> points = new ArrayList<>();
    List<Point> curStroke = new ArrayList<>();
    List<Character> list1Nodes = new ArrayList<>();
    List<Character> list2Nodes = new ArrayList<>();
    Context c;
    Paint paint = new Paint();
    Paint myPaint = new Paint();
    Paint arrowPaint = new Paint();
    Paint tentPaint = new Paint();
    Paint writePaint = new Paint();
    Button addNodeButton, deleteNodeButton;

    public LLDraw(Context context, AttributeSet attrs) {
        super(context);
        inflate(context, R.layout.lldraw, this);
        this.setWillNotDraw(false);
        addNodeButton = (Button) findViewById(R.id.node_increment);
        deleteNodeButton = (Button) findViewById(R.id.node_decrement);

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
        if(curStroke.size()>2) {
            for(Point point : curStroke)
                points.add(point);
        }
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, 20, paint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Point point;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                point = new Point();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                point = new Point();
                point.x = event.getX();
                point.y = event.getY();
                curStroke.add(point);
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
                        Toast toast = Toast.makeText(c, "Double Finger Left Swipe", Toast.LENGTH_SHORT);
                        toast.show();
                        numNodes = Math.max(1, numNodes-deleteNodes);
                        deleteNodes = 0;
                        invalidate();
                    } else {
                        //Swipe right
                        Toast toast = Toast.makeText(c, "Double Finger Right Swipe", Toast.LENGTH_SHORT);
                        toast.show();
                        numNodes += addNodes;
                        addNodes = 0;
                        invalidate();
                    }
                } else if (Math.abs(startY - stopY) > THRESHOLD && Math.abs(startX - stopX) < THRESHOLD) {
                    if (startY > stopY) {
                        // Swipe up
                        Toast toast = Toast.makeText(c, "Double Finger Up Swipe", Toast.LENGTH_SHORT);
                        toast.show();
                        points.clear();
                        invalidate();
                    } else {
                        //Swipe down
                        float lastNodeX = 100+(200*numNodes);
                        Toast toast = Toast.makeText(c, lastNodeX+" "+stopX, Toast.LENGTH_SHORT);
                        toast.show();
                        if(stopX<=lastNodeX && stopX>=100) {
                            final int curNode = (int)Math.max(1, (Math.ceil((stopX-100)/200)));
                            String nodeName = (char)(64+curNode)+"";
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
                            String[] options = new String[]{"Delete Node "+nodeName, "Delete List from Node "+nodeName, "Cut List from Node "+nodeName};
                            alertDialog.setTitle("Actions for Node "+curNode).
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
                    point = new Point();
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
