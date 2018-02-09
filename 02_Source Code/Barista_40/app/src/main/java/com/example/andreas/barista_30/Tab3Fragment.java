package com.example.andreas.barista_30;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



public class Tab3Fragment extends Fragment {

    //dialog variables
    private Dialog myDialog;
    private TextView dialogDrinkName;
    private TextView dialogDescription;
    private ImageView img;


    private ArrayList<DrinkList> listItems = new ArrayList<>();
    private RecyclerView MyRecyclerView;
    private String Drinks[] = {"Coke","Fanta","Tomato juice","Water"};
    private int  Images[] = {R.drawable.coca_cola,R.drawable.fanta,R.drawable.tomatensaft,R.drawable.wasser};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //initialise ArrayList
        super.onCreate(savedInstanceState);
        initializeList();
        getActivity().setTitle("DrinkList");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab3_fragment, container, false);

        //RecyclerView bind
        MyRecyclerView = view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);

        //Manager creation, to control data while scrolling
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listItems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listItems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        //popup initialisation
        myDialog = new Dialog(getActivity());
        myDialog.setContentView(R.layout.tab3_dialog);

        //view initialisation
        dialogDrinkName = myDialog.findViewById(R.id.dialogDrinkName);
        dialogDescription =  myDialog.findViewById(R.id.dialogDescription);

        //floating Button for google search
        FloatingActionButton floatingSearchButton = myDialog.findViewById(R.id.floating_search);
        floatingSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    floatSearch();
                }catch (Exception e){
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //adapter to connect RecyclerView and data
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<DrinkList> list;
        public MyAdapter(ArrayList<DrinkList> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardfragment, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        //ViewHolder connected with data of DrinkList
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


    //viewHolder for all views
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView coverImageView;
        public CardView card;


        public MyViewHolder(View v) {
            super(v);

            img =  myDialog.findViewById(R.id.img);
            dialogDrinkName =  myDialog.findViewById(R.id.dialogDrinkName);
            dialogDescription =  myDialog.findViewById(R.id.dialogDescription);
            card =  v.findViewById(R.id.card1);
            titleTextView = v.findViewById(R.id.txtcard);
            coverImageView =  v.findViewById(R.id.imagecrd);
            coverImageView.setOnClickListener(new View.OnClickListener() {

                //onClick if card are clicked
                @Override
                public void onClick(View v) {
                    int id = (int)coverImageView.getTag();

                    //dialog filling depending on which card was chosen
                    if( id == R.drawable.coca_cola){

                        dialogDescription.setText(R.string.colaDesc);
                        dialogDrinkName.setText("Coke");
                        img.setTag(R.drawable.coca_cola);
                        img.setImageResource(R.drawable.coca_cola);

                    }else if (id==R.drawable.fanta){

                        dialogDescription.setText(R.string.fantaDesc);
                        dialogDrinkName.setText("Fanta");
                        img.setTag(R.drawable.fanta);
                        img.setImageResource(R.drawable.fanta);
                    }else if (id==R.drawable.tomatensaft){

                        dialogDescription.setText(R.string.tomatoDesc);
                        dialogDrinkName.setText("Tomato juice");
                        img.setTag(R.drawable.tomatensaft);
                        img.setImageResource(R.drawable.tomatensaft);
                    }else if (id==R.drawable.wasser){

                        dialogDescription.setText(R.string.waterDesc);
                        dialogDrinkName.setText("Water");
                        img.setTag(R.drawable.wasser);
                        img.setImageResource(R.drawable.wasser);
                    }
                    myDialog.show();
                }
            });
        }
    }

    public void initializeList() {
        listItems.clear();
        //for every Drink added, i must be incremented
        for(int i =0;i<4;i++){
           DrinkList item = new DrinkList();
            item.setCardName(Drinks[i]);
            item.setImageResourceId(Images[i]);
            listItems.add(item);
        }
    }

    //google search
    public void floatSearch() {
        CharSequence drinkSearch=dialogDrinkName.getText();
        String url = "http://www.google.com/search?q="+drinkSearch;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}