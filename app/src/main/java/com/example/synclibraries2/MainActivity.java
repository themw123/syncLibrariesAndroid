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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.synclibraries2.Download.MainActivity3;
import com.example.synclibraries2.Exceptions.MainActivity2;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import syncLibraries.Download;
import syncLibraries.SSH;
import syncLibraries.SyncLibrary;

public class MainActivity extends AppCompatActivity {

    //ssh für open/close stremio ist gleiche wie für download
    private final String sshuser = "marvin";
    private final String sshpass = "xxxxx";
    private final String sshserver = "192.168.0.138";
    private final int sshport = 22;
    private final int qbittorrentport = 8080;
    private final String downloadpath = "D:\\torrents";





    private static int counter = 0;
    public static SyncLibrary sl = null;
    public static SSH ssh = null;
    public static Download download = null;


    private static Thread createSyncLibrary = null;
    private static Thread createSSH = null;
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
            createSSH(sshuser, sshpass, sshserver, sshport);
            this.download = new Download(sshuser, sshpass, sshserver, sshport, qbittorrentport, downloadpath);
        }



        menuButtons();


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

    private void createSSH(String user, String pass, String server, int port) {
        createSSH = new Thread(() -> {
            ssh = new SSH(user, pass, server ,port);
            ssh.connect();
        });
        createSSH.start();
    }

    public static void waitForCreateSSH() {
        try {
            createSSH.join();
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


    private void menuButtons() {
        FloatingActionButton.LayoutParams params1 = new FloatingActionButton.LayoutParams(170,170);
        params1.setMargins(0, 0, 0, 80);
        FloatingActionButton menuButton = new FloatingActionButton.Builder(this)
                .setPosition(5)
                .setLayoutParams(params1)
                .setBackgroundDrawable(R.drawable.menu)
                .build();
        menuButton.setElevation(5);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getDrawable(R.drawable.exception));
        SubActionButton exceptionButton = itemBuilder.setContentView(itemIcon2).build();
        exceptionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MainActivity.this, MainActivity2.class));
            }
        });
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getDrawable(R.drawable.download));
        SubActionButton downloadButton = itemBuilder.setContentView(itemIcon).build();
        downloadButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( MainActivity.this, MainActivity3.class));
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(downloadButton, 180, 180)
                .addSubActionView(exceptionButton, 180, 180)
                .setRadius(250)
                .setStartAngle(230)
                .setEndAngle(310)
                .attachTo(menuButton)
                .build();
    }

    private void closeStremio(boolean state) {
        waitForCreateSSH();
        ssh.sendCommend("taskkill /IM stremio.exe /F >nul 2>&1");
        if(state) {
            Button open = findViewById(R.id.open);
            Button close = findViewById(R.id.close);
            if(ssh.getError()) {
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
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openstremio\"");
        Button open = findViewById(R.id.open);
        Button close = findViewById(R.id.close);
        if(ssh.getError()) {
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
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"opensurfshark\"");
    }

    private void closeSurfshark() {
        waitForCreateSSH();
        ssh.sendCommend("taskkill /IM Surfshark.exe /F >nul 2>&1");
    }


    private void openQbit() {
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openqbit\"");
        download.startSSH();
        download.setDownloading();
        download.resumeDownloading();
    }

    private void closeQbit() {
        waitForCreateSSH();
        download.startSSH();
        download.setDownloading();
        download.pauseDownloading();
        ssh.sendCommend("taskkill /IM qbittorrent.exe /F >nul 2>&1");
    }

    public void onClickClose(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            closeStremio(true);
            closeQbit();
        });
        t.start();
    }

    public void onClickOpen(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            openSurfshark();
            openQbit();
            openStremio();
        });
        t.start();

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

        String test = "";
    }

}