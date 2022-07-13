package com.example.synclibraries2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import syncLibraries.Session;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private Session s;
    private Vector<String> ausnahmen;

    public RecycleAdapter(MainActivity2 mainActivity2) {
        s = new Session();
        this.ausnahmen = new Vector<String>();
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public ImageButton del;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            del = (ImageButton) view.findViewById(R.id.button);
        }

        public TextView getTextView() {
            return textView;
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

    public void refreshListe(Vector<String> ausnahmen) {
        this.ausnahmen = ausnahmen;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);
        viewHolder.getTextView().setText(ausnahmen.get(position));

        //Zeile löschen
        viewHolder.del.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView tw = viewHolder.getTextView();
                String titel = tw.getText().toString();
                delData(titel, viewHolder.getAdapterPosition());
            }
        });

    }

    public void addData(String titel, int position) {
        ausnahmen.add(position, titel);
        Thread t1 = new Thread(() -> {
            s.addAusnahme(titel);
        });
        t1.start();
        notifyItemInserted(position);
        notifyItemRangeChanged(position, ausnahmen.size());
    }

    private void delData(String titel, int position) {
        ausnahmen.remove(position);
        Thread t1 = new Thread(() -> {
            s.delAusnahme(titel);
        });
        t1.start();
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ausnahmen.size());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return ausnahmen.size();
    }



}