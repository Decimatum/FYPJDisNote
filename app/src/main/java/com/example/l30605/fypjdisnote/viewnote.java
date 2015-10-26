package com.example.l30605.fypjdisnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.l30605.fypjdisnote.Entity.Note;

public class viewnote extends ActionBarActivity {


    Menu settingsMenu;
    MenuItem editnote;
    RelativeLayout rlBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnote);

        int i = (int)getIntent().getSerializableExtra("note");
        NoteApplication na = NoteApplication.getInstance();
        Note note = na.getSpecificDescriptionNote(viewnote.this, i);

        TextView tv = (TextView)findViewById(R.id.textView);
        String s = String.valueOf(i);
        tv.setText(note.getTitle());

        TextView tv2 = (TextView)findViewById(R.id.textView2);
        tv2.setText(note.getDescription());

        TextView tv3 = (TextView) findViewById(R.id.tvDate);
        tv3.setText(note.getCreateDate());

        String noteType = note.getNoteType();
        TextColor(noteType);

        rlBackground = (RelativeLayout)findViewById(R.id.rlBackground);

        String textSize = note.getFontsize();
        TextSize(textSize);
    }

    public void TextSize(String size){
        TextView tv = (TextView)findViewById(R.id.textView);
        TextView tv3 = (TextView) findViewById(R.id.tvDate);

        if(size.equals("Small")){
            tv.setTextSize(10);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv3.setTextSize(10);
        }
        else if(size.equals("Medium")){
            tv.setTextSize(14);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv3.setTextSize(14);
        }
        else if(size.equals("Large")){
            tv.setTextSize(18);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv3.setTextSize(18);
        }
    }

    public void TextColor(String color){
        TextView tv = (TextView)findViewById(R.id.textView);
        TextView tv2 = (TextView)findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.tvDate);

        if(color.equals("Normal")){

                tv.setTextColor(getResources().getColor(R.color.Black));
                tv2.setTextColor(getResources().getColor(R.color.Black));
                tv3.setTextColor(getResources().getColor(R.color.Black));
        }
        if(color.equals("Green Back")){
            tv.setTextColor(getResources().getColor(R.color.GreenBack));
            tv2.setTextColor(getResources().getColor(R.color.GreenBack));
            tv3.setTextColor(getResources().getColor(R.color.GreenBack));
          //  rlBackground.setBackgroundColor(0xFF000000);

        }
        if(color.equals("Rust Gold")){
            tv.setTextColor(getResources().getColor(R.color.TextGold));
            tv2.setTextColor(getResources().getColor(R.color.TextGold));
            tv3.setTextColor(getResources().getColor(R.color.TextGold));
           // rlBackground.setBackgroundColor(Color.GREEN);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.settingsMenu = menu;
        inflater.inflate(R.menu.menu_viewnote, menu);
        editnote = settingsMenu.findItem(R.id.editnote);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.editnote:

                int i = (int)getIntent().getSerializableExtra("note");
                Intent intent = new Intent(viewnote.this,EditActivity.class);
                intent.putExtra("note",i);
                startActivity(intent);
                return true;



        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(viewnote.this,listofnotes.class);
        startActivity(intent);
    }
}
