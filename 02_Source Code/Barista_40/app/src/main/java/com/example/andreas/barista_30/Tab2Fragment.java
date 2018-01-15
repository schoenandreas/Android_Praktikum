package com.example.andreas.barista_30;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static android.app.Activity.RESULT_OK;

/**
 * Created by Andreas on 06.12.2017.
 */

public class Tab2Fragment extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView text;

    //Animation für die SpeechRecognition Acitivity. + ein Teil an "onClickFloat"
    private ConstraintLayout constraintLayout;
    private ConstraintSet constr = new ConstraintSet();
    private ImageView imgView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);
        //views Initialisieren
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.tab2layout);
        constr.clone(getActivity(), R.layout.tab2_fragment_transition);
        text = (TextView) view.findViewById(R.id.textView);
        FloatingActionButton buttonFloat = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        imgView = constraintLayout.findViewById(R.id.imgView);
        //FloatingActionButoon onCLickListener
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    displaySpeechRecognizer();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Catch listener displaySpeechRecognition", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Invoke Switch Button tobi
        final Switch switchModus = (Switch) view.findViewById(R.id.switchCommand);
        // Check the current state of the switch
        switchModus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (switchModus.isChecked() == true){
                        constraintLayout.setBackgroundColor(Color.LTGRAY);
                        switchModus.setText(R.string.manual);
                    } else if (switchModus.isChecked() != true){
                        constraintLayout.setBackgroundColor(Color.WHITE);
                        switchModus.setText(R.string.predefined);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Catch listener switchCommand", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    //erstellt Intent fuer Speechrecognotion
    private void displaySpeechRecognizer() {
        //Intent erstellen
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        //Intent starten
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        //falls keine App den Intent erfuellen kann wird auf App verwiesen, die den Intent erfuellen kann
        catch (ActivityNotFoundException e) {
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            Toast.makeText(getActivity(), "Catch startActivityFor Result", Toast.LENGTH_SHORT).show();
        }
    }


    //empfaengt Ergebnis von Speechrecognizer
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            //Liste mit allen ideen des Recognizer
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //Wahrscheinlichstes Ergebnis
            String spokenText = "";
            String[] commandArray = getResources().getStringArray(R.array.commands);
            String[] mappingKeyArray = getResources().getStringArray(R.array.mappingKey);
            String[] mappingValueArray = getResources().getStringArray(R.array.mappingValue);

            // Compare the 5 (Maximum for Speech Recognition!) best probabilities with the whole string array to increase the possibility to find the wished solution
            try {
                int i = 0;
                while (i < 5){
                    Log.w("app", results.get(i));
                    spokenText = results.get(0);
                    for (int j = 0; j < commandArray.length; j++) {
                        if (results.get(i).toLowerCase().contains(commandArray[j])) {
                            //Check der 5 Wahrscheinlichsten Ergebnisse
                            spokenText = results.get(i);
                            i = 5;
                            break;
                        }
                    }
                    if (i < 5) {
                        for (int j = 0; j < mappingKeyArray.length; j++) {
                            if (results.get(i).equalsIgnoreCase(mappingKeyArray[j])) {
                                //Check der 5 Wahrscheinlichsten Ergebnisse zum mappen
                                spokenText = mappingValueArray[j];
                                i = 5;
                                break;
                            }
                        }
                    }
                    i++;
                }
            } catch (Exception e) {
                Log.w("app", e);
            }

            //auf Bildschirm anzeigen & Sprachungenauigkeiten ausbessern
            text.setText(spokenText);
            //per BT senden
            sendKeyword(spokenText);

            //animation Button nach unten und Bild anzeigen
            TransitionManager.beginDelayedTransition(constraintLayout);
            constr.applyTo(constraintLayout);

        }
    }

    //Keyword wird an Arduino gesendet
    private void sendKeyword(String string) {
        string.toLowerCase();
        String msg = "";

        if (string.contains("search")) {
            if (string.contains(getResources().getString(R.string.Coke))){
                imgView.setImageResource(R.drawable.coca_cola);
                msg = "brown";
            } else if (string.contains(getResources().getString(R.string.Fanta))){
                imgView.setImageResource(R.drawable.fanta);
                msg = "yellow";
            } else if (string.contains(getResources().getString(R.string.Tomato))) {
                imgView.setImageResource(R.drawable.tomatensaft);
                msg = "red";
            } else if (string.contains(getResources().getString(R.string.Water))) {
                imgView.setImageResource(R.drawable.wasser);
                msg = "white";
            } else {
                Toast.makeText(getActivity(), "No valid drink found", Toast.LENGTH_SHORT).show();
                imgView.setImageResource(R.drawable.transparent);
            }
        } else if (string.contains("do")){
            if (string.contains(getResources().getString(R.string.Standard))){
                // imgView.setImageResource(R.drawable.);
                msg = "standard";
            } else if (string.contains(getResources().getString(R.string.Stretch))){
                // imgView.setImageResource(R.drawable.);
                msg = "stretch";
            } else if (string.contains(getResources().getString(R.string.Goodbye))) {
                // imgView.setImageResource(R.drawable.);
                msg = "goodbye";
            } else if (string.contains(getResources().getString(R.string.Hello))) {
                // imgView.setImageResource(R.drawable.);
                msg = "hello";
            } else if (string.contains(getResources().getString(R.string.Release))) {
                // imgView.setImageResource(R.drawable.);
                msg = "release";
            } else if (string.contains(getResources().getString(R.string.Grap))) {
                // imgView.setImageResource(R.drawable.);
                msg = "grap";
            } else {
                Toast.makeText(getActivity(), "No valid commando found", Toast.LENGTH_SHORT).show();
                imgView.setImageResource(R.drawable.transparent);
            }
        } else if (string.contains("joint")){
            String joint = "";
            String orientation = "";
            String degreeString;
            int degree = 0;

            if (string.contains("base")){
                joint = "base";
                Pattern p = Pattern.compile("-?\\d+");
                Matcher m = p.matcher(string);
                if (string.contains("left")){
                    orientation = "left";
                }

                if (m.find()){
                    degreeString = m.group();
                    degree = Integer.parseInt(degreeString);
                }

                if (degree < 180 || degree > 0){
                    msg = joint + "_" + degree + "/" + orientation + ".";
                }


                /*while (m.find()) {
                    System.out.println(m.group());
                }*/
            } else {
                Toast.makeText(getActivity(), "No valid manual commando found", Toast.LENGTH_SHORT).show();
                imgView.setImageResource(R.drawable.transparent);
            }
        }


        /*
        //aus Speechrecognizer ergebnis das keyword suchen

        // Check if it is a manual assignment on "Joint" directly
        if (string.toLowerCase().contains(getString(R.string.Joint))){
            // Cut the string to string without the keyword "Joint" to further work with the string
            substring = getString(R.string.Joint);
            string = string.substring(string.indexOf(substring) + substring.length());

            // Braccio is manually addressed
            imgView.setImageResource(R.drawable.braccio);

            if (string.toLowerCase().contains(getString(R.string.Right))){
                // Cut the string to string without the keyword "Right" to further work with the string
                substring = getString(R.string.Right);
                string = string.substring(string.indexOf(substring) + substring.length());

                degree = Integer.parseInt(string);

            } else if (string.toLowerCase().contains(getString(R.string.Left))){
                // Cut the string to string without the keyword "Left" to further work with the string
                substring = getString(R.string.Left);
                string = string.substring(string.indexOf(substring) + substring.length());

                degree = Integer.parseInt(string);

            } else if (string.toLowerCase().contains(getString(R.string.Up))){
                // Cut the string to string without the keyword "Up" to further work with the string
                substring = getString(R.string.Up);
                string = string.substring(string.indexOf(substring) + substring.length());

                degree = Integer.parseInt(string);

            } else if (string.toLowerCase().contains(getString(R.string.Down))){
                // Cut the string to string without the keyword "Down" to further work with the string
                substring = getString(R.string.Down);
                string = string.substring(string.indexOf(substring) + substring.length());

                degree = Integer.parseInt(string);
            }

        }*/


        imgView.setVisibility(View.VISIBLE);
        //keyword per BT senden
        try {
            ((MainActivity) getActivity()).sendData(msg);
        } catch (Exception e) {
            Log.w("APP", e);
            Toast.makeText(getActivity(), "Send Failed", Toast.LENGTH_SHORT).show();
        }

    }

}