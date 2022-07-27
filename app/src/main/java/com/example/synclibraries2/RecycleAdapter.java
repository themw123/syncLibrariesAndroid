package com.example.synclibraries2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import syncLibraries.JustWatch;
import syncLibraries.Session;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private Session s;
    private Vector<String[]> ausnahmen;
    private Vector<JustWatch> jwv;

    public RecycleAdapter(MainActivity2 mainActivity2) {
        s = new Session();
        this.ausnahmen = new Vector<String[]>();
        this.jwv = new Vector<JustWatch>();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public Switch sw;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            sw = (Switch) view.findViewById(R.id.switch1);
        }

        public TextView getTextView() {
            return textView;
        }
        public Switch getSwitch() {
            return sw;
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

    public void refreshListe(Vector<JustWatch> jwv, Vector<String[]> ausnahmen) {
        this.jwv = jwv;
        this.ausnahmen = ausnahmen;
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
        for(String[] s : ausnahmen) {
            String tmdbAusnahme = s[1];
            if(tmdbAusnahme.equals(tmdb)) {
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
                    addData(title, tmdb);
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
            if(ausnahmen.get(i)[1].equals(tmdb)) {
                index = i;
            }
        }
        return index;
    }

    public void addData(String titel, String tmdb) {
        Thread t1 = new Thread(() -> {
            s.addAusnahme(titel, tmdb);
            String[] neu = {titel, tmdb};
            ausnahmen.add(neu);
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