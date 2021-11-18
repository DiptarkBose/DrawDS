package com.example.dsdraw;

import androidx.appcompat.app.AppCompatActivity;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dsdraw.structures.BinaryTree;
import com.example.dsdraw.structures.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button openDrawingCanvas;
    Button openPaintingCanvas;

    DrawingCanvas drawingCanvas;
//    PaintingCanvas paintingCanvas;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.setContentView(R.layout.activity_main);

        openDrawingCanvas = (Button)this.findViewById(R.id.drawingCanvasButton);
        openDrawingCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingCanvas = new DrawingCanvas(mContext);
                setContentView(drawingCanvas);
                drawingCanvas.requestFocus();
            }
        });

//        openPaintingCanvas = (Button)this.findViewById(R.id.paintingCanvasButton);
//        openPaintingCanvas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paintingCanvas = new PaintingCanvas(mContext);
//                setContentView(paintingCanvas);
//                paintingCanvas.requestFocus();
//            }
//        });


        // canvas = new LLDraw(this);
        // setContentView(canvas);
        // canvas.requestFocus();
        //canvas = (DrawingCanvas) findViewById(R.id.canvas);
    }


}