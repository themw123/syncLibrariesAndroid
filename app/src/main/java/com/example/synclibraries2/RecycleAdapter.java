package com.example.synclibraries2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import syncLibraries.JustWatch;
import syncLibraries.Session;
import syncLibraries.SyncLibrary;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private int position;
    private SyncLibrary sl;
    private Vector<String> ausnahmen;
    private Vector<JustWatch> jwv;
    private Session s;

    public RecycleAdapter(int position) {
        this.position = position;
        this.sl = null;
        this.ausnahmen = null;
        this.jwv = null;
        this.s = null;
    }

    public void refreshListe(SyncLibrary sl){
        this.sl = sl;
        this.ausnahmen = sl.getAusnahmen();
        this.jwv = filterTitel(sl);
        this.s = sl.getSession();

    }

    private Vector<JustWatch> filterTitel(SyncLibrary sl) {

        Vector<JustWatch> jwv = (Vector<JustWatch>) sl.getJustWatchWatchList().clone();
        
        //nur Serien
        if(position == 0) {
            for(int i=0;i<jwv.size();i++) {
                if(jwv.get(i).getType().equals("movie")) {
                    jwv.remove(i);
                    i--;
                }
            }
        }
        //nur Filme
        else if(position == 1) {
            for(int i=0;i<jwv.size();i++) {
                if(jwv.get(i).getType().equals("series")) {
                    jwv.remove(i);
                    i--;
                }
            }
        }



        return jwv;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Switch sw;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            sw = (Switch) view.findViewById(R.id.switch1);
            imageView = (ImageView) view.findViewById(R.id.imageView);
        }

        public TextView getTextView() {
            return textView;
        }
        public Switch getSwitch() {
            return sw;
        }
        public ImageView getImageView() {
            return imageView;
        }
    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);

        //text setzen
        viewHolder.getTextView().setText(jwv.get(viewHolder.getAdapterPosition()).getTitle());


        //switch state setzen
        String tmdb = jwv.get(viewHolder.getAdapterPosition()).getTmdb();
        boolean in = false;
        for(String tmdbAusnahm : ausnahmen) {
            if(tmdbAusnahm.equals(tmdb)) {
                in = true;
            }
        }
        if(in) {
            viewHolder.getSwitch().setChecked(true);
            in = false;
        }
        //wichtig
        else {
            viewHolder.getSwitch().setChecked(false);
        }


        //bild setzten
        Picasso.with(viewHolder.getImageView().getContext()).load("https://image.tmdb.org/t/p/original" + jwv.get(viewHolder.getAdapterPosition()).getTmdbPoster())
                .fit()
                .into(viewHolder.getImageView());






        //switch wechsel
        viewHolder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //wenn switch durch programm, nicht durch user gewechselt wird
                if(!viewHolder.getSwitch().isPressed()) {
                    return;
                }
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked) {
                    String title = jwv.get(viewHolder.getAdapterPosition()).getTitle();
                    String tmdb = jwv.get(viewHolder.getAdapterPosition()).getTmdb();
                    String imdb = jwv.get(viewHolder.getAdapterPosition()).getImdb();
                    addData(tmdb);
                }
                else {
                    String tmdb = jwv.get(viewHolder.getAdapterPosition()).getTmdb();
                    delData(tmdb);
                }
            }
        });

    }


    private int getIndexAusnahmen(String tmdb) {
        int index = -1;
        for(int i=0;i<ausnahmen.size();i++) {
            if(ausnahmen.get(i).equals(tmdb)) {
                index = i;
            }
        }
        return index;
    }

    public void addData(String tmdb) {
        Thread t1 = new Thread(() -> {
            s.addAusnahme(tmdb);
            ausnahmen.add(tmdb);
        });
        t1.start();
    }

    private void delData(String tmdb) {
        Thread t1 = new Thread(() -> {
            s.delAusnahme(tmdb);
            int index = getIndexAusnahmen(tmdb);
            ausnahmen.remove(index);
        });
        t1.start();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return jwv.size();
    }



}