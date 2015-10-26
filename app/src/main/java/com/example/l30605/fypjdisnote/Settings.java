package com.example.l30605.fypjdisnote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Settings extends ActionBarActivity {

    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = (Spinner) findViewById(R.id.SpinPoint);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.point_color,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        loadSavePreferences();


    }
    public void btnSave(View view){
        String selected = spinner.getSelectedItem().toString();
        if(selected.equals("Red")){
            savePreferences("pointerColor","Red");
        }
        else if(selected.equals("Blue")){
            savePreferences("pointerColor","Blue");
        }
        else if(selected.equals("Green")){
            savePreferences("pointerColor","Green");
        }

        loadSavePreferences();

        Toast.makeText(this, "Settings have been saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.this,home.class);
        startActivity(intent);
    }
    public void loadSavePreferences(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sp.getString("pointerColor","myColor");


        if(name.equals("Red")){
            spinner.setSelection(0);
        }
        else if(name.equals("Blue")){
            spinner.setSelection(1);
        }
        else if(name.equals("Green")){
            spinner.setSelection(2);
        }
    }

    public void savePreferences(String key,String value){


        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        String pointColor = spinner.getSelectedItem().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key,value);
        editor.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }
}
