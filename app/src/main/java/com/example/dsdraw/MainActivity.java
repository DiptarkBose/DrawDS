package com.example.dsdraw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button arrayCanvas, llCanvas, treeCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llCanvas = (Button) findViewById(R.id.ll_canvas);
        treeCanvas = (Button) findViewById(R.id.tree_canvas);
        arrayCanvas = (Button) findViewById(R.id.array_canvas);

        arrayCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ArrayActivity.class);
                startActivity(intent);
            }
        });

        llCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LLActivity.class);
                startActivity(intent);
            }
        });

        treeCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TreeActivity.class);
                startActivity(intent);
            }
        });
    }
}