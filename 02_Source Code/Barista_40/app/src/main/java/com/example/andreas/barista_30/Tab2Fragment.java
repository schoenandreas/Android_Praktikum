package com.example.andreas.barista_30;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.widget.CompoundButton;
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
        final View view = inflater.inflate(R.layout.tab2_fragment, container, false);
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
                        alertModus(); // Invoke alert dialog tobi
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
            String[] commandArray = getResources().getStringArray(R.array.searchCommands);
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
        String btString ="";
        btString = string.toLowerCase();
        Log.w("String", btString);
        String msg = "";
        Resources res = getResources();
        String joint = "";
        String orientation = "";
        String degreeString;
        int degree = 0;
        final Switch commandSwitch = (Switch) getActivity().findViewById(R.id.switchCommand);

        if (btString.contains("search")){
            if(commandSwitch.isChecked()){
                alertModus();
                if(!commandSwitch.isChecked()){
                    return;
                }
            }


            Log.w("String", "contains search");
            for (int i = 0; i < res.getStringArray(R.array.searchCommands).length; i++){
                if (btString.contains(res.getStringArray(R.array.searchCommands)[i])){
                    TypedArray imgs = getResources().obtainTypedArray(R.array.searchCommandsImg);
                    msg = res.getStringArray(R.array.searchCommands)[i];
                    imgView.setImageResource(imgs.getResourceId(i, -1));
                    Log.w("Search", msg);
                    break;
                } else if (i == res.getStringArray(R.array.searchCommands).length-1){
                    Toast.makeText(getActivity(), "No valid drink found", Toast.LENGTH_SHORT).show();
                    Log.w("Search", "No valid drink found");
                    imgView.setImageResource(R.drawable.transparent);
                }
            }
        } else if (btString.contains("do")){
            if(commandSwitch.isChecked()){
                alertModus();
                if(!commandSwitch.isChecked()){
                    return;
                }
            }
            for (int i = 0; i < res.getStringArray(R.array.doCommands).length; i++){
                if (btString.contains(res.getStringArray(R.array.doCommands)[i])){
                    msg = res.getStringArray(R.array.doCommands)[i];
                    imgView.setImageResource(res.obtainTypedArray(R.array.doCommandsImg).getResourceId(i, -1));
                    break;
                } else if (i == res.getStringArray(R.array.doCommands).length-1){
                    Toast.makeText(getActivity(), "No valid pattern found", Toast.LENGTH_SHORT).show();
                    Log.w("Do", "No valid pattern found");
                    imgView.setImageResource(R.drawable.transparent);
                }
            }
        } else if (btString.contains("move")){
            setModusOn();
            for (int i = 0; i < res.getStringArray(R.array.jointCommands).length; i++) {
                if (btString.contains(res.getStringArray(R.array.jointCommands)[i])) {
                    Pattern p = Pattern.compile("-?\\d+");
                    Matcher m = p.matcher(btString);

                    joint = res.getStringArray(R.array.jointCommands)[i];
                    imgView.setImageResource(res.obtainTypedArray(R.array.jointCommandsImg).getResourceId(i, -1));
                    Log.w("MoveJoint", joint);

                    if (m.find()) {
                        degreeString = m.group();
                        degree = Integer.parseInt(degreeString);
                        Log.w("MoveDegree", "Wert: " + degree);
                    }

                    for (int j = 0; j < res.getStringArray(R.array.orientationCommands).length; j++){
                        if (btString.contains(res.getStringArray(R.array.orientationCommands)[j])) {
                            orientation = res.getStringArray(R.array.orientationCommands)[i];
                            Log.w("MoveOrientation", orientation);
                            break;
                        }
                    }


                    /*
                    for (int j = 0; j < res.getStringArray(R.array.orientationCommandsHorizontal).length; j++) {
                        if (btString.contains(res.getStringArray(R.array.orientationCommandsHorizontal)[j])) {
                            if (joint.equalsIgnoreCase("base") || joint.equalsIgnoreCase("wrist")) {
                                orientation = res.getStringArray(R.array.orientationCommandsHorizontal)[j];
                                Log.w("MoveOrientation", orientation);
                                break;
                            } else {
                                Toast.makeText(getActivity(), "Orientation " + res.getStringArray(R.array.orientationCommandsHorizontal)[j] + " only for base and wrist", Toast.LENGTH_SHORT).show();
                                Log.w("MoveOrientation", "No valid orientation found1");
                            }
                        }
                    }
                    for (int j = 0; j < res.getStringArray(R.array.orientationCommandsVertical).length; j++) {
                        if (btString.contains(res.getStringArray(R.array.orientationCommandsVertical)[j])) {
                            if (joint.equalsIgnoreCase("shoulder") || joint.equalsIgnoreCase("underarm") || joint.equalsIgnoreCase("elbow")) {
                                orientation = res.getStringArray(R.array.orientationCommandsVertical)[j];
                                Log.w("MoveOrientation", orientation);
                                break;
                            } else {
                                Toast.makeText(getActivity(), "Orientation " + res.getStringArray(R.array.orientationCommandsHorizontal)[j] + " only for shoulder, elbow and underarm", Toast.LENGTH_SHORT).show();
                                Log.w("MoveOrientation", "No valid orientation found2");
                            }
                        }
                    }
                    for (int j = 0; j < res.getStringArray(R.array.orientationCommandsContract).length; j++){
                        if (btString.contains(res.getStringArray(R.array.orientationCommandsContract)[j])) {
                            if (joint.equalsIgnoreCase("gripper")) {
                                orientation = res.getStringArray(R.array.orientationCommandsContract)[j];
                                Log.w("MoveOrientation", orientation);
                                break;
                            } else {
                                Toast.makeText(getActivity(), "Orientation " + res.getStringArray(R.array.orientationCommandsHorizontal)[j] + " only for gripper", Toast.LENGTH_SHORT).show();
                                Log.w("MoveOrientation", "No valid orientation found3");
                            }
                        }
                    }*/

                    if (orientation.isEmpty()) {
                        msg = joint + "_" + degree + ".";
                    } else if (!orientation.isEmpty()){
                        msg = joint + "_" + degree + "/" + orientation + ".";
                    }
                } else if (i == res.getStringArray(R.array.jointCommands).length) {
                    Toast.makeText(getActivity(), "No manual command found", Toast.LENGTH_SHORT).show();
                    Log.w("Move", "Not found");
                    imgView.setImageResource(R.drawable.transparent);
                }
            }
        }

        imgView.setVisibility(View.VISIBLE);
        //keyword per BT senden
        try {
            Log.w("Final message", msg);
            if (degree < 181 && degree >= 0){ //Always 0 for search and do
                ((MainActivity) getActivity()).sendData(msg);
            } else {
                Toast.makeText(getActivity(), "Degree must be between 0 and 180", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w("APP", e);
            Toast.makeText(getActivity(), "Send Failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected void setModusOn (){
        final Switch switchModus = (Switch) getActivity().findViewById(R.id.switchCommand);
        switchModus.setChecked(true);
        switchModus.setText(R.string.manual);

    }

    protected void setModusOff (){
        final Switch switchModus = (Switch) getActivity().findViewById(R.id.switchCommand);
        switchModus.setChecked(false);
        switchModus.setText(R.string.predefined);
    }

    protected void alertModus (){
        //Alert
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        setModusOff(); //Yes button clicked than continue
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        setModusOn(); //No button clicked
                        break;
                }
            }
        };

        // Alternativ nur context
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Question").setMessage("You are about to leave the manual modus. Do you really want to change modus?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
}