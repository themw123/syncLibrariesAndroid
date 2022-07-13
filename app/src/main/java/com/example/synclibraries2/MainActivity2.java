package com.example.synclibraries2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Vector;
import syncLibraries.Session;

public class MainActivity2 extends AppCompatActivity {

    RecycleAdapter adapter;
    RecyclerView recyclerView;
    Session s;
    Vector<String>ausnahmen;
    TextInputEditText t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ausnahmen = new Vector<String>();
        // data to populate the RecyclerView with
        recyclerView = (RecyclerView) findViewById(R.id.ausnahmen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecycleAdapter(this);

        Thread t1 = new Thread(() -> {

            s = new Session();
            ausnahmen = s.getAusnhamen();
            adapter.refreshListe(ausnahmen);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            });
        });
        t1.start();

        recyclerView.setAdapter(adapter);


        t = (TextInputEditText)this.findViewById(R.id.editTextTextPersonName);

        t.setFocusable(true);

        t.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                add();
                t.clearFocus();
                return true;
            }
        });


        t.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus) {
                    t.setHint(null);
                }
            }
        });


    }




    //Zeile hinzufügen
    public void add() {
        t = (TextInputEditText)this.findViewById(R.id.editTextTextPersonName);
        String titel = t.getText().toString();
        t.setText("");
        if(!titel.isEmpty()) {
            adapter.addData(titel,adapter.getItemCount());
            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }



}