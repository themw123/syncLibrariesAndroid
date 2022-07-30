package com.example.synclibraries2;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import syncLibraries.SyncLibrary;

public class SerienFilme extends Fragment {

    private int position;

    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    private Activity activity;
    private SyncLibrary sl;


    public static final String TITLE = "title";

    public SerienFilme(int position) {
        this.position = position;
        // Required empty public constructor
        MainActivity.waitForCreateSyncLibrary();
        MainActivity.waitForStartSync();
        this.sl = MainActivity.sl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_serien_filme, container, false);

        recyclerView = view.findViewById(R.id.justwatch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecycleAdapter(position);

        adapter.refreshListe(sl);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }
}