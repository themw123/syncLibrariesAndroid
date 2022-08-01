package com.example.synclibraries2.Download;

import android.app.Activity;
import android.os.Bundle;
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

import syncLibraries.SyncLibrary;

public class RecycleFragment extends Fragment {

    private int position;

    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    private Activity activity;
    private SyncLibrary sl;


    public static final String TITLE = "title";

    public RecycleFragment(int position) {
        this.position = position;
        // Required empty public constructor
        MainActivity.waitForCreateSyncLibrary();
        MainActivity.waitForStartSync();
        this.sl = MainActivity.sl;
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
        //((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }
}