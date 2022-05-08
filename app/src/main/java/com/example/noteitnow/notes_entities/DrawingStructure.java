package com.example.noteitnow.notes_entities;

import android.graphics.Color;

import java.io.Serializable;

public class DrawingStructure implements Serializable {
    private String drawing_file_name;
    private int drawing_bg;

    /**********************************************************************************
     * Конструкторы */
    public DrawingStructure() {
        drawing_file_name = "";
        drawing_bg = Color.TRANSPARENT;
    }

    public DrawingStructure(String name) {
        drawing_file_name = name;
        drawing_bg = Color.TRANSPARENT;
    }

    public DrawingStructure(int bg_color) {
        drawing_file_name = "";
        drawing_bg = bg_color;
    }

    public DrawingStructure(String name, int bg_color) {
        drawing_file_name = name;
        drawing_bg = bg_color;
    }

    /**********************************************************************************
     * Getter'ы */
    public String getDrawingFileName() {
        return drawing_file_name;
    }

    public int getDrawingBg() {
        return drawing_bg;
    }

    /**********************************************************************************
     * Setter'ы */
    public void setDrawingFileName(String file_name) {
        this.drawing_file_name = file_name;
    }

    public void setDrawingBg(int drawing_bg) {
        this.drawing_bg = drawing_bg;
    }

}
