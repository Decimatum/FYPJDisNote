package com.example.l30605.fypjdisnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l30605.fypjdisnote.Entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends ActionBarActivity {

    EditText etTitle,etDescription;
    Button btnSave,btnDelete;

    Spinner spinFontSize,spinNoteSize,spinNoteType;

    Dialog deleteDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

         int i = (int)getIntent().getSerializableExtra("note");
         NoteApplication na = NoteApplication.getInstance();
         Note note = na.getSpecificDescriptionNote(EditActivity.this, i);

        spinFontSize = (Spinner)findViewById(R.id.spinFontSize);
        ArrayAdapter<CharSequence> FontSizeArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Size, android.R.layout.simple_spinner_item);
        FontSizeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFontSize.setAdapter(FontSizeArrayAdapter);
        String fontSize = note.getFontsize();
        if (!fontSize.equals(null)) {
            int spinnerPosition = FontSizeArrayAdapter.getPosition(fontSize);
            spinFontSize.setSelection(spinnerPosition);
        }

        spinNoteSize = (Spinner)findViewById(R.id.spinNoteSize);
        spinNoteSize.setAdapter(FontSizeArrayAdapter);
        String noteSize = note.getSize();
        if (!noteSize.equals(null)) {
            int spinnerPosition = FontSizeArrayAdapter.getPosition(noteSize);
            spinNoteSize.setSelection(spinnerPosition);
        }

        spinNoteType = (Spinner)findViewById(R.id.spinNoteType);
        ArrayAdapter<CharSequence> BackgroundArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Background, android.R.layout.simple_spinner_item);
        BackgroundArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNoteType.setAdapter(BackgroundArrayAdapter);
        String noteType = note.getNoteType();
        if (!noteSize.equals(null)) {
            int spinnerPosition = BackgroundArrayAdapter.getPosition(noteType);
            spinNoteType.setSelection(spinnerPosition);
        }

        spinNoteType.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;
        spinNoteSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;

        spinFontSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditActivity.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;


        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        String str = note.getTitle();
        etTitle.setText(str);
        etDescription.setText(note.getDescription());

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i = (int)getIntent().getSerializableExtra("note");
                NoteApplication na = NoteApplication.getInstance();
                Note note = na.getSpecificDescriptionNote(EditActivity.this, i);


                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                note.setTitle(title);
                note.setDescription(description);
                String fontSize = spinFontSize.getSelectedItem().toString();
                String noteSize = spinNoteSize.getSelectedItem().toString();
                String NoteType = spinNoteType.getSelectedItem().toString();

                note.setFontsize(fontSize);
                note.setSize(noteSize);
                note.setNoteType(NoteType);

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
                note.setLastModi(dateFormat.format(date).toString());

                boolean boll = na.updateDatabase(note, i, EditActivity.this);
                if (boll) {
                    Toast.makeText(EditActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(EditActivity.this,listofnotes.class);
                startActivity(intent);

            }
        });
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int i = (int)getIntent().getSerializableExtra("note");
                        NoteApplication na = NoteApplication.getInstance();
                        Note note = na.getSpecificDescriptionNote(EditActivity.this, i);

                        boolean boll = na.deleteFrmDatabase(i, EditActivity.this);
                        if(boll){
                            Toast.makeText(EditActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(EditActivity.this,listofnotes.class);
                        startActivity(intent);

                       // System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditActivity.this,home.class);
        startActivity(intent);
    }
}
