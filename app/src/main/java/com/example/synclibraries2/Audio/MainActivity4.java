package com.example.synclibraries2.Audio;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.synclibraries2.R;

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

        client = new SocketClient("192.168.0.138", 9876);
        client.openSocket();

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(5000);
                client.writeToServer("https://bs.to/serie/Westworld/3/1-Parce-Domine/de/Streamtape");
                Thread.sleep(5000);
                //client.closeSocket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        t.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Perform some action here

        client.closeSocket();

    }


}