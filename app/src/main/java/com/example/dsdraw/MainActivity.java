package com.example.dsdraw;

        import androidx.appcompat.app.AppCompatActivity;
        import me.panavtec.drawableview.DrawableView;
        import me.panavtec.drawableview.DrawableViewConfig;

        import android.graphics.Color;
        import android.os.Bundle;
        import android.view.Display;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.NumberPicker;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}