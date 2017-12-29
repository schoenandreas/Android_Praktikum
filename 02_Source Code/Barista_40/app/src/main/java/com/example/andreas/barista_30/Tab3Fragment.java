package com.example.andreas.barista_30;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Andreas on 06.12.2017.
 */

public class Tab3Fragment extends Fragment implements View.OnClickListener {

    //dialog Variablen
    private Dialog myDialog;
    private TextView dialogDrinkName;
    private TextView dialogdescription;
    private ImageView img;
    //Cardviews variablen
    private CardView card1;
    private CardView card2;
    private CardView card3;
    private CardView card4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);

        //dialog erstellen
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.tab3_dialog);

        //view initialiseren
        dialogDrinkName = (TextView) myDialog.findViewById(R.id.dialogDrinkName);
        dialogdescription = (TextView) myDialog.findViewById(R.id.dialogDescription);
        img = (ImageView) myDialog.findViewById(R.id.img);
        card1 = (CardView) view.findViewById(R.id.card1);
        card2 = (CardView) view.findViewById(R.id.card2);
        card3 = (CardView) view.findViewById(R.id.card3);
        card4 = (CardView) view.findViewById(R.id.card4);
        //listener setzen
        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);

        return view;
    }

    //onClick von listener wenn auf cardview geklickt wird
    public void onClick(View view) {
        //je nachdem auf welche card geklickt wurde Bild und Text des Dialog befuellen
        switch (view.getId()) {
            case R.id.card1:
                dialogDrinkName.setText("Coca-Cola");
                dialogdescription.setText("Coca-Cola Beschreibung");
                img.setImageResource(R.drawable.coca_cola);
                break;
            case R.id.card2:
                dialogDrinkName.setText("Fanta");
                dialogdescription.setText("Fanta Beschreibung");
                img.setImageResource(R.drawable.fanta);
                break;
            case R.id.card3:
                dialogDrinkName.setText("");
                dialogdescription.setText("Objekt3");
                img.setImageResource(R.drawable.fanta);
                break;
            case R.id.card4:
                dialogDrinkName.setText("");
                dialogdescription.setText("Objekt4");
                img.setImageResource(R.drawable.fanta);
        }
        //dialog anzeigen
        myDialog.show();
    }
}