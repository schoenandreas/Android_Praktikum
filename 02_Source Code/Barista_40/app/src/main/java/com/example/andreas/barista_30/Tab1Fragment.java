package com.example.andreas.barista_30;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Andreas on 06.12.2017.
 */

public class Tab1Fragment extends Fragment {

    private View rootView;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // preparing list data
        prepareListData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab1_fragment, container, false);


        // get the listView
        expListView = rootView.findViewById(R.id.lvExp);
        listAdapter = new ExpandableListAdapter(listDataHeader, listDataChild, getActivity());
        // setting list adapter
        expListView.setAdapter(listAdapter);


        //listView Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });
        //listViewGroup expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
        //listView Group collapsed listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
        //listView on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
        // Gives the bluetooth Switch an OnClickListener
        final Switch bluetoothSwitchButton = rootView.findViewById(R.id.btSwitch);

        bluetoothSwitchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (bluetoothSwitchButton.isChecked()) {
                        //popup for bluetooth dialog initialisation
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.bluetoothDialog();

                        TextView switchItemText = rootView.findViewById(R.id.btSwitchItem);
                        switchItemText.setText(R.string.bt_enabled + "HMSoft");

                    } else if (!bluetoothSwitchButton.isChecked()) {
                        TextView switchItemText = rootView.findViewById(R.id.btSwitchItem);
                        switchItemText.setText(R.string.bt_disabled);
                    } else {
                        Toast.makeText(getActivity(), "Bluetooth Switch not working!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Catch listener bluetooth settings", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Braccio Introduction");
        listDataHeader.add("Braccio Commands");
        listDataHeader.add("FAQs");
        listDataHeader.add("About");


        // Adding child data
        List<String> introduction = new ArrayList<String>();
        introduction.add("The idea was to create a robotic barkeeper with the Arduino Braccio. " +
                "Therefor some drinks where realised to search for within the closed alpha. " +
                "Additionally the possibility to perform predefined patterns is given. " +
                "In case somebody wants to control single joints of the Braccio you can address them via absolute commands referring to the degree on the plate or via relative commands referring to the current position. " +
                "The absolute limitations for each joint are listed beneath. " +
                "\nBase: Allowed values from 0 to 180 degrees" +
                "\nShoulder: Allowed values from 15 to 165 degrees" +
                "\nElbow: Allowed values from 0 to 180 degrees" +
                "\nUnderarm: Allowed values from 0 to 180 degrees" +
                "\nWrist: Allowed values from 0 to 180 degrees" +
                "\nGrapper: Allowed values from 10 to 73 degrees (10: open / 73: closed)");

        List<String> commands = new ArrayList<String>();
        commands.add("PREDEFINED CONTROLS: Search" +
                "\nSearch Fanta " +
                "\nSearch Sport drink  " +
                "\nSearch Water " +
                "\nSearch Tomato juice ");
        commands.add("PREDEFINED CONTROLS: Pattern " +
                "\nDo Goodbye " +
                "\nDo Hello " +
                "\nDo Schnappi " +
                "\nDo Grap " +
                "\nDo Release ");
        commands.add("MANUAL CONTROLS: Absolute" +
                "\nMove Base -degree- " +
                "\nMove Shoulder -degree- " +
                "\nMove Elbow -degree- " +
                "\nMove Forearm -degree- " +
                "\nMove Wrist -degree- " +
                "\nMove Grapper -degree- ");
        commands.add("MANUAL CONTROLS: Relative" +
                "\nMove Base -degree- (right or left) " +
                "\nMove Shoulder -degree- (right or left) " +
                "\nMove Elbow -degree- (right or left) " +
                "\nMove Forearm -degree- (right or left) " +
                "\nMove Wrist -degree- (right or left) " +
                "\nMove Grapper -degree- (right or left)" +
                "\n\n Info: For the right point of view the base motor must be visible! ");

        List<String> faqs = new ArrayList<String>();
        faqs.add("Question: Why is the application so awesome? " +
                "\nBecause. Just because....");
        faqs.add("Question: Who are the geniuses behind this idea? " +
                "\nIt was developed by a bunch of medium skilled programmers who had nothing better to do with their live.");
        faqs.add("Question: Did you have problems creating this fantastic project? " +
                "\nThe project got bigger and bigger. On various times our lecturer came up with new stuff to develop without any logic.");
        faqs.add("Question: What will you develop next? " +
                "\nYou know... It has always been a dream of mine to develop a shopping app for horse riding equipment.");

        List<String> about = new ArrayList<String>();
        about.add("THIS PROJECT IN DATA");
        about.add("Total lines of code android: xxx " +
                "\nTotal lines of code arduino: xxx " +
                "\nTotal development time: 700h+" +
                "\nTotal developer count: 3 " +
                "\nTotal fun factor: Over 9,000");
        about.add("This project was a cooperation between: \nThe chair of operating systems (M. Sc. Sebastian Eckl) " +
                "\n& " +
                "\nThe Barrista Team (Andreas Schön, Krist Stoja & B. Sc. Tobias Bartsch)");


        listDataChild.put(listDataHeader.get(0), introduction); // Header, Child data
        listDataChild.put(listDataHeader.get(1), commands);
        listDataChild.put(listDataHeader.get(2), faqs);
        listDataChild.put(listDataHeader.get(3), about);
    }
}