package com.example.dsdraw;

        import androidx.appcompat.app.AppCompatActivity;

        import android.app.ActionBar;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.MenuItem;

public class LLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ll_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
