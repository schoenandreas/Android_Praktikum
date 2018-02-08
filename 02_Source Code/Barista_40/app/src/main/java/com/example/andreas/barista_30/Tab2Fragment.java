package com.example.andreas.barista_30;


import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
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
    private Switch switchMode;

    //Animation for SpeechRecognition Activity. + one part "onClickFloat"
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet = new ConstraintSet();
    private ImageView imgView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab2_fragment, container, false);
        //initialise views
        constraintLayout = view.findViewById(R.id.tab2layout);
        constraintSet.clone(getActivity(), R.layout.tab2_fragment_transition);
        text =  view.findViewById(R.id.textView);
        FloatingActionButton buttonFloat = view.findViewById(R.id.floatingActionButton);
        imgView = constraintLayout.findViewById(R.id.imgView);
        //FloatingActionButton onCLickListener
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    displaySpeechRecognizer();
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Catch listener displaySpeechRecognition", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Invoke Switch Button
        switchMode = view.findViewById(R.id.switchCommand);
        // Check the current state of the switch
        switchMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (switchMode.isChecked() == true) {
                        constraintLayout.setBackgroundColor(Color.LTGRAY);
                        switchMode.setText(R.string.manual);
                    } else if (switchMode.isChecked() != true) {
                        //alertMode(); // Invoke alert dialog
                        constraintLayout.setBackgroundColor(Color.WHITE);
                        switchMode.setText(R.string.predefined);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Catch listener switchCommand", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    //create intent for speech recognition
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "");
        //Intent start
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        //if no suiting app is available redirect to download of an app
        catch (ActivityNotFoundException e) {
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
            Toast.makeText(getActivity(), "Catch startActivityFor Result", Toast.LENGTH_SHORT).show();
        }
    }


    //receives result of Speechrecognizer
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            //result ideas
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //most likely result
            String spokenText = "";
            String[] searchCommandArray = getResources().getStringArray(R.array.searchCommands);
            String[] doCommandArray = getResources().getStringArray(R.array.doCommands);
            String[] jointCommandArray = getResources().getStringArray(R.array.jointCommands);
            String[] mappingKeyArray = getResources().getStringArray(R.array.mappingKey);
            String[] mappingValueArray = getResources().getStringArray(R.array.mappingValue);
            int searchProbability = 0;
            int doProbability = 0;
            int manualProbability = 0;

            // Compare the 5 (Maximum for Speech Recognition!) best probabilities with the whole string array to increase the possibility to find the wished solution
            try {
                for (int i = 0; i < 5; i++) {
                    spokenText = results.get(i).toLowerCase();
                    Log.w("app", spokenText);

                    for (int j = 0; j < mappingKeyArray.length; j++) {
                        Log.w("mappingKeyArrayText", results.get(i) + " / " + mappingKeyArray[j] + " / " + mappingValueArray[j]);
                        if (results.get(i).toLowerCase().contains(mappingKeyArray[j])) {
                            spokenText = spokenText.replace(mappingKeyArray[j], mappingValueArray[j]);
                            Log.w("mappingKeyArrayText", spokenText);
                        }
                    }

                    searchProbability = calculateProbability(spokenText, searchCommandArray);
                    doProbability = calculateProbability(spokenText, doCommandArray);
                    manualProbability = calculateProbability(spokenText, jointCommandArray);

                    Log.w("mappingKeyArrayProb", "sp " + searchProbability + "dp " + doProbability + "mp " + manualProbability);

                    if (searchProbability == 2 || doProbability == 2 || manualProbability == 2) {
                        Log.w("finalBreak", "baaam");
                        break;
                    }
                }
            } catch (Exception e) {
                Log.w("app", e);
            }

            //display on screen
            text.setText(spokenText);

            //animation and show picture
            TransitionManager.beginDelayedTransition(constraintLayout);
            constraintSet.applyTo(constraintLayout);

            // Check for changing from manual to predefined control
            if (switchMode.isChecked() == true && ((spokenText.contains("search") || spokenText.contains("do")))) {
                alertMode();
            } else if ((switchMode.isChecked() == false && ((spokenText.contains("search") || spokenText.contains("do")))) || (spokenText.contains("move"))) {
                sendKeyword(buildKeyword(text.getText().toString()));
            }
        }
    }

    private int calculateProbability (String spokenText, String [] commands){
        int probability = 0;
        if (spokenText.contains("search") || spokenText.contains("do") || spokenText.contains("move")) {
            probability = 1;
        }
        for (int j = 0; j < commands.length; j++) {
            if (spokenText.contains(commands[j])) {
                probability++;
                break;
            }
        }
        Log.w("probability", spokenText + " " + probability);
        return probability;
    }

    //Keyword building
    private String buildKeyword(String string) {
        String btString = "";
        btString = string.toLowerCase();
        Log.w("String", btString);
        String msg = "";
        Resources res = getResources();
        String joint = "";
        String orientation = "";
        String degreeString;
        int degree = 0;

        if (btString.contains("search")) {
            Log.w("String", "contains search");
            for (int i = 0; i < res.getStringArray(R.array.searchCommands).length; i++) {
                if (btString.contains(res.getStringArray(R.array.searchCommands)[i])) {
                    TypedArray imgs = getResources().obtainTypedArray(R.array.searchCommandsImg);
                    msg = res.getStringArray(R.array.searchCommands)[i];
                    imgView.setImageResource(imgs.getResourceId(i, -1));
                    Log.w("Search", msg);
                    break;
                } else if (i == res.getStringArray(R.array.searchCommands).length - 1) {
                    Toast.makeText(getActivity(), "No valid drink found", Toast.LENGTH_SHORT).show();
                    Log.w("Search", "No valid drink found");
                    imgView.setImageResource(R.drawable.transparent);
                }
            }
        } else if (btString.contains("do")) {
            for (int i = 0; i < res.getStringArray(R.array.doCommands).length; i++) {
                if (btString.contains(res.getStringArray(R.array.doCommands)[i])) {
                    msg = String.valueOf(i+1);
                    imgView.setImageResource(res.obtainTypedArray(R.array.doCommandsImg).getResourceId(i, -1));
                    break;
                } else if (i == res.getStringArray(R.array.doCommands).length - 1) {
                    Toast.makeText(getActivity(), "No valid pattern found", Toast.LENGTH_SHORT).show();
                    Log.w("Do", "No valid pattern found");
                    imgView.setImageResource(R.drawable.transparent);
                }
            }
        } else if (btString.contains("move")) {
            setModeOn();
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

                    for (int j = 0; j < res.getStringArray(R.array.orientationCommands).length; j++) {
                        if (btString.contains(res.getStringArray(R.array.orientationCommands)[j])) {
                            orientation = res.getStringArray(R.array.orientationCommands)[j];
                            Log.w("MoveOrientation", orientation);
                            break;
                        }
                    }

                    if (orientation.isEmpty()) {
                        msg = joint + "_" + degree + ".";
                    } else if (!orientation.isEmpty()) {
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

        return msg;
    }
    //send keyword
    private void sendKeyword(String msg){
        if (!msg.equalsIgnoreCase("")){
            try {
                Log.w("Final message", msg);
                ((MainActivity) getActivity()).sendData(msg);
            } catch (Exception e) {
                Log.w("APP", e);
                Toast.makeText(getActivity(), "Send Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "There is no command to send", Toast.LENGTH_SHORT).show();
        }
    }

    private void setModeOn() {
        final Switch switchMode = getActivity().findViewById(R.id.switchCommand);
        switchMode.setChecked(true);
        switchMode.setText(R.string.manual);
        constraintLayout.setBackgroundColor(Color.LTGRAY);
    }

    private void setModeOff() {
        final Switch switchMode = getActivity().findViewById(R.id.switchCommand);
        switchMode.setChecked(false);
        switchMode.setText(R.string.predefined);
        constraintLayout.setBackgroundColor(Color.WHITE);
    }

    protected void alertMode() {
        //Alert
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        setModeOff(); //Yes button clicked
                        try {
                            //bt send
                            sendKeyword(buildKeyword(text.getText().toString()));
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Sending didn't work!", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        setModeOn(); //No button clicked
                        text.setText("");
                        imgView.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };

        // Alternativ only context
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Question").setMessage("You are about to leave the manual modus. Do you really want to change modus?").setCancelable(false).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}