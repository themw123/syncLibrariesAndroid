package com.example.synclibraries2.Audio;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.synclibraries2.Exceptions.MainActivity;
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
            String override = null;
            url = audio.getBsUrl(override);
            if(url == null) {
                return;
            }
            MainActivity.waitForCreateSSH();
            ssh.sendCommend("taskkill /IM chrome.exe /F >nul 2>&1");

            //zwei mal damit surfshark vpn genug zeit zum laden hat und keine credentials fordert
            ssh.sendCommend( "SCHTASKS /Create /TN openchrome1 /TR \"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" /SC ONEVENT /EC Application /MO *[System/EventID=777] /f");
            ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openchrome1\"");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ssh.sendCommend( "SCHTASKS /Create /TN openchrome2 /TR \"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe " + url + "\" /SC ONEVENT /EC Application /MO *[System/EventID=777] /f");
            ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openchrome2\"");
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