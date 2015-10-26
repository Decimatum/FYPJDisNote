package com.example.l30605.fypjdisnote.Entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.l30605.fypjdisnote.NoteApplication;

import java.util.Date;

/**
 * Created by L30605 on 06-Oct-15.
 */
public class NoteAdapter {
    private static final String DATABASE_NAME = "note2.db";
    private static final String DATABASE_TABLE = "notedb";
    private static final int DATABASE_VERSION = 4;
    private SQLiteDatabase _db;
    private final Context context;

    public NoteAdapter(Context _context){
        this.context = _context;
        dbHelper = new MyNoteDBHelper(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    public static final String KEY_ID = "_id";
    public static final int COLUMN_KEY_ID = 0;

    public static final String ENTRY_TITLE = "title";
    public static final int COLUMN_TITLE_ID = 1;

    public static final String ENTRY_DESCRIPTION = "description";
    public static final int COLUMN_DESCRIPTION_ID = 2;

    public static final String ENTRY_CREATEDATE = "createdate";
    public static final int COLUMN_CREATEDATE_ID = 3;

    public static final String ENTRY_NOTETYPE = "notetype";
    public static final int COLUMN_NOTETYPE_ID = 4;

    public static final String ENTRY_UUID = "uuid";
    public static final int COLUMN_UUID_ID = 5;

    public static final String ENTRY_SIZE = "size";
    public static final int COLUMN_SIZE = 6;

    public static final String ENTRY_FONTSIZE = "fontsize";
    public static final int COLUMN_FONTSIZE = 7 ;

    public static final String ENTRY_IMAGE = "image";
    public static final int COLUMN_IMAGE = 8;

    public static final String ENTRY_LASTMODI = "lastmodified";
    public static final int COLUMN_LASTMODI = 9;


    protected static final String DATABASE_CREATE = "create table " +
            DATABASE_TABLE + " " + "(" +
            KEY_ID + " integer primary key autoincrement, " +
            ENTRY_TITLE + " Text, " +
            ENTRY_DESCRIPTION + " Text, " +
            ENTRY_CREATEDATE + " Text, " +
            ENTRY_NOTETYPE + " Text, " +
            ENTRY_UUID + " Text, " +
            ENTRY_SIZE + " Text, " +
            ENTRY_FONTSIZE + " Text, " +
            ENTRY_IMAGE + " Text, " +
            ENTRY_LASTMODI + " Text)";



    public void open() throws SQLiteException {
        try{
            _db= dbHelper.getWritableDatabase();

            Log.w(MYDBADAPTER_LOG_CAT,"DB opened as writable database");

        }catch(SQLiteException e){
            _db= dbHelper.getReadableDatabase();
            Log.w(MYDBADAPTER_LOG_CAT,"DB opened as readable database");
        }
    }
    public void close(){
        _db.close();
        Log.w(MYDBADAPTER_LOG_CAT,"DB closed");

    }



    public long insertEntry(Note n ){

        ContentValues newEntryValues = new ContentValues();
        NoteApplication na = NoteApplication.getInstance();
        long ret = 0;
        try {
            newEntryValues.put(ENTRY_TITLE, n.getTitle());
            newEntryValues.put(ENTRY_DESCRIPTION, n.getDescription());
            String da = n.getCreateDate().toString();
            newEntryValues.put(ENTRY_CREATEDATE, da);
            newEntryValues.put(ENTRY_NOTETYPE, n.getNoteType());
            newEntryValues.put(ENTRY_UUID, n.getUUID());
            newEntryValues.put(ENTRY_SIZE, n.getSize());
            newEntryValues.put(ENTRY_FONTSIZE, n.getFontsize());
            newEntryValues.put(ENTRY_IMAGE,n.getImage());
            newEntryValues.put(ENTRY_LASTMODI,n.getLastModi());

            ret = _db.insert(DATABASE_TABLE, null, newEntryValues);

        }catch(Exception ex){
            String message = ex.getMessage();

        }
        return ret;
    }

    public boolean removeEntry(long _rowIndex){
        if(_db.delete(DATABASE_TABLE,KEY_ID + " = " + _rowIndex,null) <= 0 ){
            Log.w(MYDBADAPTER_LOG_CAT,"Removing entry where id = "
                    + _rowIndex + "Failed");
            return false;
        }
        Log.w(MYDBADAPTER_LOG_CAT, "Removing entry where id = "
                + _rowIndex + " Success");
        return true;
    }

    public boolean updateEntry(long _rowIndex, Note n ){
        ContentValues editedEntryvalues = new ContentValues();
        NoteApplication na = NoteApplication.getInstance();

        editedEntryvalues.put(ENTRY_TITLE, n.getTitle());
        editedEntryvalues.put(ENTRY_DESCRIPTION, n.getDescription());
        String da = n.getCreateDate().toString();
        editedEntryvalues.put(ENTRY_CREATEDATE, da);
        editedEntryvalues.put(ENTRY_NOTETYPE, n.getNoteType());
        editedEntryvalues.put(ENTRY_SIZE, n.getSize());
        editedEntryvalues.put(ENTRY_FONTSIZE, n.getFontsize());
        editedEntryvalues.put(ENTRY_IMAGE,n.getImage());
        editedEntryvalues.put(ENTRY_LASTMODI,n.getLastModi());

        int updateStatus = _db.update(DATABASE_TABLE,editedEntryvalues, "_id = ?",
                new String[]{Integer.toString((int)_rowIndex)});
        return true;
    }

    public Cursor retrieveAllEntriesCursor(){
        Cursor c = null;
        try{
            c = _db.query(DATABASE_TABLE,
                    new String[]{KEY_ID,ENTRY_TITLE,ENTRY_DESCRIPTION,ENTRY_CREATEDATE,ENTRY_NOTETYPE,ENTRY_UUID,ENTRY_SIZE,ENTRY_FONTSIZE,ENTRY_IMAGE,ENTRY_LASTMODI}
            ,null, null,null,null,null,null);

        }catch (SQLiteException ex){
            Log.w(MYDBADAPTER_LOG_CAT,"Retrieve fail!");
        }
        return c;
    }


    private String MYDBADAPTER_LOG_CAT = "MY_LOG";

    private MyNoteDBHelper dbHelper;

    public class MyNoteDBHelper extends SQLiteOpenHelper {

        public MyNoteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DATABASE_CREATE);


            Cursor c = db.rawQuery("SELECT * FROM notedb ", null);
            try{
                String[] columnNames = c.getColumnNames();

            }
            finally{
                c.close();
            }

            Log.w(MYDBADAPTER_LOG_CAT, "Helper : DB " + DATABASE_TABLE + " Created!!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE " + " IF EXISTS " + DATABASE_TABLE);
            //db.execSQL(DATABASE_CREATE);
            //Log.w(MYDBADAPTER_LOG_CAT, "Helper : DB " + DATABASE_TABLE + " Created!!");


            try {
                db.execSQL("DROP TABLE IF EXISTS notedb");
                onCreate(db);

            } catch (SQLException e) {
                String message = e.getMessage();
            }
        }


    }
}
