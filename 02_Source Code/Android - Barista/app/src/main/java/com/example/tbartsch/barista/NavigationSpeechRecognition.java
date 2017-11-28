package com.example.tbartsch.barista;

/**
 * Created by tbartsch on 23.11.2017.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationSpeechRecognition extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_speech_recognition, container, false);
        return rootView;
    }
}
