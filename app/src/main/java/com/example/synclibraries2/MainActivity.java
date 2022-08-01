package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.synclibraries2.Exceptions.MainActivity2;

import syncLibraries.SyncLibrary;

public class MainActivity extends AppCompatActivity {

    private static int counter = 0;
    public static SyncLibrary sl = null;

    private static Thread createSyncLibrary = null;
    private static Thread startSync = new Thread();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tw2 = (TextView) findViewById(R.id.textView2);
        tw2.setMovementMethod(new ScrollingMovementMethod());

        //sonnst wird es mehrmals aufgerufen durch MainActivity2 anscheinend
        counter++;
        if(counter == 1) {
            createSyncLibrary();
        }

    }




    public void onClickSync(View view) {

        startSync = new Thread(() -> {

            new Thread(() -> closeStremio(false)).start();

            new Thread(() -> openSurfshark()).start();

            sync();

        });
        startSync.start();


    }




    public void sync() {


        Button b1 = (Button) findViewById(R.id.syncButton);
        ImageButton b2 = (ImageButton) findViewById(R.id.button);
        TextView tw1 = (TextView) findViewById(R.id.textView1);
        TextView tw2 = (TextView) findViewById(R.id.textView2);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE );

                buttonAnimation(findViewById(R.id.syncButton), "long");
                buttonOnorOff(b1, false);
                //ImagebuttonOnorOff(b2, false);

                //b1.getBackground().setAlpha(200);
                tw1.setText("");
                tw2.setText("");
            }
        });

        waitForCreateSyncLibrary();

        //!!!!!!!!!!!!!!!synchronize!!!!!!!!!!!!!!!!!!!!!!
        sl.sync();
        boolean error = sl.wasSuccessful();
        //!!!!!!!!!!!!!!!synchronize!!!!!!!!!!!!!!!!!!!!!!


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Button syncButton = findViewById(R.id.syncButton);
                if(error) {
                    syncButton.setTextColor(Color.parseColor("#c44347"));
                }

                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

                if(!error) {
                    tw1.setText("Synchronisation erfolgreich.");
                }
                else {
                    tw1.setText("Synchronisation fehlgeschlagen!!!");
                    tw2.setText(sl.getErrorString());
                }

                buttonOnorOff(b1, true);
                //ImagebuttonOnorOff(b2, true);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tw1.setText("");
                        syncButton.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }, 3000);
            }
        });

    }



    private void createSyncLibrary() {
        createSyncLibrary = new Thread(() -> {
            sl = new SyncLibrary();
        });
        createSyncLibrary.start();
    }

    public static void waitForCreateSyncLibrary() {
        try {
            createSyncLibrary.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void waitForStartSync() {
        try {
            startSync.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closeStremio(boolean state) {
        waitForCreateSyncLibrary();
        sl.sshSendCommand("taskkill /IM stremio.exe /F >nul 2>&1");
        if(state) {
            Button open = findViewById(R.id.open);
            Button close = findViewById(R.id.close);
            if(sl.getSSH().getError()) {
                open.setTextColor(Color.parseColor("#c44347"));
                close.setTextColor(Color.parseColor("#c44347"));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            open.setTextColor(Color.parseColor("#A6A4A4"));
                            close.setTextColor(Color.parseColor("#A6A4A4"));
                        }
                    }, 4000);
                }
            });
        }

    }

    private void openStremio() {
        waitForCreateSyncLibrary();
        sl.sshSendCommand("SCHTASKS.EXE /RUN /TN \"openstremio\"");
        Button open = findViewById(R.id.open);
        Button close = findViewById(R.id.close);
        if(sl.getSSH().getError()) {
            open.setTextColor(Color.parseColor("#c44347"));
            close.setTextColor(Color.parseColor("#c44347"));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        open.setTextColor(Color.parseColor("#A6A4A4"));
                        close.setTextColor(Color.parseColor("#A6A4A4"));
                    }
                }, 4000);
            }
        });
    }

    private void openSurfshark() {
        waitForCreateSyncLibrary();
        sl.sshSendCommand("SCHTASKS.EXE /RUN /TN \"opensurfshark\"");
    }

    private void closeSurfshark() {
        waitForCreateSyncLibrary();
        sl.sshSendCommand("taskkill /IM Surfshark.exe /F >nul 2>&1");
    }





    public void onClickClose(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            closeStremio(true);
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


    public void buttonOnorOff(Button b, boolean st) {
        if(st) {
            b.setEnabled(true);
        }
        else {
            b.setEnabled(false);
        }
    }

}