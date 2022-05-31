package com.example.noteitnow.notes_entities;

import android.text.SpannableStringBuilder;
import android.util.Log;

import com.example.noteitnow.R;
import com.example.noteitnow.statics_entity.PublicResources;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class NoteStructure implements Serializable {
    // название заметки
    private String name;
    // содержимое заметки
    private String text;
    // имя .json файла
    private String file_name;
    // фон заметки
    private int bg;
    // пин иконка
    private int pin;
    // закреплена ли заметка
    private boolean is_pinned;
    // рисунки и их фоны
    private ArrayList<DrawingStructure> drawings;
    // дата создания или редактирования
    private ArrayList<Integer> date;

    private ArrayList<TextSpans> spans;

    /**********************************************************************************
     * Конструкторы */
    public NoteStructure() {
        name = "";
        text = "";
        file_name = "";
        bg = R.color.white;
        pin = R.drawable.ic_clip_icon;
        is_pinned = false;
        drawings = null;
        spans = null;
        date = null;
    }

    public NoteStructure(String note_name) {
        name = note_name;
        text = "";
        file_name = "";
        bg = R.color.white;
        pin = R.drawable.ic_clip_icon;
        is_pinned = false;
        drawings = null;
        spans = null;
        date = null;
    }

    public NoteStructure(String note_name, String note_text) {
        name = note_name;
        text = note_text;
        file_name = "";
        bg = R.color.white;
        pin = R.drawable.ic_clip_icon;
        is_pinned = false;
        drawings = null;
        spans = null;
        date = null;
    }

    /**********************************************************************************
     * Setter'ы */
    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setIsPinned(boolean is_pinned) {
        this.is_pinned = is_pinned;
    }

    public void setDrawings(ArrayList<DrawingStructure> drawings) {
        this.drawings = drawings;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public void setSpans(ArrayList<TextSpans> spans) {
        this.spans = spans;
    }

    public void setDate(ArrayList<Integer> date) {
        this.date = date;
    }

    /**********************************************************************************
     * Getter'ы */
    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getBg() {
        return bg;
    }

    public int getPin() {
        return pin;
    }

    public boolean getPinned() {
        return is_pinned;
    }

    public ArrayList<DrawingStructure> getDrawings() {
        return drawings;
    }

    public String getFileName() {
        return file_name;
    }

    public ArrayList<TextSpans> getSpans() {
        return spans;
    }

    public ArrayList<Integer> getDate() {
        return date;
    }

    /**********************************************************************************
     * GSON */
    public static String getNoteToGsonString(NoteStructure note) {
        Gson gson = new Gson();
        String gson_string = gson.toJson(note);
        return gson_string;
    }

    public static NoteStructure getNoteFromGson(String gson_string) {
        Gson gson = new Gson();
        NoteStructure note = null;
        try {
            note = gson.fromJson(gson_string, NoteStructure.class);
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
        }
        return note;
    }

}
