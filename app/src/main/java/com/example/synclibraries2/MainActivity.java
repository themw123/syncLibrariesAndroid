package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    //local
    private final String ssh1user = "marvin";
    private final String ssh1server = "192.168.0.138";
    private final int ssh1port = 22;
    //server
    private final String ssh2user = "marvin";
    private final String server = "192.168.178.24";
    private final int ssh2port = 22;
    private final int qbittorrentport = 8080;
    private final String downloadpath = "/downloads";


    private static int counter = 0;
    public static SyncLibrary sl = null;
    public static SSH ssh = null;
    public static Download download = null;

    private static Thread createSyncLibrary = null;
    private static Thread createSSH1 = null;
    private static Thread startSync = new Thread();

    private FloatingActionMenu actionMenu;

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
            createSSH1(ssh1user, ssh1pass, ssh1server, ssh1port);
            this.download = new Download(ssh2user, ssh2pass, server, ssh2port, qbittorrentport, downloadpath);
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

    private void createSSH1(String user, String pass, String server, int port) {
        createSSH1 = new Thread(() -> {
            ssh = new SSH(user, pass, server ,port);
            ssh.connect();
        });
        createSSH1.start();
    }

    public static void waitForCreateSSH() {
        try {
            createSSH1.join();
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

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getDrawable(R.drawable.exception));
        SubActionButton exceptionButton = itemBuilder.setContentView(itemIcon2).build();
        exceptionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actionMenu.close(true);
                    }
                }, 500);

                startActivity(new Intent( MainActivity.this, MainActivity2.class));
            }
        });
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageDrawable(getDrawable(R.drawable.download1));
        SubActionButton downloadButton = itemBuilder.setContentView(itemIcon).build();
        downloadButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actionMenu.close(true);
                    }
                }, 500);
                startActivity(new Intent( MainActivity.this, MainActivity3.class));
            }
        });

        actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(downloadButton, 200, 200)
                .addSubActionView(exceptionButton, 200, 200)
                .setRadius(250)
                .setStartAngle(230)
                .setEndAngle(310)
                .attachTo(menuButton)
                .build();
    }















    private void openPlex() {
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openplex\"");
        opencloseColor(false);
    }
    private void closePlex() {
        ssh.sendCommend("taskkill /IM Plex.exe /F >nul 2>&1");
        opencloseColor(false);
    }

    private void openVPNPC() {
        ssh.sendCommend("\"C:\\Program Files\\ShrewSoft\\VPN Client\\ipsecc.exe\" -r ko0eqqy9sklnlwn7.myfritz.net -u Marvin -p " + ssh1pass +"Mw -a");
        opencloseColor(false);
    }

    private void closeVPNPC() {
        ssh.sendCommend("taskkill /IM ipsecc.exe /F >nul 2>&1");
        opencloseColor(false);
    }

    private void closeStremio(boolean state) {
        waitForCreateSSH();
        ssh.sendCommend("taskkill /IM stremio.exe /F >nul 2>&1");
        if(state) {
            opencloseColor(false);
        }
    }

    private void openStremio() {
        waitForCreateSSH();
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openstremio\"");
        opencloseColor(false);
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
        ssh.sendCommend("SCHTASKS.EXE /RUN /TN \"openqbit\"");
        download.startSSH();
        download.setDownloading();
        download.resumeDownloading();
    }

    private void closeQbit() {
        download.startSSH();
        download.setDownloading();
        download.pauseDownloading();
        ssh.sendCommend("taskkill /IM qbittorrent.exe /F >nul 2>&1");
    }

    public void onClickClose(View view) {
        Thread t = new Thread(() -> {
            waitForCreateSSH();
            buttonAnimation(view,"short");
            closePlex();
            closeVPNPC();
            //closeStremio(true);
            //closeQbit();
        });
        t.start();
    }

    public void onClickOpen(View view) {
        Thread t = new Thread(() -> {
            waitForCreateSSH();
            buttonAnimation(view,"short");
            String ipold = "";
            ipold = ssh.sendCommend("curl \"http://myexternalip.com/raw\"");

            openVPNPC();

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String ipnew = "";
            ipnew = ssh.sendCommend("curl \"http://myexternalip.com/raw\"");

            try {
                if(!ipold.equals(ipnew)) {
                    opencloseColor(true);
                    openPlex();
                }
            }catch (Exception e) {

            }


            //openSurfshark();
            //openQbit();
            //openStremio();
        });
        t.start();

    }

    private void opencloseColor(boolean state) {
        Button open = findViewById(R.id.open);
        Button close = findViewById(R.id.close);
        if(ssh.getError()) {
            open.setTextColor(Color.parseColor("#c44347"));
            close.setTextColor(Color.parseColor("#c44347"));
        }
        if(state) {
            open.setTextColor(Color.parseColor("#32a852"));
            close.setTextColor(Color.parseColor("#32a852"));
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

    private final String ssh1pass = "***REMOVED***";
    private final String ssh2pass = "***REMOVED***";

}