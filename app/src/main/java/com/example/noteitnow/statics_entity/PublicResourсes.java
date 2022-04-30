package com.example.noteitnow.statics_entity;

import android.app.admin.DeviceAdminReceiver;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.noteitnow.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.zip.Inflater;

public class PublicResourсes {
    // Отладка
    public static final String DEBUG_LOG_TAG = "my debugging messages";

    // Единица измерения
    public static double DP;

    // Обязательное количество ответов = 1
    public static final int REQUIRED_REQUEST_ANSWERS_NUMBER = 1;

    // EXTRAS для передачи данных
    public static final String EXTRA_NOTE_IS_EMPTY = "IS_EMPTY";
    public static final String EXTRA_NOTE_NAME = "NOTE_NAME";
    public static final String EXTRA_DEFAULT_STRING_VALUE = "none";
    public static final Boolean EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE = true;

    // то, что касается FileProvider
    public static final String FILE_PROVIDER = "com.example.noteitnow.fileprovider";
    public static final String[] FILES_DIRECTORY = new String[]
            {"data_images", "data_docs", "cache_data_images", "cache_data_docs"};

    // Касается панели для заметок (текст, рисование, ластик)
//    public static final int DRAW = 1;
//    public static final int WRITE = 0;
    private static boolean is_draw = false;

    // Методы

    public static void setDrawStatus(boolean status) {
        is_draw = status;
    }

    public static boolean isDraw() {
        return is_draw;
    }

    private static LinearLayout getNoteLL(LayoutInflater inflater, int ll_id) {
        LinearLayout ll = (LinearLayout) inflater.inflate(ll_id, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (56 * DP));
        lp.bottomMargin = (int) (7 * DP);
        ll.setClipToPadding(true);
        ll.setLayoutParams(lp);
        return ll;
    }

    private static void setNoteImagePinsBtn(LayoutInflater inflater, int child_layout,
                                           LinearLayout parent_ll, Drawable pin_icon) {
        ImageButton btn = (ImageButton) inflater
                .inflate(child_layout, null, false);
        btn.setImageDrawable(pin_icon);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams((int) (24 * DP), LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) (6 * DP);
        lp.rightMargin = (int) (14 * DP);
        lp.weight = 1;
        btn.setLayoutParams(lp);
        parent_ll.addView(btn);
    }

    private static void setNoteName(LayoutInflater inflater, int child_layout,
                                    LinearLayout parent_ll, String note_name) {
        TextView note = (TextView) inflater.inflate(child_layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams((int) (160 * DP),
                              LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 10;
        note.setLayoutParams(lp);
        note.setText(note_name);
        parent_ll.addView(note);
    }

    private static void setNoteSpecialImagePinsButton(LayoutInflater inflater, int child_layout,
                                                      LinearLayout parent_ll) {
        ImageButton special_btn = (ImageButton) inflater
                .inflate(child_layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams((int) (24 * DP), LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = (int) (4 * DP);
        lp.weight = 3;
        special_btn.setLayoutParams(lp);
        parent_ll.addView(special_btn);
    }

    public static LinearLayout getNewNote(LayoutInflater inflater,
                                          String note_name, Drawable pin_icon) {
        LinearLayout ll_container = getNoteLL(inflater, R.layout.note_layout);
        setNoteImagePinsBtn(inflater, R.layout.pin_btn_layout, ll_container, pin_icon);
        setNoteName(inflater, R.layout.note_name_layout, ll_container, note_name);
        setNoteSpecialImagePinsButton(inflater, R.layout.note_edit_pin_layout, ll_container);
        setNoteSpecialImagePinsButton(inflater, R.layout.note_delete_pin_layout, ll_container);
        return ll_container;
    }
}
