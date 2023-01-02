package com.example.synclibraries2.Audio;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.synclibraries2.MainActivity;
import com.example.synclibraries2.R;

import syncLibraries.Audio;
import syncLibraries.SSH;


public class MainActivity4 extends AppCompatActivity{

    private String url;
    private SSH ssh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ssh = MainActivity.ssh;
        openWebsite();
        //closeBrowser();
    }

    private void openWebsite() {
        Thread t = new Thread(() -> {
            Audio audio = new Audio("192.168.0.67", 32400, "fYyu_nN9V9_jqpZ6K6UF");
            url = audio.getBsUrl();
            if(url == null) {
                return;
            }
            //per ssh openchrome ausführen
            MainActivity.waitForCreateSSH();
            ssh.sendCommend("taskkill /IM chrome.exe /F >nul 2>&1");
            ssh.sendCommend( "SCHTASKS /Create /TN openchrome /TR \"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe " + url + "\" /SC ONEVENT /EC Application /MO *[System/EventID=777] /f");
            ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openchrome\"");
            //ssh.sendCommend("schtasks.exe /delete /tn mytest /f");


        });
        t.start();

    }


    /*
    @Override
    protected void onStop() {
        super.onStop();
        client.closeSocket();
    }
    */

}