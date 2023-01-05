package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.synclibraries2.Exceptions.MainActivity2;
import com.example.synclibraries2.Download.MainActivity3;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;
import java.util.Vector;

import syncLibraries.Audio;
import syncLibraries.Download;
import syncLibraries.SSH;
import syncLibraries.SocketClient;
import syncLibraries.SyncLibrary;

public class MainActivity extends AppCompatActivity {

    //local
    private final String ssh1user = "marvin";
    private final String ssh1server = "192.168.0.138";
    private final int ssh1port = 22;
    
    //server
    private final String ssh2user = "marvin";
    private final String server = "192.168.0.67";
    private final int ssh2port = 22;
    private final int qbittorrentport = 8080;
    private final String downloadpath = "/downloads";


    //SocketServer
    private final String socketServer = "192.168.0.138";
    private final int socketPort = 9876;



    private final String ssh1pass = BuildConfig.ssh1pass;
    private final String ssh2pass = BuildConfig.ssh2pass;

    private static int counter = 0;
    public static SyncLibrary sl = null;
    public static SSH ssh = null;
    public static Download download = null;

    private static Thread createSyncLibrary = null;
    private static Thread createSSH1 = null;
    private static Thread startSync = new Thread();

    private FloatingActionMenu actionMenu;

    private Thread updateAudioSettings = new Thread();
    private TextInputEditText editText;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode deaktivieren
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

    private void waitForUpdateAudioSettings() {
        try {
            updateAudioSettings.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buildAlert() {
        //dialog box
        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setMessage("Settings")
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        updateAudioSettings = new Thread(() -> {
                            String bsTitle = editText.getText().toString();

                            String site = "";
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            int b1id = radioButton1.getId();
                            int b2id = radioButton2.getId();
                            if (selectedId != -1) {
                                if(selectedId == b1id) {
                                    //site = (String) radioButton1.getText();
                                    site = "cine";
                                }
                                else if(selectedId == b2id) {
                                    //site = (String) radioButton2.getText();
                                    site = "kinos";
                                }
                            }

                            waitForCreateSyncLibrary();
                            String plexToken = sl.getSession().getPlex();
                            sl.getSession().updateAusnahme("site", site);
                            sl.getSession().updateAusnahme("bsTitle", bsTitle);
                            sl.setAusnahme();

                        });
                        updateAudioSettings.start();
                    }
                })
                .setNeutralButton("exit".toLowerCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .setView(R.layout.override)
                .show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setAllCaps(false);

        Thread t = new Thread(() -> {
            waitForCreateSyncLibrary();
            waitForUpdateAudioSettings();
            String bsTitle = sl.getAusnahme("bsTitle");
            String site = sl.getAusnahme("site");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editText = (TextInputEditText) dialog.findViewById(R.id.editText);
                    editText.setText(bsTitle);

                    radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
                    radioButton1 = (RadioButton) dialog.findViewById(R.id.radio_cine);
                    radioButton2 = (RadioButton) dialog.findViewById(R.id.radio_kinos);

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    if(site.equals("cine")) {
                        radioGroup.check(R.id.radio_cine);
                    }
                    else if(site.equals("kinos")) {
                        radioGroup.check(R.id.radio_kinos);
                    }
                    selectedId = radioGroup.getCheckedRadioButtonId();

                }
            });

        });
        t.start();
    }


    private void openWebsite() {
        Thread t = new Thread(() -> {


            waitForCreateSyncLibrary();
            String override = sl.getAusnahme("bsTitle");
            String plexToken = sl.getSession().getPlex();
            Audio audio = new Audio(sl, server, 32400, plexToken);
            String url = audio.getUrl(override);
            if(url == null) {
                return;
            }

            closeChrome();

            SocketClient client = new SocketClient(socketServer, socketPort);
            String[] arr2 = {"open", url};
            client.writeToServer(arr2);


        });
        t.start();

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

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getDrawable(R.drawable.audio));
        SubActionButton audioButton = itemBuilder.setContentView(itemIcon3).build();
        audioButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        actionMenu.close(true);
                    }
                }, 500);


                buildAlert();

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
                .addSubActionView(audioButton, 200, 200)
                .addSubActionView(exceptionButton, 200, 200)
                .setRadius(290)
                .setStartAngle(230)
                .setEndAngle(310)
                .attachTo(menuButton)
                .build();
    }














    private void closeChrome() {
        ssh.sendCommend("taskkill /IM chrome.exe /F >nul 2>&1");
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
            buttonAnimation(view,"short");
            waitForCreateSSH();
            closePlex();
            closeChrome();
            //closeVPNPC();
            //closeStremio(true);
            //closeQbit();
        });
        t.start();
    }

    public void onClickOpen(View view) {
        Thread t = new Thread(() -> {
            buttonAnimation(view,"short");
            waitForCreateSSH();
            openPlex();

            //on double click
            openWebsite();

            //on hold add exception
            //...

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


}