package com.example.krist.barista_20;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Andreas on 06.12.2017.
 */

public class MyGestureListener implements GestureDetector.OnGestureListener {

    private Context context;

    public MyGestureListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
        //left
        if(e1.getX() - e2.getX() > 50){
            Log.d("App","LEFT");

            Toast.makeText(, "Fehler", Toast.LENGTH_LONG).show();
            return true;
        }
        //right
        if(e2.getX() - e1.getX() > 50){

            Log.d("App","RIGHT");
            return true;
        }
        return false;
    }
}
