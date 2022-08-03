package com.example.synclibraries2.Download;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import syncLibraries.Download;
import syncLibraries.SSH;
import syncLibraries.SyncLibrary;

public class RecycleFragment extends Fragment {

    private int position;
    private Download download;
    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    private TextInputEditText t;
    private ProgressBar pr;

    public static final String TITLE = "title";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            recyclerView = view.findViewById(R.id.search);
            t = (TextInputEditText) view.findViewById(R.id.editText);
            pr = (ProgressBar)view.findViewById(R.id.progressbar);
        }
        else if(position == 1) {
            view = inflater.inflate(R.layout.recycle_fragment_download2, container, false);
            recyclerView = view.findViewById(R.id.justwatch);
            t = (TextInputEditText) view.findViewById(R.id.editText);
            pr = (ProgressBar)view.findViewById(R.id.progressbar);
        }
        else if (position == 2) {
            view = inflater.inflate(R.layout.recycle_fragment_download3, container, false);
            recyclerView = view.findViewById(R.id.justwatch);
            t = (TextInputEditText) view.findViewById(R.id.editText);
            pr = (ProgressBar) view.findViewById(R.id.progressbar);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecycleAdapter(position);
        adapter.refreshAdapter(download);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        if(position == 0) {
            t.setFocusable(true);
            t.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    String titel = t.getText().toString();
                    t.setText("");
                    if(!titel.isEmpty()) {
                        pr.setVisibility(View.VISIBLE);

                        Thread t = new Thread(() -> {

                            download.setSearch(titel, 1);

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    pr.setVisibility(View.INVISIBLE);
                                }
                            });

                        });
                        t.start();

                    }
                    t.clearFocus();
                    return true;
                }
            });

        }




        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }


}