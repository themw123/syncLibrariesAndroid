package com.example.synclibraries2.Download;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import syncLibraries.Download;
import syncLibraries.SSH;

public class MainActivity3 extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SeitenAdapter seitenAdapter;
    private static Download download;

    public static Thread t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
        Thread t1 = new Thread(() -> {

            this.download = MainActivity.download;
            download.startSSH();
            refreshThread();

            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);
            seitenAdapter = new SeitenAdapter(getSupportFragmentManager(), getLifecycle(), download);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewPager.setAdapter(seitenAdapter);
                    new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                        if(position == 0) {
                            tab.setText("Search");
                        }
                        else if(position == 1) {
                            tab.setText("Loading");
                        }
                        else if(position == 2){
                            tab.setText("Local");
                        }
                        else {
                            tab.setText("kp");
                        }
                    }).attach();
                    findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
                }
            });



        });
        t1.start();





    }


    private void refreshThread() {
        t1 = new Thread(() -> {
            while(true) {
                if(download.getSSH().getError()) {
                    download.getSSH().connect();
                }
                download.refreshData();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }



    public void onPause() {
        super.onPause();
        RecycleFragment.handler.removeCallbacks(RecycleFragment.runnable);
    }








}