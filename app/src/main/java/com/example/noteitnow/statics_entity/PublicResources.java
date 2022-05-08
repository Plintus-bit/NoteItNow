package com.example.noteitnow.statics_entity;

import android.app.admin.DeviceAdminReceiver;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteitnow.Note;
import com.example.noteitnow.R;
import com.example.noteitnow.notes_entities.NoteStructure;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.zip.Inflater;
//PublicResourсes
public class PublicResources {
    // Отладка
    public static final String DEBUG_LOG_TAG = "my debugging messages";

    // Единица измерения
    public static int device_width;
    public static int device_height;
    public static double DP;

    // Запросы на какие действия рассматриваем
    public static final int REQUEST_NOTE_EMPTY = 1;
    public static final int REQUEST_NEW_CANVAS = 2;
    public static final int REQUEST_EDIT_CANVAS = 3;
    public static final int REQUEST_NOTE_EDIT = 4;

    // EXTRAS для передачи данных
    public static final String EXTRA_NOTE = "NOTE";
    public static final String EXTRA_NOTE_DRAWINGS = "NOTE_DRAWINGS";
    public static final String EXTRA_BG_CANVAS_COLOR = "BG_CANVAS_COLOR";
    public static final String EXTRA_DRAWING_TEMP_INDEX = "DRAWING_TEMP_INDEX";
    public static final String EXTRA_NOTE_IS_EMPTY = "IS_EMPTY";
    public static final String EXTRA_NOTE_NAME = "NOTE_NAME";
    public static final String EXTRA_CANVAS_IS_EMPTY = "CANVAS_IS_EMPTY";

    // ACTIONS для передачи данных
    public static final String ACTION_EDIT_EXIST_CANVAS = "EDIT_EXIST_CANVAS";
    public static final String ACTION_CREATE_NEW_CANVAS = "CREATE_NEW_CANVAS";
    public static final String ACTION_EDIT_EXIST_NOTE = "EDIT_EXIST_NOTE";
    public static final String ACTION_CREATE_NEW_NOTE = "CREATE_NEW_NOTE";

    // Значения EXTRAS по умолчанию
    public static final String EXTRA_DEFAULT_STRING_VALUE = "none";
    public static final boolean EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE = true;
    public static final int EXTRA_DEFAULT_INT_VALUE = -1;

    // Название заметки по умолчанию, если отсутсвует и название, и текст
    public static String DEFAULT_NOTE_NAME;

    // то, что касается FileProvider
    public static final String FILE_PROVIDER = "com.example.noteitnow.fileprovider";
    public static final String[] FILES_DIRECTORY = new String[]
            {"data_images", "data_notes", "data_docs",
             "cache_data_images", "cache_data_docs", "cache_data_notes"};

    // директория с заметками
    public static File NOTES_DIR;
    public static File IMAGES_DIR;

    // расширения
    public static final String IMAGE_PNG = ".png";
    public static final String TEXT_GSON = ".txt";
//    public static final String TEXT_GSON = ".json";
    public static final String IMAGE_PNG_PREFIX = "PNG_";
    public static final String TEXT_GSON_PREFIX = "GSON_";

    // для вычисления прозрачности
    public static final int ALPHA = 255;

    // Default цвет
    public static int DEFAULT_COLOR;
    public static int DEFAULT_BG_COLOR;
    public static final float DEFAULT_STROKE_WIDTH = 12;
    public static final int DEFAULT_OPACITY = (int) (20 * PublicResources.ALPHA / 100);

    /**********************************************************************************
     * Первый вариант разметки (устар.) */
    private static LinearLayout getNoteLL(LayoutInflater inflater, int ll_id) {
        LinearLayout ll = (LinearLayout) inflater.inflate(ll_id, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (56 * DP));
        lp.bottomMargin = (int) (7 * DP);
        ll.setClipToPadding(true);
        ll.setLayoutParams(lp);
        return ll;
    }

    /**********************************************************************************
     * установка пина (устар.) */
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

    /**********************************************************************************
     * установка названия заметки (устар.) */
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

    /**********************************************************************************
     * установка специальных пинов "редактировать" и "удалить" (устар.) */
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

    /**********************************************************************************
     * создание новой заметки (устар.) */
    public static LinearLayout getNewNote(LayoutInflater inflater,
                                          String note_name, Drawable pin_icon) {
        LinearLayout ll_container = getNoteLL(inflater, R.layout.note_layout);
        setNoteImagePinsBtn(inflater, R.layout.pin_btn_layout, ll_container, pin_icon);
        setNoteName(inflater, R.layout.note_name_layout, ll_container, note_name);
        setNoteSpecialImagePinsButton(inflater, R.layout.note_edit_pin_layout, ll_container);
        setNoteSpecialImagePinsButton(inflater, R.layout.note_delete_pin_layout, ll_container);
        return ll_container;
    }

    /**********************************************************************************
     * создание цветового элемента */
    private static void colorItem(LayoutInflater inflater, int child_layout,
                                      LinearLayout parent, int color) {
        ImageButton btn = (ImageButton) inflater
                .inflate(child_layout, parent, false);
        btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        btn.setId(color);
        parent.addView(btn);
    }

    /**********************************************************************************
     * получение панели цветов */
    public static LinearLayout getLLPanelWithColors(LayoutInflater inflater, int layout,
                                              int[] colors) {
        LinearLayout ll = (LinearLayout) inflater
                .inflate(layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        // ll.setId(TempResources.LINEAR_LAYOUT_ID);
        ll.setLayoutParams(lp);
        for (int color : colors) {
            colorItem(inflater, R.layout.popup_menu_item_layout, ll, color);
        }
        return ll;
    }

    /**********************************************************************************
     * получение панели элементов */
    public static LinearLayout getLLPanelWithItems(LayoutInflater inflater, int layout,
                                                   int[] items_id, ArrayList<Drawable> items) {
        LinearLayout ll = (LinearLayout) inflater
                .inflate(layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        // ll.setId(TempResources.LINEAR_LAYOUT_ID);
        ll.setLayoutParams(lp);
        for (int i = 0; i < items_id.length; ++i) {
            getPanelItem(inflater,
                    R.layout.popup_menu_default_item_layout, ll, items_id[i], items.get(i));
        }
        return ll;
    }

    /**********************************************************************************
     * создание элемента для панели элементов */
    private static void getPanelItem(LayoutInflater inflater, int child_layout,
                                     LinearLayout parent, int item_id, Drawable item) {
        ImageButton btn = (ImageButton) inflater
                .inflate(child_layout, parent, false);
        btn.setImageDrawable(item);
        btn.setId(item_id);
        parent.addView(btn);
    }


    /**********************************************************************************
     * Работа с json */
    // чтение json
    public static NoteStructure parseGsonFromFile(String file_path) {
        String temp_str;
        String gson_string = "";
        File file = new File(file_path);
        // Log.d(DEBUG_LOG_TAG, file.getAbsolutePath());
        // чтение
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((temp_str = reader.readLine()) != null) {
                gson_string += temp_str;
            }
            // Log.d(DEBUG_LOG_TAG, gson_string);
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
        }
        return NoteStructure.getNoteFromGson(gson_string);
    }

    // добавить json (2)
    public static void saveOrReplaceGson(String file_path, String gson_string) {
        File file = new File(file_path);
        // Log.d(DEBUG_LOG_TAG, file.getAbsolutePath());
        // запись
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(gson_string);
            writer.close();

        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
        }
    }

    // создать или получить существующий файл для записи рисунка
    public static File createFile(String file_name, Doings extension) {
        File file = null;
        switch (extension) {
            case PNG:
                file = new File(IMAGES_DIR, file_name + PublicResources.IMAGE_PNG);
                break;
            case GSON:
                file = new File(NOTES_DIR, file_name + PublicResources.TEXT_GSON);
                break;
        }
//        if (file != null) {
//            Log.d(DEBUG_LOG_TAG, file.getAbsolutePath());
//        }
        return file;
    }

}
