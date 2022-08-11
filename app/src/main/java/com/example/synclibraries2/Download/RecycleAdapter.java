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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.material.tabs.TabLayout;

import java.util.Vector;

import syncLibraries.Download;
import syncLibraries.Qbittorrent;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private int positionFragment;
    private Download download;

    public RecycleAdapter(int positionFragment) {
        this.positionFragment = positionFragment;
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
        private final TextView speed;
        private ImageButton button;
        private TabLayout tabLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
            site = (TextView) view.findViewById(R.id.site);
            size = (TextView) view.findViewById(R.id.size);
            seeder = (TextView) view.findViewById(R.id.seeder);
            totalsize = (TextView) view.findViewById(R.id.totalsize);
            progress = (TextView) view.findViewById(R.id.progress);
            speed = (TextView) view.findViewById(R.id.speed);
            button = (ImageButton) view.findViewById(R.id.imageButton);
            tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
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
        public TextView getSpeed() {
            return speed;
        }
        public ImageButton getButton() {
            return button;
        }
        public TabLayout getTabLayout() { return tabLayout; }


    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = null;

        if(positionFragment == 0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_row1, viewGroup, false);
        }
        else if(positionFragment == 1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.download_row2, viewGroup, false);
        }
        else if(positionFragment == 2) {
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
        if(this.positionFragment == 0) {
            viewHolder.getTextView().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getName());
            viewHolder.getSite().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSite());
            viewHolder.getSize().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSize());
            viewHolder.getSeeder().setText(download.getSearch().get(viewHolder.getAdapterPosition()).getSeeder());

            //wenn download geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    buttonAnimation(v, "long");
                    String magnet = download.getSearch().get(viewHolder.getAdapterPosition()).getMagnet();
                    int count1 = download.getDownloading().size();

                    Thread t = new Thread(() -> {

                        download.addDownloading(magnet);
                        download.setDownloading();

                        int count2 = download.getDownloading().size();
                        if(count2 > count1) {
                            MainActivity3.notification(1,"up");
                        }

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //try damit falls download gedrückt wird und direkt geschlossen wird app nicht abstürzt
                                //verursacher RecycleFragment.getAdapter
                                try{
                                    RecycleAdapter adapter = RecycleFragment.getAdapter(1);
                                    adapter.notifyDataSetChanged();
                                }
                                catch(Exception e) {

                                }
                            }
                        });
                    });
                    t.start();
                }
            });
        }
        else if(this.positionFragment == 1) {

            int pos = viewHolder.getLayoutPosition();

            viewHolder.getTextView().setText(download.getDownloading().get(pos).getName());
            viewHolder.getTotalsize().setText(download.getDownloading().get(pos).getTotal_size()+" GiB");
            viewHolder.getSeeder().setText(download.getDownloading().get(pos).getSeeder());
            viewHolder.getProgress().setText(download.getDownloading().get(pos).getProgress()+"%");
            viewHolder.getSpeed().setText(download.getDownloading().get(pos).getSpeed()+" MiB");


            //wenn stoppen geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    buttonAnimation(v, "long");


                    //dialog box
                    androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(v.getContext(), R.style.AlertDialogTheme)
                            .setMessage("Auch local löschen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    delDownloading(viewHolder, pos, true);
                                }
                            })
                            .setNeutralButton("Nein", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    delDownloading(viewHolder, pos, false);
                                }
                            })
                            .show();



                }
            });




        }
        else if(this.positionFragment == 2) {

            int pos = viewHolder.getLayoutPosition();

            viewHolder.getTextView().setText(download.getDownloaded().get(pos));

            //wenn löschen geklickt wird
            viewHolder.getButton().setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    buttonAnimation(v, "long");

                    //dialog box
                    androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(v.getContext(), R.style.AlertDialogTheme)
                            .setMessage("Wirklich löschen?")
                            .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    delDownloaded(viewHolder, pos);
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


        if(positionFragment == 0) {
           i = download.getSearch().size();
        }
        else if(positionFragment == 1) {
            i = download.getDownloading().size();
        }
        else if(positionFragment == 2) {
            i = download.getDownloaded().size();
        }
        else {
            i = 0;
        }
        return i;
    }



    private void delDownloaded(ViewHolder viewHolder, int pos) {
        String title = download.getDownloaded().get(pos);
        download.getDownloaded().remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, download.getDownloaded().size());

        Thread t = new Thread(() -> {

            download.delDownloanded(title);
            //muss aus sein sonnst absturz weil inconsistent.
            //download.setDownloaded();

            /*
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //um die realen daten zu erhalten und sicherzustellen das es gelöscht wurde
                    notifyDataSetChanged();
                }
            });
            */
        });
        t.start();
    }


    private void delDownloading(ViewHolder viewHolder, int pos, boolean localDelete) {
        String infohash_v1 = download.getDownloading().get(pos).getInfohash();
        download.getDownloading().remove(pos);
        //bei live refresh sollte es besser aus sein
        //notifyItemRemoved(pos);
        //notifyItemRangeChanged(pos, download.getDownloading().size());

        Thread t = new Thread(() -> {

            download.delDownloading(infohash_v1, localDelete);
            //muss aus sein sonnst absturz weil inconsistent.
            //download.setDownloading();


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

    public void buttonAnimation(View view, String time) {

        Animation shake = null;

        if(time.equals("long")) {
            shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.longanim);
        }
        else {
            shake = AnimationUtils.loadAnimation(view.getContext(), R.anim.shortanim);
        }

        view.startAnimation(shake);
    }


}