package com.example.andreas.barista_30;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Andreas on 06.12.2017.
 */

public class Tab3Fragment extends Fragment  implements View.OnClickListener {

    //popup
    Dialog myDialog;
    TextView txt;
    ImageView img;
    CardView card1;
    CardView card2;
    CardView card3;
    CardView card4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment, container, false);

        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.tab3_fragment_transition);

        txt=(TextView) myDialog.findViewById(R.id.txt);
        img=(ImageView) myDialog.findViewById(R.id.img);
        card1=(CardView) view.findViewById(R.id.card1);
        card2=(CardView) view.findViewById(R.id.card2);
        card3=(CardView) view.findViewById(R.id.card3);
        card4=(CardView) view.findViewById(R.id.card4);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);

        return view;
    }

    //popup
    public void onClick(View view){
         switch (view.getId()) {
                case R.id.card1:
                    txt.setText("Objekt1");
                    //img.
                    break;
                case R.id.card2:
                    txt.setText("Objekt2");
                    break;
                case R.id.card3:
                    txt.setText("Objekt3");
                    break;
                case R.id.card4:
                    txt.setText("Objekt4");
         }
         myDialog.show();
    }
}