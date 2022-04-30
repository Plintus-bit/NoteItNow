package com.example.noteitnow.statics_entity;

import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

public class TempResources {
    private static Drawable temp_drawable;
    private static ImageButton current_active_panel_btn;

    // Методы

    public static void setTempDrawable(Drawable drawable) {
        temp_drawable = drawable;
    }

    public static Drawable getTempDrawable() {
        return temp_drawable;
    }

    public static void setCurrentActivePanelBtn(ImageButton btn) {
        current_active_panel_btn = btn;
    }

    public static ImageButton getCurrentActivePanelBtn() {
        return current_active_panel_btn;
    }
}
