package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import syncLibraries.SyncLibrary;

public class MainActivity2 extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SeitenAdapter seitenAdapter;

    private Activity activity = this;
    private RecycleAdapter adapter;
    private RecyclerView recyclerView;

    private SyncLibrary sl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        seitenAdapter = new SeitenAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(seitenAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("Tab " + (position+1));
        }).attach();


        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        Thread t1 = new Thread(() -> {

            //darf nicht in main thread damit ui lädt
            MainActivity.waitForCreateSyncLibrary();
            MainActivity.waitForStartSync();

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