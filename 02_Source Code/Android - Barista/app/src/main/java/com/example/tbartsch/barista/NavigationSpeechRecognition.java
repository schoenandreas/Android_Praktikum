package com.example.tbartsch.barista;

/**
 * Created by tbartsch on 23.11.2017.
 */


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationSpeechRecognition extends Fragment {

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    TextView output;
    Button aufzeichnen;
    public static int SPEECH_REQUEST_CODE =0;
    private String uebergabe;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_speech_recognition, container, false);

        aufzeichnen = (Button)getView().findViewById(R.id.imageButton);
        output=(TextView)getView().findViewById(R.id.section_label2);

        return rootView;


    }

    public void onClick(View v)
    {

        displaySpeechRecognizer();
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
            Toast.makeText(NavigationActivity.this, "Fehler", Toast.LENGTH_LONG).show();
        }
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == ((NavigationActivity)getActivity()).RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            uebergabe = spokenText;
            output.setText(spokenText);
            try {
                sendDataVoice();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    void sendData() throws IOException
    {
        String msg = output.getText().toString();
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        aufzeichnen.setText("Data Sent");
    }

    void sendDataVoice() throws IOException
    {
        String msg = uebergabe;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        aufzeichnen.setText("Data Sent");
    }



}




