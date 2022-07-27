package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.Collections;
import java.util.Vector;

import syncLibraries.JustWatch;
import syncLibraries.SyncLibrary;

public class MainActivity2 extends AppCompatActivity implements Serializable {

    RecycleAdapter adapter;
    RecyclerView recyclerView;

    SyncLibrary sl;
    Vector<JustWatch> jwv;
    Vector<String[]> ausnahmen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        jwv = new Vector<JustWatch>();
        ausnahmen = new Vector<String[]>();

        /*
        // data to populate the RecyclerView with
        recyclerView = (RecyclerView) findViewById(R.id.ausnahmen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleAdapter(this);
        */
        // data to populate the RecyclerView with
        recyclerView = (RecyclerView) findViewById(R.id.justwatch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleAdapter(this);

        Thread t1 = new Thread(() -> {

            sl = new SyncLibrary();
            jwv = sl.setJustWatchWatchList();
            ausnahmen = sl.getAusnahmen();

            adapter.refreshListe(jwv, ausnahmen);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            });
        });
        t1.start();

        recyclerView.setAdapter(adapter);



    }





}