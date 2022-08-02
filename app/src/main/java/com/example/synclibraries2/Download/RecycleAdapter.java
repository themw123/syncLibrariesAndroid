package com.example.synclibraries2.Download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.squareup.picasso.Picasso;

import java.util.Vector;

import syncLibraries.Download;
import syncLibraries.JustWatch;
import syncLibraries.Qbittorrent;
import syncLibraries.Session;
import syncLibraries.Snowfl;
import syncLibraries.SyncLibrary;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private int position;
    private Download download;
    private Vector<String> downloaded;
    private Vector<Qbittorrent> downloading;
    private Vector<Snowfl> search;

    public RecycleAdapter(int position) {
        this.position = position;
        this.download = null;
        this.downloaded = null;
        this.downloading = null;
        this.search = null;

    }

    public void refreshAdapter(Download download) {
        this.download = download;
        this.downloaded = download.getDownloaded();
        this.downloading = download.getDownloading();
        Snowfl s = new Snowfl("hi", "h", "hj", "jj", 1);
        this.search = download.getSearch();
        this.search.add(s);
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

    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.download_row, viewGroup, false);

        return new ViewHolder(view);
    }




    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);

        //text setzen
        //viewHolder.getTextView().setText(jwv.get(viewHolder.getAdapterPosition()).getTitle());
        if(this.position == 0) {
            viewHolder.getTextView().setText(search.get(viewHolder.getAdapterPosition()).getName());
        }


    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //return localDataSet.length;
        //return jwv.size();
        int i = 0;


        if(position == 0) {
            if(search == null) {
                return 0;
            }
           i = search.size();
        }
        else if(position == 1) {
            if(downloading == null) {
                return 0;
            }
            i = downloading.size();
        }
        else if(position == 2) {
            if(downloaded == null) {
                return 0;
            }
            i = downloaded.size();
        }
        else {
            i = 0;
        }
        return i;
    }


    public void test() {
        //this.search = search[]
        notifyDataSetChanged();
        String test = "";
    }

}