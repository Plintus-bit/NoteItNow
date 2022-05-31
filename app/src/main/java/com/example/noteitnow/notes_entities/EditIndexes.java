package com.example.noteitnow.notes_entities;

import com.example.noteitnow.statics_entity.Doings;

public class EditIndexes {
    private int start;
    private int end;
    private Doings type;
    private int length;

    public EditIndexes() {
        start = -1;
        end = -1;
        type = null;
    }

    public EditIndexes(int start, int end) {
        this.type = null;
        setStartAndEnd(start, end);
    }

    public EditIndexes(int start, int end, Doings type) {
        this.start = start;
        this.end = end;
        this.type = type;
        updateLength();
    }

    public void updateLength() {
        length = end - start;
    }

    public void setStart(int start) {
        this.start = start;
        updateLength();
    }

    public void setEnd(int end) {
        this.end = end;
        updateLength();
    }

    public void setType(Doings type) {
        this.type = type;
    }

    public void setStartAndEnd(int start, int end) {
        this.start = start;
        this.end = end;
        updateLength();
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getLength() {
        return this.length;
    }

    public Doings getType() {
        return this.type;
    }
}