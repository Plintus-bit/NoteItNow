package com.example.noteitnow.statics_entity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.noteitnow.R;
import com.example.noteitnow.notes_entities.NoteStructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

//PublicResourсes
public class PublicResources {
    // Отладка
    public static final String DEBUG_LOG_TAG = "my debugging messages";

    // Ключи для настроек
    public static String THEME_KEY = "darcula_theme";

    // для получения разрешений
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    // статусы разрешений
    public static boolean has_external_permission;

    // ресурсы
    public static Resources res;

    // Единица измерения
    public static int device_width;
    public static int device_height;
    public static double DP;

    // Высота системной панели
    public static final int SYSTEM_PANEL_HEIGHT = 50;

    // Запросы на какие действия рассматриваем
    public static final int REQUEST_EXTERNAL_STORAGE = 111;
    public static final int REQUEST_NOTE_EMPTY = 1;
    public static final int REQUEST_NEW_CANVAS = 2;
    public static final int REQUEST_EDIT_CANVAS = 3;
    public static final int REQUEST_NOTE_EDIT = 4;
    public static final int REQUEST_PICK_IMAGE = 5;

    // EXTRAS для передачи данных
    public static final String EXTRA_NOTE = "NOTE";
    public static final String EXTRA_CANVAS_TAG = "CANVAS_TAG";
    public static final String EXTRA_BG_CANVAS_COLOR = "BG_CANVAS_COLOR";
    public static final String EXTRA_DRAWING_TEMP_INDEX = "DRAWING_TEMP_INDEX";
    public static final String EXTRA_NOTE_IS_EMPTY = "IS_EMPTY";
    public static final String EXTRA_CANVAS_IS_EDIT = "IS_EDIT";
    public static final String EXTRA_CANVAS_IS_EMPTY = "CANVAS_IS_EMPTY";

    // ACTIONS для передачи данных
    public static final String ACTION_EDIT_EXIST_CANVAS = "EDIT_EXIST_CANVAS";
    public static final String ACTION_CREATE_NEW_CANVAS = "CREATE_NEW_CANVAS";
    public static final String ACTION_EDIT_EXIST_NOTE = "EDIT_EXIST_NOTE";
    public static final String ACTION_CREATE_NEW_NOTE = "CREATE_NEW_NOTE";

    // Значения EXTRAS по умолчанию
    public static final String EXTRA_DEFAULT_STRING_VALUE = "";
    public static final boolean EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE = true;
    public static final int EXTRA_DEFAULT_INT_VALUE = -1;

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
    public static final String IMAGE_PNG_PREFIX = "PNG_";
    public static final String TEXT_GSON_PREFIX = "GSON_";

    // для вычисления прозрачности
    public static final int ALPHA = 255;

    // Default значения
    public static String DEFAULT_NOTE_NAME;
    public static int DEFAULT_COLOR;
    public static int DEFAULT_BG_COLOR;
    public static int DEFAULT_BG_COLOR_VALUE = -101;
    public static final float DEFAULT_STROKE_WIDTH = 12;
    public static final int DEFAULT_OPACITY = (int) (20 * PublicResources.ALPHA / 100);
    public static final int DEFAULT_ID = -111;

    // разделитель
    public static final String SIMPLE_SEPARATOR = ":";

    // настройки
    public static SharedPreferences preferences;

    // IDs
    public static final int CANCEL_ID = 119;

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

    public static void createCancelItem(LayoutInflater inflater, int child_layout,
                                        LinearLayout parent, View.OnClickListener cl) {
        ImageButton btn = (ImageButton) inflater
                .inflate(child_layout, parent, false);
        btn.setImageDrawable(res.getDrawable(R.drawable.ic_cancel_icon, null));
        btn.setId(PublicResources.CANCEL_ID);
        btn.setOnClickListener(cl);
        parent.addView(btn, 0);
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

    /**********************************************************************************
     * удалить файл */
    public static boolean deleteFile(File file) {
        return file.delete();
    }

    // распарсить рисунок из файла
    public static Bitmap parseBitmapFromDrawingFile(String file_path) {
        File file = new File(file_path);
        return BitmapFactory.decodeFile(file_path);
    }

    /**********************************************************************************
     * собрать время создания файла */
    public static ArrayList<Integer> getCollectedDate(String date) {
        String[] temp_date_data = date.split(SIMPLE_SEPARATOR);
        ArrayList<Integer> new_date = new ArrayList<Integer>();
        for (int i = 0; i < temp_date_data.length; ++i) {
//            Log.d(DEBUG_LOG_TAG, "date >>> " + temp_date_data[i]);
            new_date.add(Integer.parseInt(temp_date_data[i]));
        }
        return new_date;
    }
}
