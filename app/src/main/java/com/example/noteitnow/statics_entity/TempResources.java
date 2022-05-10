package com.example.noteitnow.statics_entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

import java.util.ArrayList;

public class TempResources {

    // Временный Bitmap массив файлов для текущей заметки
    private static ArrayList<Bitmap> temp_drawings_array = new ArrayList<Bitmap>();
    public static ArrayList<Bitmap> getTempDrawingsArray() {
        return temp_drawings_array;
    }
//    public static void setTempDrawingsArray(ArrayList<Bitmap> array) {
//        temp_drawings_array = array;
//    }

    // Цвета фонов для данных рисунков
    private static ArrayList<Integer> temp_bgs_for_drawings = new ArrayList<Integer>();
    public static ArrayList<Integer> getTempBGsForDrawings() {
        return temp_bgs_for_drawings;
    }
//    public static void setTempBGsForDrawings(ArrayList<Integer> new_bgs) {
//        temp_bgs_for_drawings = new_bgs;
//    }

    // Временный Bitmap нарисованной картинки
    private static Bitmap temp_drawing;
    public static void setTempDrawingFromCanvas(Bitmap drawing) {
        temp_drawing = drawing;
    }
    public static Bitmap getTempDrawingFromCanvas() {
        return temp_drawing;
    }

    // Временная Иконка текущей заметки
    private static Drawable temp_pin_icon;
    public static void setTempPinIcon(Drawable icon) {
        temp_pin_icon = icon;
    }
    public static Drawable getTempPinIcon() {
        return temp_pin_icon;
    }

}
