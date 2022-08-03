package com.example.synclibraries2.Download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
        public TextView getSize() {
            return textView;
        }
        public TextView getSite() {
            return textView;
        }
        public TextView getSeeder() {
            return textView;
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
            viewHolder.getSite().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSite());
            viewHolder.getSeeder().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSeeder());

        }


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