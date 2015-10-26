package com.example.l30605.fypjdisnote;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.l30605.fypjdisnote.Entity.Note;
import com.example.l30605.fypjdisnote.Entity.NoteAdapter;

import java.util.ArrayList;

public class listofnotes extends ActionBarActivity {

    ListView lvNotes;

    Menu settingsMenu;
    MenuItem addNote;

    @Override
    protected void onResume() {
        super.onResume();

        NoteApplication na = NoteApplication.getInstance();
        na.populateArrayFromDB(listofnotes.this);

        noteAdapter what = new noteAdapter(this,na.getArray());
        lvNotes = (ListView) findViewById(R.id.lvNote);
        lvNotes.setAdapter(what);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofnotes);

        NoteApplication na = null;
        try {
            na = NoteApplication.getInstance();
            na.populateArrayFromDB(listofnotes.this);
            noteAdapter what = new noteAdapter(this,na.getArray());
            lvNotes = (ListView) findViewById(R.id.lvNote);
            lvNotes.setAdapter(what);

            lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    NoteApplication na2 = NoteApplication.getInstance();
                    na2.populateArrayFromDB(listofnotes.this);
                    noteAdapter wha2 = new noteAdapter(listofnotes.this, na2.getArray());
                    Note item = wha2.getItem(position);

                    Intent intent = new Intent(listofnotes.this, viewnote.class);
                    intent.putExtra("note", item.getNoteID());
                    startActivity(intent);
                }
            });
        }
        catch(Exception ex){
            String message = ex.getMessage();
        }




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        this.settingsMenu = menu;
        inflater.inflate(R.menu.menu_listofnote, menu);
        addNote = settingsMenu.findItem(R.id.addnote);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.addnote:

                Intent intent = new Intent(listofnotes.this,CreateNote.class);
                startActivity(intent);
                return true;

        }
        return false;

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(listofnotes.this,home.class);
        startActivity(intent);
    }

}
