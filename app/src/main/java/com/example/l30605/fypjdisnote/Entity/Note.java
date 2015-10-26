package com.example.l30605.fypjdisnote.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by L30605 on 06-Oct-15.
 */
@SuppressWarnings("serial")
public class Note implements Serializable{
    public static String INTENT_NAME_NOTEID = "NoteID";
    public static String INTENT_NAME_TITLE = "Title";
    public static String INTENT_NAME_DESCRIPTION = "Description";
    public static String INTENT_NAME_CREATEDATE = "CreateDate";

    private int noteID;
    private String title;
    private String description;
    private String createDate;
    private String noteType;
    private String UUID;
    private String size;
    private String fontsize;
    private String image;
    private String LastModi;

    public Note(String title, String description,
                String createDate, String fontsize,
                String lastModi, String size, String UUID,String noteType) {
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.fontsize = fontsize;
        LastModi = lastModi;
        this.size = size;
        this.UUID = UUID;
        this.noteType = noteType;
    }

    public Note(int noteID, String title,
                String description, String createDate,
                String noteType, String fontsize,
                String size, String UUID,String LastModi) {
        this.noteID = noteID;
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.noteType = noteType;
        this.fontsize = fontsize;
        this.size = size;
        this.UUID = UUID;
        this.LastModi = LastModi;
    }

    public Note(){}

    public Note( int noteID,String title, String description
            ,String createDate,String lastModi){
        this.noteID = noteID;
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.LastModi = lastModi;

    }

    public Note(String image,String uuid,String createDate,String lastModi){
        this.image = image;
        this.UUID = uuid;
        this.createDate = createDate;
        this.LastModi = lastModi;
    }

    public Note(int id,String title, String date){
        noteID = id;
        this.title = title;
        this.createDate = date;
    }

    public String getFontsize() {
        return fontsize;
    }

    public void setFontsize(String fontsize) {
        this.fontsize = fontsize;
    }




    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }


    public String getLastModi() {
        return LastModi;
    }

    public void setLastModi(String lastModi) {
        LastModi = lastModi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
