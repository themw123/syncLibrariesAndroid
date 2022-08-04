package com.example.synclibraries2.Download;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.synclibraries2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Vector;

import syncLibraries.Download;
import syncLibraries.Qbittorrent;

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
        private final TextView totalsize;
        private final TextView progress;
        private ImageButton button;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
            site = (TextView) view.findViewById(R.id.site);
            size = (TextView) view.findViewById(R.id.size);
            seeder = (TextView) view.findViewById(R.id.seeder);
            totalsize = (TextView) view.findViewById(R.id.totalsize);
            progress = (TextView) view.findViewById(R.id.progress);
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
        public TextView getTotalsize() {
            return totalsize;
        }
        public TextView getProgress() {
            return progress;
        }
        public ImageButton getButton() {
            return button;
        }


    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = null;

        if(position == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_row1, viewGroup, false);
        }
        else if(position == 1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_row2, viewGroup, false);
        }
        else if(position == 2) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_row3, viewGroup, false);
        }
        // Create a new view, which defines the UI of the list item
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

            //wenn download geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String magnet = download.getSearch().get(viewHolder.getAdapterPosition()).getMagnet();

                    Thread t = new Thread(() -> {

                        download.addDownloading(magnet);
                        download.setDownloading();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                RecycleAdapter adapter = RecycleFragment.getAdapter(1);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    });
                    t.start();
                }
            });
        }
        else if(this.position == 1) {
            viewHolder.getTextView().setText(download.getDownloading().get(viewHolder.getAdapterPosition()).getName());
            viewHolder.getTotalsize().setText(download.getDownloading().get(viewHolder.getAdapterPosition()).getTotal_size()+" GB");
            viewHolder.getProgress().setText(download.getDownloading().get(viewHolder.getAdapterPosition()).getProgress()+"%");

            //wenn stoppen geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String infohash_v1 = download.getDownloading().get(viewHolder.getAdapterPosition()).getInfohash();
                    download.getDownloading().remove(viewHolder.getAdapterPosition());
                    notifyItemRemoved(viewHolder.getAdapterPosition());

                    Thread t = new Thread(() -> {

                        download.delDownloading(infohash_v1);
                        download.setDownloading();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //um die realen daten zu erhalten und sicherzustellen das es gelöscht wurde
                                notifyDataSetChanged();
                            }
                        },1000);
                    });
                    t.start();
                }
            });




        }
        else if(this.position == 2) {
            viewHolder.getTextView().setText(download.getDownloaded().get(viewHolder.getAdapterPosition()));

            //wenn löschen geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    //dialog box
                    androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(v.getContext(), R.style.AlertDialogTheme)
                            .setMessage("Wirklich löschen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    delDownloaded(viewHolder);
                                }
                            })
                            .setNeutralButton("Nein", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //nichts
                                }
                            })
                            .show();



                }
            });
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



    private void delDownloaded(ViewHolder viewHolder) {
        int pos = viewHolder.getAdapterPosition();
        String title = download.getDownloaded().get(viewHolder.getAdapterPosition());
        download.getDownloaded().remove(viewHolder.getAdapterPosition());

        //nicht möglich wegen bekannten bug in recycler view
        //notifyItemRemoved(viewHolder.getAdapterPosition());
        notifyDataSetChanged();

        Thread t = new Thread(() -> {

            download.delDownloanded(title);
            download.setDownloaded();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //um die realen daten zu erhalten und sicherzustellen das es gelöscht wurde
                    notifyDataSetChanged();
                }
            });
        });
        t.start();
    }




}