package com.example.noteitnow.notes_entities;

import android.graphics.Typeface;

import com.example.noteitnow.statics_entity.Doings;

import java.io.Serializable;

public class TextSpans implements Serializable {
    private int start;
    private int end;
    private Doings span_type;
    private int data;
    private int flag;

    /**********************************************************************************
     * Конструкторы */
    public TextSpans() {
        start = -1;
        end = -1;
        span_type = null;
        data = 0;
        flag = 0;
    }

    public TextSpans(int start_selection, int end_selection) {
        start = start_selection;
        end = end_selection;
        span_type = null;
        data = 0;
        flag = 0;
    }

    public TextSpans(int start_selection, int end_selection, Doings span_type,
                     int data, int flag) {
        start = start_selection;
        end = end_selection;
        this.span_type = span_type;
        this.data = data;
        this.flag = flag;
    }

    /**********************************************************************************
     * Setter'ы */
    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setSpanType(Doings span_type) {
        this.span_type = span_type;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**********************************************************************************
     * Getter'ы */
    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public Doings getSpanType() {
        return span_type;
    }

    public int getData() {
        return data;
    }

    public int getFlag() {
        return flag;
    }

}
