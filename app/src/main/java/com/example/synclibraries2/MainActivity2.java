package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity2 extends AppCompatActivity{

    public Activity activity = this;

    RecycleAdapter adapter;
    RecyclerView recyclerView;

    private SyncLibrary sl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);


        Thread t1 = new Thread(() -> {
            //darf nicht in main thread damit ui lädt
            MainActivity.waitForCreateSyncLibrary();
            MainActivity.waitForCreateSyncThread();
            sl = MainActivity.sl;

            //muss in main thread
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    recyclerView = (RecyclerView) findViewById(R.id.justwatch);

                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    adapter = new RecycleAdapter();

                    findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);

                    adapter.refreshListe(sl);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
            });


        });
        t1.start();






    }





}