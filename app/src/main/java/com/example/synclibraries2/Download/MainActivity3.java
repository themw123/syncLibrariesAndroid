package com.example.synclibraries2.Download;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import syncLibraries.Download;
import syncLibraries.SSH;

public class MainActivity3 extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SeitenAdapter seitenAdapter;
    private static Download download;
    public static Thread ssh;
    public static Thread loadDownloading;
    public static Thread loadDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        this.download = MainActivity.download;

        ssh = new Thread(() -> {
            download.startSSH();
        });
        ssh.start();


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        seitenAdapter = new SeitenAdapter(getSupportFragmentManager(), getLifecycle(), download);
        //alle fragments direkt setzen.Also nicht erst laden wenn geswiped wird. Wichtig damit update funktioniert.
        viewPager.setOffscreenPageLimit(2);
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
        }).attach();




    }

    public static void waitForSSH() {
        try {
            ssh.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void finish() {
        super.finish();
        RecycleFragment.dellAllAdapter();
        RecycleFragment.live = false;
    }
}