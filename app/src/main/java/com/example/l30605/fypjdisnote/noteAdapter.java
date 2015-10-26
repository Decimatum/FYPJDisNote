package com.example.l30605.fypjdisnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.l30605.fypjdisnote.Entity.Note;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by L30605 on 07-Oct-15.
 */
public class noteAdapter extends ArrayAdapter<Note> {
    public noteAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adaptlayout, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvid = (TextView) convertView.findViewById(R.id.tvid);
        tvid.setVisibility(View.INVISIBLE);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(note.getTitle());
        String da = "Created Date  : " + note.getCreateDate();

        TextView tvLast = (TextView) convertView.findViewById(R.id.tvLast);
        String lastModi = "Last Modified : " + note.getLastModi();
        tvLast.setText(lastModi);
        tvHome.setText(da);
        int i = note.getNoteID();
        String noteb = String.valueOf(i);
        tvid.setText(noteb);
        // Return the completed view to render on screen
        return convertView;
    }

}
