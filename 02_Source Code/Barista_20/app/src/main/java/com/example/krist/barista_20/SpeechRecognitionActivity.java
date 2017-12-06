package com.example.krist.barista_20;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

/**
 * Created by Krist on 06/12/2017.
 */

public class SpeechRecognitionActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView text;

    private float x0 = 0,x1 = 0,y0 = 0,y1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_speechrecognition);

        text = (TextView) findViewById(R.id.textView);


        FloatingActionButton buttonFloat = (FloatingActionButton) findViewById(R.id.floatingActionButton);





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
                    Intent intent = new Intent(this, DrinkOverviewActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter_from_right, R.anim.animation_leave_to_left);

                }else if (x1 - x0 > 100){ //rightswipe
                    Intent intent = new Intent(this, HelpActivity.class);
                    startActivity(intent);
                    this.overridePendingTransition(R.anim.animation_enter_from_left, R.anim.animation_leave_to_right);
                }
        }
        return false;
    }

    public void onClickFloat(View v){

        try {

            displaySpeechRecognizer();
        }catch(Exception e){

            Log.d("App","Exception",e);
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
// Start the activity, the intent will be populated with the speech text
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        catch(ActivityNotFoundException e){
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            Toast.makeText(SpeechRecognitionActivity.this, "Fehler", Toast.LENGTH_LONG).show();
        }
    }


    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            text.setText(spokenText);
        }

    }


}
