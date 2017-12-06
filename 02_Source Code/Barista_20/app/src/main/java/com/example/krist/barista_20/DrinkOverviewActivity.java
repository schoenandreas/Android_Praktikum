package com.example.krist.barista_20;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

/**
 * Created by Krist on 06/12/2017.
 */

public class DrinkOverviewActivity extends AppCompatActivity {

    private float x0 = 0,x1 = 0,y0 = 0,y1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinkoverview);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x0 = event.getX();
                y0 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x1 = event.getX();
                y1 = event.getY();
                if(x0 - x1 > 100){ //leftswipe

                }else if (x1 - x0 > 100){ //rightswipe
                    Intent intent = new Intent(this, SpeechRecognitionActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter_from_left, R.anim.animation_leave_to_right);

                }
        }
        return false;
    }

}
