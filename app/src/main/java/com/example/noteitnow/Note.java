package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResourсes;
import com.example.noteitnow.statics_entity.TempResources;

import java.util.ArrayList;

public class Note extends AppCompatActivity implements View.OnClickListener {
    private EditText note_name, note_main_text;
    private ImageButton pin_btn;
    private ArrayList<Bitmap> drawings;
    private boolean is_drawings_empty;
    private Resources res;
    private int text_selection_start, text_selection_end;

    // inflater
    LayoutInflater inflater;

    // цвета
    private int current_text_color;
    private int current_text_bg_color;
    private int default_text_color;
    private static final int NO_SELECTION = -1;

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
    }

    private void initOnCreate() {
        // зануляем выделение
        text_selection_start = NO_SELECTION;
        text_selection_end = NO_SELECTION;

        // получение ресурсов
        res = getResources();

        // получение цветов
        default_text_color = res.getColor(R.color.black, null);
        current_text_bg_color = res.getColor(R.color.transparent_, null);
        current_text_color = default_text_color;

        // инициализация inflater
        inflater = getLayoutInflater();

        // инициализация панелек
        items_hsv = findViewById(R.id.items_hsv);
        pins_sv = findViewById(R.id.pins_sv);
        pins_ll = findViewById(R.id.pins_ll);
        this_note_place_ll = findViewById(R.id.this_note_place_ll);
        panel_views = new ArrayList<ExistView>();
        active_panel_item_bg = getDrawable(R.drawable.active_panel_item_btn);
        inactive_panel_item_bg = getDrawable(R.drawable.icons_bg);
        pin_btn = findViewById(R.id.pin_btn);

        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_color_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.bg_change_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.add_drawing_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.marker_list_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_background_color_btn)));

        // рисунков нет
        is_drawings_empty = true;
        drawings = new ArrayList<Bitmap>();

        for (ExistView btn : panel_views) {
            btn.getItem().setOnClickListener(this);
        }
        pin_btn.setOnClickListener(this);

        note_name = findViewById(R.id.note_name);
        note_main_text = findViewById(R.id.note_main_text);

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
                startActivityForResult(intent, PublicResourсes.REQUEST_NEW_CANVAS);
                break;
            case R.id.pin_btn:
                pins_sv.setVisibility(View.VISIBLE);
                View.OnClickListener cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TempResources.setTempPinIcon(((ImageButton) view).getDrawable());
                        pin_btn.setImageDrawable(TempResources.getTempPinIcon());
                        clearPinsPanel();
                    }
                };
                for (int line_number = 0; line_number < pins_ll.getChildCount(); ++line_number) {
                    LinearLayout line = (LinearLayout) pins_ll.getChildAt(line_number);
                    for (int pin_item_number = 0; pin_item_number < line.getChildCount();
                         ++pin_item_number) {
                        line.getChildAt(pin_item_number).setOnClickListener(cl);
                    }
                }
                break;
            default:
//                text_selection_start = note_main_text.getSelectionStart();
//                text_selection_end = note_main_text.getSelectionEnd();
//                Log.d(PublicResourсes.DEBUG_LOG_TAG,
//                        ">>> start selection: " + text_selection_start + "\n" +
//                                ">>> end selection: " + text_selection_end);
                // nothing
        }
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

    private void forActivityResult() {
        Intent intent = new Intent();
        // код обработки закрытия
        boolean is_empty = ((note_name.getText().length()
                + note_main_text.getText().length()) == 0) && is_drawings_empty;
        // Пустая заметка или нет
        intent.putExtra(PublicResourсes.EXTRA_NOTE_IS_EMPTY, is_empty);
        // Если не пустая, сообщить её название и пользовательский пин
        if (!is_empty) {
            String new_note_name;
            switch (note_name.getText().toString().length()) {
                case 0:
                    new_note_name = getNewNoteName(note_main_text.getText().toString());
                    break;
                default:
                    new_note_name = getNewNoteName(note_name.getText().toString());
            }
            intent.putExtra(PublicResourсes.EXTRA_NOTE_NAME, new_note_name);
            TempResources.setTempPinIcon(pin_btn.getDrawable());
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private String getNewNoteName(String text) {
        String new_note_name = "";
        final int MAX_CHARS_LEN = 24;
        final int MAX_CHARS_LEN_WITH_END_CHARS = 22;
        final String END_CHARS = "..";
        if (text.length() > 24) {
            for (int i = 0; i < MAX_CHARS_LEN_WITH_END_CHARS; ++i) {
                new_note_name += text.charAt(i);
            }
            new_note_name += END_CHARS;
            return new_note_name;
        }
        else if (text.length() == 0) {
            new_note_name = PublicResourсes.DEFAULT_NOTE_NAME;
            return new_note_name;
        }
        return text;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // получение рисунка
                case PublicResourсes.REQUEST_NEW_CANVAS:
                    boolean is_new_canvas_empty = intent
                            .getBooleanExtra(PublicResourсes.EXTRA_CANVAS_IS_EMPTY,
                            PublicResourсes.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
                    if (!is_new_canvas_empty) {
                        ArrayList<Bitmap> bitmaps_array = TempResources.getTempDrawingsArray();
                        if (bitmaps_array.size() > 0) {
                            is_drawings_empty = false;
                        }
                        // добавить изображение в заметку
                        Bitmap new_drawing = bitmaps_array.get(bitmaps_array.size() - 1);
                        int bg_color = TempResources.getTempBGsForDrawings()
                                .get(bitmaps_array.size() - 1);
                        addImageDrawingToNote(R.layout.drawing_layout,
                                R.layout.note_main_text_layout,
                                new_drawing, bg_color);
                    }
                    break;
                case PublicResourсes.REQUEST_EDIT_CANVAS:
                    int temp_index = intent
                            .getIntExtra(PublicResourсes.EXTRA_DRAWING_TEMP_INDEX,
                            PublicResourсes.EXTRA_DEFAULT_INT_VALUE);
                    Drawable img_bg = waiting_for_result.getBackground();
                    img_bg.setColorFilter(TempResources.getTempBGsForDrawings().get(temp_index),
                            PorterDuff.Mode.SRC_IN);
                    waiting_for_result.setBackground(img_bg);
                    waiting_for_result.setImageBitmap(TempResources
                            .getTempDrawingsArray().get(temp_index));
                    break;
                default:
                    // nothing
            }
        }
    }

    // выбор цвета для заднего фона или текста
    private void setColorChooserForPanel(ImageButton panel_element,
                                         Doings panel_element_color) {
        // nothing
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
                                             int bg_color) {
        // создание изображения
        ImageView drawing_img = (ImageView) inflater
                .inflate(child_layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (320 * PublicResourсes.DP));
        lp.bottomMargin = (int) (7 * PublicResourсes.DP);
        Drawable bg = drawing_img.getBackground();
        bg.setColorFilter(bg_color, PorterDuff.Mode.SRC_IN);
        drawing_img.setBackground(bg);
        drawing_img.setLayoutParams(lp);
        drawing_img.setImageBitmap(drawing);
        // добавляем кастомизированный попап
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuForDrawing);
        PopupMenu popup = new PopupMenu(wrapper, drawing_img);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu_for_image_drawing, popup.getMenu());
        // обработка действий при клике на PopupMenu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // получаем данные об рисунке и его положении в массиве
                ArrayList<Bitmap> temp_drawings = TempResources.getTempDrawingsArray();
                Bitmap draw_bmp = ((BitmapDrawable)
                        (waiting_for_result.getDrawable())).getBitmap();
                switch (menuItem.getItemId()) {
                    case R.id.edit_draw_btn:
                        // редактируем
                        Intent intent = new Intent(Note.this, CanvasActivity.class);

                        for (int index = 0; index < temp_drawings.size(); ++index) {
                            if (draw_bmp.equals(temp_drawings.get(index))) {
                                intent.putExtra(PublicResourсes.EXTRA_BG_CANVAS_COLOR,
                                        TempResources.getTempBGsForDrawings().get(index));
                                intent.putExtra(PublicResourсes.EXTRA_DRAWING_TEMP_INDEX,
                                        index);
                                TempResources.setTempDrawingFromCanvas(temp_drawings.get(index));
                                break;
                            }
                        }
                        intent.setAction(PublicResourсes.ACTION_EDIT_EXIST_CANVAS);
                        startActivityForResult(intent, PublicResourсes.REQUEST_EDIT_CANVAS);
                        return true;
                    case R.id.remove_draw_btn:
                        // удаляем
                        for (int index = 0; index < temp_drawings.size(); ++index) {
                            if (draw_bmp.equals(temp_drawings.get(index))) {
                                TempResources.getTempDrawingsArray().remove(index);
                                TempResources.getTempBGsForDrawings().remove(index);
                                this_note_place_ll.removeView(waiting_for_result);

                                // слияние текстовых частей
//                                int views_count = this_note_place_ll.getChildCount();
//                                for (int i = 0; i < views_count; ++i) {
//                                    if (this_note_place_ll.getChildAt(i) == waiting_for_result) {
//                                        // объединить текстовые поля в одно и удалить
//                                    }
//                                }
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
        // обработка клика по рисунку
        drawing_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView temp = (ImageView) view;
                waiting_for_result = temp;
                TempResources.setTempDrawingFromCanvas(
                        ((BitmapDrawable) temp.getDrawable()).getBitmap());
                popup.show();
            }
        });
        return drawing_img;
    }

    private EditText getEditMainTextForNote(int child_layout) {
        return (EditText) inflater.inflate(child_layout, null, false);
    }

    public void addImageDrawingToNote(int image_layout, int edit_text_layout,
                                      Bitmap drawing, int bg_color) {
        ImageView drawing_img = getImageDrawingForNote(image_layout,
                drawing, bg_color);
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
}