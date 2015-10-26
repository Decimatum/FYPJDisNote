package com.example.l30605.fypjdisnote;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.example.l30605.fypjdisnote.Entity.Note;
import com.example.l30605.fypjdisnote.Entity.NoteAdapter;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by L30605 on 06-Oct-15.
 */
public class NoteApplication extends Application {

    private ArrayList<Note> noteArray;
    private SimpleDateFormat sdf;
    private static NoteApplication ourInstance = new NoteApplication();
    public static NoteApplication getInstance(){return ourInstance;}

    public NoteApplication(){
        noteArray = new ArrayList<Note>();
        sdf = new SimpleDateFormat("dd-MM-yy");

    }
    public SimpleDateFormat getDateFormat(){
        return sdf;
    }
    public ArrayList<Note> getArray(){
        return noteArray;
    }

    public static long addToDatabase(Note n , Context c){
        NoteAdapter db = new NoteAdapter(c);
        db.open();
        long rowIDofInsertedEntry = db.insertEntry(n);
        db.close();
        return rowIDofInsertedEntry;
    }



    public static boolean updateDatabase(Note n , int rowID, Context c){
        NoteAdapter db = new NoteAdapter(c);
        db.open();
        boolean updateStatus = db.updateEntry(rowID, n);
        db.close();
        return updateStatus;
    }

    public static boolean deleteFrmDatabase(int rowID, Context c){
        NoteAdapter db = new NoteAdapter(c);
        db.open();

        boolean updatestatus = db.removeEntry(rowID);
        db.close();
        return updatestatus;
    }

    public void populateArrayFromDB(Context c){
        noteArray.clear();
        NoteAdapter db = new NoteAdapter(c);
        db.open();


        Cursor cur = db.retrieveAllEntriesCursor();
        cur.moveToFirst();
        while(cur.moveToNext()){
            int rowID = cur.getInt(NoteAdapter.COLUMN_KEY_ID);
            String title = cur.getString(NoteAdapter.COLUMN_TITLE_ID);
            String description = cur.getString(NoteAdapter.COLUMN_DESCRIPTION_ID);
            String date = cur.getString(NoteAdapter.COLUMN_CREATEDATE_ID);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
            String lastModi = cur.getString(NoteAdapter.COLUMN_LASTMODI);


            try{
                Note n = new Note(rowID,title,description,date,lastModi);
                noteArray.add(n);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        db.close();

    }

    public Note getSpecificNote(Context c,int i){
        noteArray.clear();
        NoteAdapter db = new NoteAdapter(c);
        db.open();
        Note n = new Note();

        Cursor cur = db.retrieveAllEntriesCursor();
        cur.moveToFirst();
        while(cur.moveToNext()){
            int rowID = cur.getInt(NoteAdapter.COLUMN_KEY_ID);
            if(rowID == i) {
                String title = cur.getString(NoteAdapter.COLUMN_TITLE_ID);
                String description = cur.getString(NoteAdapter.COLUMN_DESCRIPTION_ID);
                String date = cur.getString(NoteAdapter.COLUMN_CREATEDATE_ID);

                // String textColor = cur.getString(NoteAdapter.COLUMN_WIDTH);
                //String BGColor = cur.getString(NoteAdapter.COLUMN_HEIGHT);

                try {
                    // n = new Note(rowID, title, date);
                    n = new Note(rowID,title,date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        db.close();
        return n;
    }
    public Note getSpecificDescriptionNote(Context c,int i){
        noteArray.clear();
        NoteAdapter db = new NoteAdapter(c);
        db.open();
        Note n = new Note();

        Cursor cur = db.retrieveAllEntriesCursor();
        cur.moveToFirst();
        while(cur.moveToNext()){
            int rowID = cur.getInt(NoteAdapter.COLUMN_KEY_ID);
            if(rowID == i) {

                String title = cur.getString(NoteAdapter.COLUMN_TITLE_ID);
                String description = cur.getString(NoteAdapter.COLUMN_DESCRIPTION_ID);
                String date = cur.getString(NoteAdapter.COLUMN_CREATEDATE_ID);
                String NoteType = cur.getString(NoteAdapter.COLUMN_NOTETYPE_ID);
                String FontSize = cur.getString(NoteAdapter.COLUMN_FONTSIZE);
                String NoteSize = cur.getString(NoteAdapter.COLUMN_SIZE);
                String lastModi = cur.getString(NoteAdapter.COLUMN_LASTMODI);
                String uuid = cur.getString(NoteAdapter.COLUMN_UUID_ID);

                String textColor = "";
                String BGColor = "";
//                try {
//                    int a = NoteAdapter.COLUMN_TEXTCOLOR;
//                    int b = NoteAdapter.COLUMN_BGCOLOR;
//
//                    textColor = cur.getString(NoteAdapter.COLUMN_TEXTCOLOR);
//                     BGColor = cur.getString(NoteAdapter.COLUMN_BGCOLOR);
//                }catch (Exception ex){
//                    String message = ex.getMessage();
//                }
                try {

                    //n = new Note(rowID, title, description,date,0,0,height,width,0,0);
                    n = new Note(rowID,title, description,date,NoteType,FontSize,NoteSize,uuid,lastModi);

                }catch (Exception e) {
                    String msg = e.getMessage();
                    // e.printStackTrace();
                }
            }
            else{}
        }

        db.close();
        return n;
    }

}
