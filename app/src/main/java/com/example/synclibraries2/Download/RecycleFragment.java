package com.example.synclibraries2.Download;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Vector;
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
    private static Vector<RecycleAdapter> adapterArray = new Vector<RecycleAdapter>();
    private RecyclerView recyclerView;

    private TextInputEditText t;
    private ProgressBar pr;
    private SwipeRefreshLayout swipeContainer;

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
            recyclerView = view.findViewById(R.id.loading);
            t = (TextInputEditText) view.findViewById(R.id.editText);
            pr = (ProgressBar)view.findViewById(R.id.progressbar);
        }
        else if (position == 2) {
            view = inflater.inflate(R.layout.recycle_fragment_download3, container, false);
            recyclerView = view.findViewById(R.id.local);
            t = (TextInputEditText) view.findViewById(R.id.editText);
            pr = (ProgressBar) view.findViewById(R.id.progressbar);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecycleAdapter(position);
        adapter.refreshAdapter(download);
        recyclerView.setAdapter(adapter);
        adapterArray.add(adapter);



        if(position == 1) {
            Thread t = new Thread(() -> {

                MainActivity3.waitForDownloading();

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


        if(position == 2) {
            Thread t = new Thread(() -> {

                MainActivity3.waitForDownloaded();

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


        if(position == 0) {
            t.setFocusable(true);
            t.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    String titel = t.getText().toString();
                    t.setText("");
                    if(!titel.isEmpty()) {
                        pr.setVisibility(View.VISIBLE);

                        download.setSearch("",0);
                        adapter.notifyDataSetChanged();
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

        if(position == 1) {
            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Thread t = new Thread(() -> {
                        download.setDownloading();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeContainer.setRefreshing(false);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    });
                    t.start();
                }
            });

            // Configure the refreshing colors

            swipeContainer.setColorSchemeColors(Color.parseColor("#3584D5"));
        }


        if(position == 2) {
            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    Thread t = new Thread(() -> {
                        download.setDownloaded();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeContainer.setRefreshing(false);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    });
                    t.start();
                }
            });

            // Configure the refreshing colors

            swipeContainer.setColorSchemeColors(Color.parseColor("#3584D5"));
        }



        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //((TextView)view.findViewById(R.id.textViewXY)).setText(getArguments().getString(TITLE));

    }

    public static RecycleAdapter getAdapter(int position) {
        return adapterArray.get(position);
    }

    public static void dellAllAdapter() {
        adapterArray = new Vector<RecycleAdapter>();
    }

}