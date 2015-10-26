package com.example.l30605.fypjdisnote;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class home extends ActionBarActivity {

    private ImageButton btnRemote;
    private ImageButton btnWhiteboard;
    private ImageButton btnNote;
    private ImageButton btnSetting;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        btnRemote = (ImageButton)findViewById(R.id.btnRemote);
        btnWhiteboard = (ImageButton)findViewById(R.id.btnDiscussion);
        btnNote = (ImageButton)findViewById(R.id.btnNote);
        btnSetting = (ImageButton)findViewById(R.id.btnSetting);


        btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,Remote.class);
                startActivity(intent);
            }
        });

        btnWhiteboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,Whiteboard.class);
                startActivity(intent);
            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,listofnotes.class);
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home.this,Settings.class);
                startActivity(intent);
            }
        });

    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(home.this,Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
