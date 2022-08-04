package com.example.synclibraries2.Download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.synclibraries2.R;

import syncLibraries.Download;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private int position;
    private Download download;

    public RecycleAdapter(int position) {
        this.position = position;
        this.download = null;
    }

    public void refreshAdapter(Download download) {
        this.download = download;
    }



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView site;
        private final TextView size;
        private final TextView seeder;
        private ImageButton button;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
            site = (TextView) view.findViewById(R.id.site);
            size = (TextView) view.findViewById(R.id.size);
            seeder = (TextView) view.findViewById(R.id.seeder);
            button = (ImageButton) view.findViewById(R.id.imageButton);
        }

        public TextView getTextView() {
            return textView ;
        }
        public TextView getSize() {
            return site;
        }
        public TextView getSite() {
            return size;
        }
        public TextView getSeeder() {
            return seeder;
        }
        public ImageButton getButton() {
            return button;
        }


    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.download_row1, viewGroup, false);
        return new ViewHolder(view);
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        //text setzen
        if(this.position == 0) {
            viewHolder.getTextView().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getName());
            viewHolder.getSite().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSite());
            viewHolder.getSize().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSize());
            viewHolder.getSeeder().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSeeder());
        }

        //wenn download geklickt wird
        viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String magnet = download.getSearch().get(viewHolder.getAdapterPosition()).getMagnet();
                //download...
            }
        });


    }





    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int i = 0;


        if(position == 0) {
           i = download.getSearch().size();
        }
        else if(position == 1) {
            i = download.getDownloading().size();
        }
        else if(position == 2) {
            i = download.getDownloaded().size();
        }
        else {
            i = 0;
        }
        return i;
    }



}