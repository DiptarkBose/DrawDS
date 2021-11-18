package com.example.dsdraw;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button openTreeCanvas = this.findViewById(R.id.treeCanvasButton);
    Button openArrayCanvas = this.findViewById(R.id.arrayCanvasButton);
    Button openLLCanvas = this.findViewById(R.id.llCanvasButton);

    DrawingCanvas drawingCanvas;
    ArrayDraw arrayDraw;
    LLDraw llDraw;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.setContentView(R.layout.activity_main);

        openTreeCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingCanvas = new DrawingCanvas(mContext);
                setContentView(drawingCanvas);
                drawingCanvas.requestFocus();
            }
        });

        openArrayCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayDraw = new ArrayDraw(mContext);
                setContentView(arrayDraw);
                arrayDraw.requestFocus();
            }
        });

        openLLCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDraw = new LLDraw(mContext);
                setContentView(llDraw);
                llDraw.requestFocus();
            }
        });

    }


}