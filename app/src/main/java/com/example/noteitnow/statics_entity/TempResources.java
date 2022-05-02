package com.example.noteitnow.statics_entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

public class TempResources {

    // Временный Drawable файл
    private static Drawable temp_drawable;
    public static void setTempDrawable(Drawable drawable) {
        temp_drawable = drawable;
    }
    public static Drawable getTempDrawable() {
        return temp_drawable;
    }

    // Временный Bitmap нарисованной картинки
    private static Bitmap temp_drawing;
    public static void setTempDrawingFromCanvas(Bitmap drawing) {
        temp_drawing = drawing;
    }
    public static Bitmap getTempDrawingFromCanvas() {
        return temp_drawing;
    }

    // Непостоянный Id для LinearLayout в CanvasActivity
    public static final int LINEAR_LAYOUT_ID = 98;

}
