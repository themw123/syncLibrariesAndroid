package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import syncLibraries.SSH;
import syncLibraries.SyncLibrary;

public class MainActivity extends AppCompatActivity {

    private SSH ssh = null;
    private Thread createSSHThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tw2 = (TextView) findViewById(R.id.textView2);
        tw2.setMovementMethod(new ScrollingMovementMethod());

        createSSH();
    }




    public void onClickSync(View view) {

        Thread t1 = new Thread(() -> {

            new Thread(() -> closeStremio()).start();

            new Thread(() -> openSurfshark()).start();

            sync();

        });
        t1.start();


    }




    public void sync() {

        Button b1 = (Button) findViewById(R.id.syncButton);
        TextView tw1 = (TextView) findViewById(R.id.textView1);
        TextView tw2 = (TextView) findViewById(R.id.textView2);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE );

                buttonAnimation(findViewById(R.id.syncButton), "long");
                buttonOnorOff(b1, false);

                //b1.getBackground().setAlpha(200);
                tw1.setText("");
                tw2.setText("");
            }
        });


        //!!!!!!!!!!!!!!!synchronize!!!!!!!!!!!!!!!!!!!!!!
        SyncLibrary sl = new SyncLibrary();
        sl.sync();
        boolean error = sl.wasSuccessful();
        //!!!!!!!!!!!!!!!synchronize!!!!!!!!!!!!!!!!!!!!!!


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

                if(!error) {
                    tw1.setText("Synchronisation erfolgreich.");
                }
                else {
                    tw1.setText("Synchronisation fehlgeschlagen!!!");
                    tw2.setText(sl.getErrorString());
                }

                buttonOnorOff(b1, true);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tw1.setText("");
                    }
                }, 3000);
            }
        });

    }

    private void createSSH() {
        createSSHThread = new Thread(() -> {
            ssh = new SSH("marvin","***REMOVED***","192.168.0.138",22);
        });
        createSSHThread.start();
    }

    private void waitForCreateSSH() {
        try {
            createSSHThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeStremio() {
        waitForCreateSSH();
        ssh.sendCommend("taskkill /IM stremio.exe /F >nul 2>&1");
    }

    private void openStremio() {
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openstremio\"");
    }

    private void openSurfshark() {
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"opensurfshark\"");
    }

    private void closeSurfshark() {
        waitForCreateSSH();
        ssh.sendCommend("taskkill /IM Surfshark.exe /F >nul 2>&1");
    }





    public void onClickClose(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            closeStremio();
        });
        t.start();
    }

    public void onClickOpen(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            openStremio();
            openSurfshark();
        });
        t.start();

    }


    public void onClickExceptions(View view) {

        buttonAnimation(view,"short");
        startActivity(new Intent( MainActivity.this, MainActivity2.class));
    }


    public void buttonAnimation(View view, String time) {

        Animation shake = null;

        if(time.equals("long")) {
            shake = AnimationUtils.loadAnimation(this, R.anim.longanim);
        }
        else {
            shake = AnimationUtils.loadAnimation(this, R.anim.shortanim);
        }

        view.startAnimation(shake);
    }

    public void buttonOnorOff(Button b1, boolean st) {

        String color = "";

        if(st) {
            b1.setEnabled(true);
            color = "#3584D5";
        }
        else {
            b1.setEnabled(false);
            color = "#91adc9";
        }





    }

}