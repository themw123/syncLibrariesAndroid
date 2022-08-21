package com.example.synclibraries2.Download;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synclibraries2.Callback;
import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import syncLibraries.Download;

public class MainActivity3 extends AppCompatActivity implements Callback {

    private Callback callback;

    private static TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SeitenAdapter seitenAdapter;
    private static Download download;
    public static Thread ssh2;
    public static Thread loadDownloading;
    public static Thread loadDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //nur um callbacks zu testen
        RecycleAdapter.setCallback(this);

        this.download = MainActivity.download;

        ssh2 = new Thread(() -> {
            download.startSSH();
        });
        ssh2.start();

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

        if(download.getDownloadingNoti() > 0) {
            tabLayout.getTabAt(1).getOrCreateBadge().setNumber(download.getDownloadingNoti());
            tabLayout.getTabAt(1).getOrCreateBadge().setBackgroundColor(Color.parseColor("#12467a"));
            tabLayout.getTabAt(1).getOrCreateBadge().setBadgeTextColor(Color.WHITE);
            tabLayout.getTabAt(1).getOrCreateBadge().setHorizontalOffset(-10);
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {

                } else if (tab.getPosition() == 1) {
                    notificationCallback(1, "down");
                } else {
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public static void waitForSSH2() {
        try {
            ssh2.join();
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

    //nur um callbacks zu testen
    @Override
    public void notificationCallback(int tab, String state) {
        if(state.equals("up")) {
            download.setDownloadingNoti(download.getDownloadingNoti()+1);
            tabLayout.getTabAt(tab).getOrCreateBadge().setNumber(download.getDownloadingNoti());
            tabLayout.getTabAt(tab).getOrCreateBadge().setBackgroundColor(Color.parseColor("#12467a"));
            tabLayout.getTabAt(tab).getOrCreateBadge().setBadgeTextColor(Color.WHITE);
            tabLayout.getTabAt(tab).getOrCreateBadge().setHorizontalOffset(-10);
        }
        else if(state.equals("down")){
            tabLayout.getTabAt(tab).removeBadge();
            download.setDownloadingNoti(0);
        }
    }
}