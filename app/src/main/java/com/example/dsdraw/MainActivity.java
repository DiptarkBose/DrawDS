package com.example.dsdraw;

import androidx.appcompat.app.AppCompatActivity;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    DrawableView drawableView;
    Button up, down, color, undo;
    DrawableViewConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawableView = (DrawableView) findViewById(R.id.paintView);
        up = (Button) findViewById(R.id.sizeUp);
        down = (Button) findViewById(R.id.sizeDown);
        color = (Button) findViewById(R.id.colorChange);
        undo = (Button) findViewById(R.id.undo);

        config = new DrawableViewConfig();
        Display display = getWindowManager(). getDefaultDisplay();
        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true); // If the view is bigger than canvas, with this the user will see the bounds (Recommended)
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1000);
        config.setCanvasWidth(display.getWidth());
        drawableView.setConfig(config);

        up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });

        down.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });

        color.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Random random = new Random();
                config.setStrokeColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });

        undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                drawableView.undo();
            }
        });
    }
}