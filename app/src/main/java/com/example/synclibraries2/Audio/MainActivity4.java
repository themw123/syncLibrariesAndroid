package com.example.synclibraries2.Audio;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.synclibraries2.R;

import syncLibraries.Audio;
import syncLibraries.SocketClient;

public class MainActivity4 extends AppCompatActivity{

    private String url = "https://bs.to/serie/Westworld/3/4-x";
    private SocketClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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
            //erst per ssh server socket starten dann weiter
            client = new SocketClient("192.168.0.138", 9876);
            client.writeToServer(url);
        });
        t.start();

    }


    private void closeBrowser() {
        client.closeSocket();
        //auch noch serversocket script auf pc schließen (kein ssh nötig weil script mit writeToServer("exit"); beendet wird durch client)
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        client.closeSocket();
    }
    */

}