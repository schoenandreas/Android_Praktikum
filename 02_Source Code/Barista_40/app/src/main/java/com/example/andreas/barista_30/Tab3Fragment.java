package com.example.andreas.barista_30;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
    import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andreas.barista_30.DrinkList;
import com.example.andreas.barista_30.R;

import java.util.ArrayList;



public class Tab3Fragment extends Fragment {

    //dialog Variablen
    Dialog myDialog;
    TextView dialogDrinkName;
    TextView dialogdescription;
    ImageView img;


    ArrayList<DrinkList> listitems = new ArrayList<>();
    RecyclerView MyRecyclerView;
    String Drinks[] = {"Coca-Cola","Fanta","Tomatensaft","Wasser"};
    int  Images[] = {R.drawable.coca_cola,R.drawable.fanta,R.drawable.tomatensaft,R.drawable.wasser};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        //arraylist initialisieren
        super.onCreate(savedInstanceState);
        initializeList();
        getActivity().setTitle("DrinkList");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3_fragment, container, false);

        //RecyclerView binden
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);

        //Manager erstellen, um die Position der Daten während der Scrolling zu kontrollieren
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        //popup initialisieren
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.tab3_dialog);

        //view initialiseren
        dialogDrinkName = (TextView) myDialog.findViewById(R.id.dialogDrinkName);
        dialogdescription = (TextView) myDialog.findViewById(R.id.dialogDescription);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    //adapter um die Recyclerview und die Daten zu verbinden
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<DrinkList> list;

        public MyAdapter(ArrayList<DrinkList> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cardfragment, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        //ViewHolder wird mit die Daten von DrinkList verbunden
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {

            holder.titleTextView.setText(list.get(position).getCardName());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    //viewHolder für alle Views
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView coverImageView;
        public CardView card;


        public MyViewHolder(View v) {
            super(v);

            img = (ImageView) myDialog.findViewById(R.id.img);
            dialogDrinkName = (TextView) myDialog.findViewById(R.id.dialogDrinkName);
            dialogdescription = (TextView) myDialog.findViewById(R.id.dialogDescription);
            card = (CardView) v.findViewById(R.id.card1);
            titleTextView = (TextView) v.findViewById(R.id.txtcard);
            coverImageView = (ImageView) v.findViewById(R.id.imagecrd);
            coverImageView.setOnClickListener(new View.OnClickListener() {

                //onClick von listener wenn auf cards geklickt wird
                @Override
                public void onClick(View v) {


                    int id = (int)coverImageView.getTag();

                    //dialog befüllen je nachdem auf welche card geklickt wurde
                    if( id == R.drawable.coca_cola){

                        dialogdescription.setText("Coca Cola Description");
                        dialogDrinkName.setText("Coca Cola");
                        img.setTag(R.drawable.coca_cola);
                        img.setImageResource(R.drawable.coca_cola);

                    }else if (id==R.drawable.fanta){

                        dialogdescription.setText("Fanta Description");
                        dialogDrinkName.setText("Fanta");
                        img.setTag(R.drawable.fanta);
                        img.setImageResource(R.drawable.fanta);
                    }else if (id==R.drawable.tomatensaft){

                        dialogdescription.setText("Tomatensaft Description");
                        dialogDrinkName.setText("Tomatensaft");
                        img.setTag(R.drawable.tomatensaft);
                        img.setImageResource(R.drawable.tomatensaft);
                    }else if (id==R.drawable.wasser){

                        dialogdescription.setText("Wasser Description");
                        dialogDrinkName.setText("Wasser");
                        img.setTag(R.drawable.wasser);
                        img.setImageResource(R.drawable.wasser);
                    }

                    myDialog.show();

                }
            });
        }
    }

    public void initializeList() {
        listitems.clear();

        //for every Drink added, i musst be incremented

        for(int i =0;i<4;i++){


            DrinkList item = new DrinkList();
            item.setCardName(Drinks[i]);
            item.setImageResourceId(Images[i]);
            listitems.add(item);

        }
    }
}