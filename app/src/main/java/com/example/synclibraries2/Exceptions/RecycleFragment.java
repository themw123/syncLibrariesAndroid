package com.example.synclibraries2.Exceptions;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;

import syncLibraries.SyncLibrary;

public class RecycleFragment extends Fragment {

    private int position;

    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    public static final String TITLE = "title";

    public RecycleFragment(int position) {
        this.position = position;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycle_fragment_exceptions, container, false);

        recyclerView = view.findViewById(R.id.justwatch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecycleAdapter(position);

        setData();


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }


    private void setData() {
        Thread t1 = new Thread(() -> {
            MainActivity.waitForCreateSyncLibrary();
            MainActivity.waitForStartSync();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        adapter.refreshListe(MainActivity.sl);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                        getActivity().findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                    }
                    catch(Exception e) {

                    }

                }
            });
        });
        t1.start();



    }

}