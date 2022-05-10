package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.noteitnow.notes_entities.DrawingStructure;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.example.noteitnow.statics_entity.TempResources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Note extends AppCompatActivity implements View.OnClickListener {
    private EditText note_name, note_main_text;
    private ImageButton pin_btn;

    // статичные массивы
    public int[] pin_ids;
    public int[] colors;

    // текущий цвет bg
    private int bg_color;

    // массив рисунков
    ArrayList<DrawingStructure> drawings;

    // текущее действие над заметкой, от которого зависит её выгрузка
    private String current_action;
    private String current_file_name;

    private boolean is_drawings_empty;
    private Resources res;

    // попап
    private PopupMenu.OnMenuItemClickListener popup_cl;

    // inflater
    LayoutInflater inflater;

    // цвета
    private int current_text_color;
    private int current_text_bg_color;
    private int default_text_color;

    // вложенный класс для хранения элементов
    private class ExistView {
        private ImageButton item;
        public boolean is_active;
        ExistView(ImageButton item) {
            this.item = item;
            is_active = false;
        }
        ExistView(ImageButton item, boolean is_active) {
            this.item = item;
            this.is_active = is_active;
        }
        public void setActive(boolean is_active) {
            this.is_active = is_active;
        }
        private boolean getActive() {
            return is_active;
        }
        private ImageButton getItem() {
            return item;
        }
    }

    // активные элементы
    private ArrayList<ExistView> panel_views;
    private ImageView waiting_for_result;

    // HorizontalScrollView для загрузки элементов
    private HorizontalScrollView items_hsv;
    private ScrollView pins_sv;
    private LinearLayout pins_ll,
            main_ll,
            this_note_place_ll;

    // активные и неактивные элементы
    private Drawable active_panel_item_bg;
    private Drawable inactive_panel_item_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initOnCreate();
        
        getIntentFromMain();
    }

    /**********************************************************************************
     * получение намерения из MainActivity */
    private void getIntentFromMain() {
        Intent intent = getIntent();
        current_action = intent.getAction();
        Log.d(PublicResources.DEBUG_LOG_TAG, current_action);

        if (current_action.equals(PublicResources.ACTION_EDIT_EXIST_NOTE)) {
            try {
                NoteStructure exist_note = (NoteStructure) intent
                        .getSerializableExtra(PublicResources.EXTRA_NOTE);
                note_name.setText(exist_note.getName());
                note_main_text.setText(exist_note.getText());
                for (int i = 0; i < pin_ids.length; ++i) {
                    if (pin_ids[i] == exist_note.getPin()) {
                        pin_btn.setTag(i);
                        break;
                    }
                }
                pin_btn.setImageDrawable(res.getDrawable(exist_note.getPin(), null));
                bg_color = exist_note.getBg();
                main_ll.setBackgroundColor(bg_color);
                current_file_name = exist_note.getFileName();
                drawings = exist_note.getDrawings();
                if (drawings != null) {
                    is_drawings_empty = false;
                    for (int i = 0; i < drawings.size(); ++i) {
                        File file = PublicResources.createFile(drawings.get(i)
                                .getDrawingFileName(), Doings.PNG);
                        Bitmap new_drawing = PublicResources
                                .parseBitmapFromDrawingFile(file.getAbsolutePath());
                        TempResources.getTempDrawingsArray().add(new_drawing);
                        TempResources.getTempBGsForDrawings().add(drawings.get(i).getDrawingBg());
                        addImageDrawingToNote(R.layout.drawing_layout,
                                drawings.get(i).getDrawingFileName(), new_drawing,
                                drawings.get(i).getDrawingBg());
                    }
                }
                Log.d(PublicResources.DEBUG_LOG_TAG, ">>> Processed!");
            } catch (Exception e) {
                Log.d(PublicResources.DEBUG_LOG_TAG, "error (1): " + e.getMessage());
            }
        }

    }

    /**********************************************************************************
     * начальная инициализация */
    private void initOnCreate() {
        // получение ресурсов
        res = getResources();

        // сбор ресурсов
        pin_ids = getPinIds();
        colors = fillColors();

        // получение цветов
        default_text_color = res.getColor(R.color.black, null);
        current_text_bg_color = res.getColor(R.color.transparent_, null);
        current_text_color = default_text_color;
        bg_color = res.getColor(R.color.white, null);

        // инициализация inflater
        inflater = getLayoutInflater();

        // инициализация панелек
        items_hsv = findViewById(R.id.items_hsv);
        pins_sv = findViewById(R.id.pins_sv);
        pins_ll = findViewById(R.id.pins_ll);
        main_ll = findViewById(R.id.main_ll);
         this_note_place_ll = findViewById(R.id.this_note_place_ll);
        panel_views = new ArrayList<ExistView>();
        active_panel_item_bg = getDrawable(R.drawable.active_panel_item_btn);
        inactive_panel_item_bg = getDrawable(R.drawable.icons_bg);
        pin_btn = findViewById(R.id.pin_btn);

        // настройки Layout'ов
        main_ll.setBackgroundColor(bg_color);

        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_color_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.bg_change_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.add_drawing_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.marker_list_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_background_color_btn)));

        // рисунков нет
        is_drawings_empty = true;
        drawings = new ArrayList<DrawingStructure>();

        for (ExistView btn : panel_views) {
            btn.getItem().setOnClickListener(this);
        }
        pin_btn.setOnClickListener(this);
        pin_btn.setTag(28);

        note_name = findViewById(R.id.note_name);
        note_main_text = findViewById(R.id.note_main_text);

        TempResources.setTempPinIcon(pin_btn.getDrawable());

        popup_cl = getPopupMenuItemCl();

    }

    // попап меню
    private void showPopupForDrawing(View view) {
        PopupMenu popup_for_drawing = new PopupMenu(this, view);
        popup_for_drawing.setOnMenuItemClickListener(popup_cl);
        popup_for_drawing.inflate(R.menu.popup_menu_for_image_drawing);
        popup_for_drawing.show();
    }

    @Override
    public void onClick(View view) {
        // код обработки кликов
        if (pins_sv.getVisibility() == View.VISIBLE) {
            clearPinsPanel();
        }
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
            return;
        }
        switch (view.getId()) {
            case R.id.add_drawing_btn:
                Intent intent = new Intent(this, CanvasActivity.class);
                intent.setAction(PublicResources.ACTION_CREATE_NEW_CANVAS);
                startActivityForResult(intent, PublicResources.REQUEST_NEW_CANVAS);
                break;
            case R.id.pin_btn:
                pins_sv.setVisibility(View.VISIBLE);
                View.OnClickListener cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TempResources.setTempPinIcon(((ImageButton) view).getDrawable());
                        pin_btn.setTag(view.getTag());
                        pin_btn.setImageDrawable(TempResources.getTempPinIcon());
                        clearPinsPanel();
                    }
                };
                for (int line_number = 0; line_number < pins_ll.getChildCount(); ++line_number) {
                    LinearLayout line = (LinearLayout) pins_ll.getChildAt(line_number);
                    for (int pin_item_number = 0; pin_item_number < line.getChildCount();
                         ++pin_item_number) {
                        line.getChildAt(pin_item_number).setTag(line_number * 7 + pin_item_number);
                        line.getChildAt(pin_item_number).setOnClickListener(cl);
                    }
                }
                break;
            case R.id.text_color_btn:
                setColorChooserForPanelElement(panel_views.get(0), Doings.TEXT);
                break;
            case R.id.bg_change_btn:
                setColorChooserForPanelElement(panel_views.get(1), Doings.BACKGROUND);
                break;
            case R.id.text_background_color_btn:
                setColorChooserForPanelElement(panel_views.get(4), Doings.TEXT_BG);
                break;
            default:
                // nothing
        }
    }

    /**********************************************************************************
     * выборка цвета */
    private void setColorChooserForPanelElement(ExistView existView, Doings color_of_what) {
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
        }
        items_hsv.setVisibility(View.VISIBLE);
        LinearLayout ll = PublicResources
                .getLLPanelWithColors(inflater, R.layout.popup_menu_layout, colors);
        items_hsv.addView(ll);
        View.OnClickListener color_cl = getColorCL(existView, color_of_what);
        for (int i = 0; i < ll.getChildCount(); ++i) {
            (ll.getChildAt(i)).setOnClickListener(color_cl);
        }
    }

    /**********************************************************************************
     * получение специального ClickListener'а */
    private View.OnClickListener getColorCL(ExistView existView, Doings color_of_what) {
        View.OnClickListener cl = null;
        switch (color_of_what) {
            case BACKGROUND:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        bg_color = view.getId();
                        main_ll.setBackgroundColor(bg_color);
                        clearPanelItems();
                    }
                };
                break;
            default:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        clearPanelItems();
                    }
                };
        }
        return cl;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            forActivityResult();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        forActivityResult();
    }

    /**********************************************************************************
     * возврат результата */
    private void forActivityResult() {
        Intent intent = new Intent();
        if (current_action.equals(PublicResources.ACTION_CREATE_NEW_NOTE)) {
            Log.d(PublicResources.DEBUG_LOG_TAG, ">>> create new");
            // код обработки закрытия
            boolean is_empty = ((note_name.getText().length()
                    + note_main_text.getText().length()) == 0) && is_drawings_empty;
            // Пустая заметка или нет
            intent.putExtra(PublicResources.EXTRA_NOTE_IS_EMPTY, is_empty);
            // Если не пустая, сообщить её данные
            if (!is_empty) {
                NoteStructure new_note = getNewNote();
                intent.putExtra(PublicResources.EXTRA_NOTE, new_note);
                // сохранение файла
                File file = PublicResources.createFile(new_note.getFileName(), Doings.GSON);
                Log.d(PublicResources.DEBUG_LOG_TAG, file.getAbsolutePath());
                PublicResources.saveOrReplaceGson(file.getAbsolutePath(),
                        NoteStructure.getNoteToGsonString(new_note));
                // TempResources.setTempPinIcon(pin_btn.getDrawable());
            }
        }
        else if (current_action.equals(PublicResources.ACTION_EDIT_EXIST_NOTE)) {
            Log.d(PublicResources.DEBUG_LOG_TAG, ">>> edit exist");
            try {
                NoteStructure exist_note = getNewNote();
                exist_note.setFileName(current_file_name);
                intent.putExtra(PublicResources.EXTRA_NOTE, exist_note);
                // сохранение файла
                File file = PublicResources.createFile(current_file_name, Doings.GSON);
                PublicResources.saveOrReplaceGson(file.getAbsolutePath(),
                        NoteStructure.getNoteToGsonString(exist_note));
                Log.d(PublicResources.DEBUG_LOG_TAG, "Finally!");
            } catch (Exception e) {
                Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
            }
        }
        clearTempResources();
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getNewNoteName(String text) {
        String new_note_name = "";
        final int MAX_CHARS_LEN = 16;
        final int MAX_CHARS_LEN_WITH_END_CHARS = MAX_CHARS_LEN - 2;
        final String END_CHARS = "..";
        if (text.length() > MAX_CHARS_LEN) {
            for (int i = 0; i < MAX_CHARS_LEN_WITH_END_CHARS; ++i) {
                if (text.charAt(i) == '\n') {
                    break;
                }
                else {
                    new_note_name += text.charAt(i);
                }
            }
            new_note_name += END_CHARS;
            return new_note_name;
        }
        else if (text.length() == 0) {
            new_note_name = PublicResources.DEFAULT_NOTE_NAME;
            return new_note_name;
        }
        return text;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            String file_name = "";
            switch (requestCode) {
                // получение рисунка
                case PublicResources.REQUEST_NEW_CANVAS:
                    boolean is_new_canvas_empty = intent
                            .getBooleanExtra(PublicResources.EXTRA_CANVAS_IS_EMPTY,
                                    PublicResources.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
                    if (!is_new_canvas_empty) {
                        ArrayList<Bitmap> bitmaps_array = TempResources.getTempDrawingsArray();
                        if (bitmaps_array.size() > 0) {
                            is_drawings_empty = false;
                        }
                        file_name = getNewFileName(Doings.PNG);
                        // добавить изображение в заметку
                        Bitmap new_drawing = bitmaps_array.get(bitmaps_array.size() - 1);
                        int bg_color = TempResources.getTempBGsForDrawings()
                                .get(bitmaps_array.size() - 1);
                        addImageDrawingToNote(R.layout.drawing_layout,
                                file_name,
                                new_drawing, bg_color);

                        // добавление в массив
                        File file = PublicResources.createFile(file_name, Doings.PNG);
                        DrawingStructure catched_drawing = new DrawingStructure();
                        catched_drawing.setDrawingBg(bg_color);
                        catched_drawing.setDrawingFileName(file_name);
                        drawings.add(catched_drawing);
                        // добавление изображения в файл
                        Thread save_drawing = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                saveOrReplaceDrawing(file, new_drawing);
                                Log.d(PublicResources.DEBUG_LOG_TAG, ">>> Get it!");
                            }
                        });
                        save_drawing.start();
                    }
                    break;
                case PublicResources.REQUEST_EDIT_CANVAS:
                    int temp_index = intent
                            .getIntExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                                    PublicResources.EXTRA_DEFAULT_INT_VALUE);
                    Drawable img_bg = waiting_for_result.getBackground();
                    img_bg.setColorFilter(TempResources.getTempBGsForDrawings().get(temp_index),
                            PorterDuff.Mode.SRC_IN);
                    waiting_for_result.setBackground(img_bg);
                    waiting_for_result.setImageBitmap(TempResources
                            .getTempDrawingsArray().get(temp_index));

                    // замена записи в массиве
                    file_name = intent.getStringExtra(PublicResources.EXTRA_CANVAS_TAG);
                    File file = PublicResources.createFile(file_name, Doings.PNG);
                    DrawingStructure editing_drawing = new DrawingStructure();
                    editing_drawing.setDrawingBg(TempResources
                            .getTempBGsForDrawings().get(temp_index));
                    editing_drawing.setDrawingFileName(file_name);
                    for (int i = 0; i < drawings.size(); ++i) {
                        if (drawings.get(i).getDrawingFileName().equals(file_name)) {
                            drawings.set(i, editing_drawing);
                            break;
                        }
                    }
                    if (intent.getBooleanExtra(PublicResources.EXTRA_CANVAS_IS_EDIT,
                            !PublicResources.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE)) {
                        // добавление изображения в файл
                        Thread save_drawing = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                saveOrReplaceDrawing(file, TempResources
                                        .getTempDrawingsArray().get(temp_index));
                                Log.d(PublicResources.DEBUG_LOG_TAG, ">>> Get it!");
                            }
                        });
                        save_drawing.start();
                    }
                    break;
                default:
                    // nothing
            }
        }
    }

    private void clearPanelItems() {
        items_hsv.removeAllViews();
        items_hsv.setVisibility(View.GONE);
    }
    private void clearPinsPanel() {
        pins_sv.setVisibility(View.GONE);
    }

    // Заполнение

    private int[] fillColors() {
        return (new int[] {
                res.getColor(R.color.black, null),
                res.getColor(R.color.soft_cian, null),
                res.getColor(R.color.soft_grass, null),
                res.getColor(R.color.soft_lemon, null),
                res.getColor(R.color.soft_lime, null),
                res.getColor(R.color.soft_orange, null),
                res.getColor(R.color.soft_peach, null),
                res.getColor(R.color.soft_pink, null),

                res.getColor(R.color.dark_grass, null),
                res.getColor(R.color.dark_grey_blue, null),
                res.getColor(R.color.dark_night_sky, null),
                res.getColor(R.color.dark_orange, null),
                res.getColor(R.color.dark_pink, null),
                res.getColor(R.color.dark_sea_wave, null),
                res.getColor(R.color.dark_sky, null),
                res.getColor(R.color.dark_tomato, null),

                res.getColor(R.color.pastel_lavender, null),
                res.getColor(R.color.pastel_lilac, null),
                res.getColor(R.color.pastel_lime, null),
                res.getColor(R.color.pastel_mint, null),
                res.getColor(R.color.pastel_orange, null),
                res.getColor(R.color.pastel_pink_lilac, null),
                res.getColor(R.color.pastel_sky, null),

                res.getColor(R.color.shadow_brown, null),
                res.getColor(R.color.shadow_darkest_blue, null),
                res.getColor(R.color.shadow_deep_blue, null),
                res.getColor(R.color.shadow_magenta, null),
                res.getColor(R.color.shadow_purple, null),
                res.getColor(R.color.shadow_red, null),
                res.getColor(R.color.shadow_sea, null),

                res.getColor(R.color.mono_total_black, null),
                res.getColor(R.color.mono_dark_gray, null),
                res.getColor(R.color.mono_middle_grey, null),
                res.getColor(R.color.mono_medium_grey, null),
                res.getColor(R.color.mono_grey, null),
                res.getColor(R.color.mono_light_grey, null),
                res.getColor(R.color.mono_total_white, null),

                res.getColor(R.color.blue_total_black, null),
                res.getColor(R.color.blue_dark_gray, null),
                res.getColor(R.color.blue_middle_grey, null),
                res.getColor(R.color.blue_medium_grey, null),
                res.getColor(R.color.blue_grey, null),
                res.getColor(R.color.blue_light_grey, null),
                res.getColor(R.color.blue_total_white, null)});
    }

    // обработка добавления изображений
    private ImageView getImageDrawingForNote(int child_layout, Bitmap drawing,
                                             int bg_color, String tag) {
        // создание изображения
        ImageView drawing_img = (ImageView) inflater
                .inflate(child_layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (320 * PublicResources.DP));
        lp.bottomMargin = (int) (7 * PublicResources.DP);
        Drawable bg = drawing_img.getBackground();
        bg.setColorFilter(bg_color, PorterDuff.Mode.SRC_IN);
        drawing_img.setTag(tag);
        drawing_img.setBackground(bg);
        drawing_img.setLayoutParams(lp);
        drawing_img.setImageBitmap(drawing);
        // добавляем кастомизированный попап
        // Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuForDrawing);
//        PopupMenu popup = new PopupMenu(this, drawing_img);
//        popup.getMenuInflater()
//                .inflate(R.menu.popup_menu_for_image_drawing, popup.getMenu());
        // обработка действий при клике на PopupMenu
//        popup.setOnMenuItemClickListener(popup_cl);
        // обработка клика по рисунку
        drawing_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView temp = (ImageView) view;
                waiting_for_result = temp;
                TempResources.setTempDrawingFromCanvas(
                        ((BitmapDrawable) temp.getDrawable()).getBitmap());
//                popup.show();
                showPopupForDrawing(temp);
            }
        });
        return drawing_img;
    }

    // popup слушатель
    private PopupMenu.OnMenuItemClickListener getPopupMenuItemCl() {
        return (new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.edit_draw_btn) {
                    // получаем данные о рисунке и его положении в массиве
                    ArrayList<Bitmap> temp_drawings = TempResources.getTempDrawingsArray();
                    Bitmap draw_bmp = ((BitmapDrawable)
                            (waiting_for_result.getDrawable())).getBitmap();
                    // редактируем
                    Intent intent = new Intent(Note.this, CanvasActivity.class);
                    for (int index = 0; index < temp_drawings.size(); ++index) {
                        if (draw_bmp.equals(temp_drawings.get(index))) {
                            intent.putExtra(PublicResources.EXTRA_BG_CANVAS_COLOR,
                                    TempResources.getTempBGsForDrawings().get(index));
                            intent.putExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                                    index);
                            TempResources.setTempDrawingFromCanvas(temp_drawings.get(index));
                            break;
                        }
                    }
                    intent.putExtra(PublicResources.EXTRA_CANVAS_TAG,
                            waiting_for_result.getTag().toString());
                    intent.setAction(PublicResources.ACTION_EDIT_EXIST_CANVAS);
                    startActivityForResult(intent, PublicResources.REQUEST_EDIT_CANVAS);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.remove_draw_btn) {
                    // удаляем
                    String file_name = waiting_for_result.getTag().toString();
                    for (int i = 0; i < drawings.size(); ++i) {
                        if (drawings.get(i).getDrawingFileName().equals(file_name)) {
                            File file = PublicResources.createFile(file_name, Doings.PNG);
                            drawings.remove(i);
                            PublicResources.deleteFile(file);
                            waiting_for_result.setOnClickListener(null);
                            if (i < TempResources.getTempDrawingsArray().size()) {
                                TempResources.getTempDrawingsArray().remove(i);
                                TempResources.getTempBGsForDrawings().remove(i);
                            }
                            this_note_place_ll.removeView(waiting_for_result);
                            break;
                        }
                    }
                    Toast.makeText(Note.this,
                            "Удалили, пиу-пиу!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return true;
            }
        });
    }

    private EditText getEditMainTextForNote(int child_layout) {
        return (EditText) inflater.inflate(child_layout, null, false);
    }

    public void addImageDrawingToNote(int image_layout, String tag,
                                      Bitmap drawing, int bg_color) {
        ImageView drawing_img = getImageDrawingForNote(image_layout,
                drawing, bg_color, tag);
        // новые тектовые поля после рисунков
        // EditText main_text_place = getEditMainTextForNote(edit_text_layout);
        this_note_place_ll.addView(drawing_img);
        // this_note_place_ll.addView(main_text_place);
    }

    // очистка временного хранилища после успешного сохранения данных
    protected void clearTempResources() {
        TempResources.getTempBGsForDrawings().clear();
        TempResources.getTempDrawingsArray().clear();
    }

    /**********************************************************************************
     * работа с файлами (рисунки) */

    // добавить рисунок
    public void saveOrReplaceDrawing(File file, Bitmap bitmap) {
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
    }

    public String getNewFileName(Doings file_extension) {
        String extension = "_";
        switch (file_extension) {
            case GSON:
                extension = PublicResources.TEXT_GSON_PREFIX;
                break;
            case PNG:
                extension = PublicResources.IMAGE_PNG_PREFIX;
                break;
        }
        String time_stamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        return (extension + time_stamp + "_");
    }

    /**********************************************************************************
     * сохранение заметки */
    public NoteStructure getNewNote() {
        NoteStructure new_note = null;
        try {
            new_note = new NoteStructure();
            new_note.setFileName(getNewFileName(Doings.GSON));
            if (note_name.getText().length() == 0) {
                new_note.setName(getNewNoteName(note_main_text.getText().toString()));
            }
            else {
                new_note.setName(note_name.getText().toString());
            }
            new_note.setText(note_main_text.getText().toString());
            new_note.setPin(pin_ids[Integer.parseInt(pin_btn.getTag().toString())]);
            new_note.setBg(bg_color);
            new_note.setDrawings(drawings);
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
        return new_note;
    }

    /**********************************************************************************
     * инициализация пинов */
    private int[] getPinIds() {
        return new int[] {
                R.drawable.ic_address_book_icon, R.drawable.ic_alarm_clock_icon,
                R.drawable.ic_apple_icon, R.drawable.ic_avocado_icon,
                R.drawable.ic_baseball_alt_icon, R.drawable.ic_baby_carriage_icon,
                R.drawable.ic_backpack_icon, R.drawable.ic_balloons_icon,
                R.drawable.ic_bandage_icon, R.drawable.ic_bank_icon,
                R.drawable.ic_barber_shop_icon, R.drawable.ic_baseball_icon,
                R.drawable.ic_woman_head_icon, R.drawable.ic_beach_icon,
                R.drawable.ic_bed_icon, R.drawable.ic_beer_icon,
                R.drawable.ic_big_finger_down_icon, R.drawable.ic_big_finger_up_icon,
                R.drawable.ic_biking_icon, R.drawable.ic_birthday_cake_icon,
                R.drawable.ic_bolt_icon, R.drawable.ic_book_icon,
                R.drawable.ic_bookmark_icon, R.drawable.ic_briefcase_icon,
                R.drawable.ic_broom_icon, R.drawable.ic_browser_icon,
                R.drawable.ic_brush_icon, R.drawable.ic_bulb_icon,
                R.drawable.ic_clip_icon,
                R.drawable.ic_calculator_icon, R.drawable.ic_calendar_icon,
                R.drawable.ic_call_history_icon, R.drawable.ic_camera_icon,
                R.drawable.ic_car_mechanic_icon, R.drawable.ic_carrot_icon,
                R.drawable.ic_cars_icon,
                R.drawable.ic_cloud_rain_icon, R.drawable.ic_comment_info_icon,
                R.drawable.ic_credit_card_icon, R.drawable.ic_gem_icon,
                R.drawable.ic_gift_icon, R.drawable.ic_global_network_icon,
                R.drawable.ic_graduation_cap_icon, R.drawable.ic_headphones_icon,
                R.drawable.ic_heart_icon, R.drawable.ic_incognito_icon,
                R.drawable.ic_key_icon, R.drawable.ic_lock_icon,
                R.drawable.ic_map_marker_home_icon, R.drawable.ic_medal_icon,
                R.drawable.ic_paw_icon, R.drawable.ic_pineapple_icon,
                R.drawable.ic_portrait_icon, R.drawable.ic_recycle_icon,
                R.drawable.ic_rocket_lunch_icon, R.drawable.ic_shopping_cart_icon,
                R.drawable.ic_sparkles_icon, R.drawable.ic_stats_icon,
                R.drawable.ic_subway_icon, R.drawable.ic_thumbtack_pin_icon,
                R.drawable.ic_time_quarter_to_icon, R.drawable.ic_time_to_eat_icon,
                R.drawable.ic_tooth_icon, R.drawable.ic_user_icon,
                R.drawable.ic_users_icon, R.drawable.ic_video_icon,
                R.drawable.ic_basketball_icon
        };
    }

    /**********************************************************************************
     * МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        colors = null;
        pin_ids = null;
        for (int i = 0; i < this_note_place_ll.getChildCount(); ++i) {
            this_note_place_ll.getChildAt(i).setOnClickListener(null);
        }
    }
}