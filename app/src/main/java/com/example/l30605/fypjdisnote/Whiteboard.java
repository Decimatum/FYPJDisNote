package com.example.l30605.fypjdisnote;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Whiteboard extends ActionBarActivity {

    ImageButton btnCamera;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.

    ImageView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whiteboard);

        hello = (ImageView) findViewById(R.id.ivEx);
        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Whiteboard.this,Whiteboar2.class);
                startActivity(intent);
            }
        });

    }


}
