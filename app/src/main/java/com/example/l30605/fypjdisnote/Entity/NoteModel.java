package com.example.l30605.fypjdisnote.Entity;

/**
 * Created by L30605 on 08-Oct-15.
 */
public class NoteModel {
    private String title;
    private String description;

    public NoteModel(String title, String description){
        this.title = title;
        this.description = description;
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
}
