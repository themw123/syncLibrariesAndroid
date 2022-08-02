package com.example.synclibraries2.Download;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;

import syncLibraries.Download;
import syncLibraries.SSH;
import syncLibraries.SyncLibrary;

public class RecycleFragment extends Fragment {

    private int position;
    private Download download;
    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    public static Handler handler;
    public static Runnable runnable;

    public static final String TITLE = "title";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        refreshHandler();
    }

    public RecycleFragment(int position, Download download) {
        this.position = position;
        this.download = download;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;

        if(position == 0) {
            view = inflater.inflate(R.layout.recycle_fragment_download1, container, false);
        }
        else if(position == 1) {
            view = inflater.inflate(R.layout.recycle_fragment_download2, container, false);
        }
        else if (position == 2) {
            view = inflater.inflate(R.layout.recycle_fragment_download3, container, false);
        }


        recyclerView = view.findViewById(R.id.justwatch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecycleAdapter(position);

        //adapter.refreshListe();
        //adapter.notifyDataSetChanged();
        //refreshThread();
        recyclerView.setAdapter(adapter);


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }

    private void refreshHandler() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                adapter.refreshAdapter(download);
                adapter.notifyDataSetChanged();
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);
    }

}