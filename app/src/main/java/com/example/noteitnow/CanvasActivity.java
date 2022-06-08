package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.noteitnow.notes_entities.DrawingStructure;
import com.example.noteitnow.notes_entities.NoteSavings;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.statics_entity.PublicResources;

import java.io.File;
import java.util.ArrayList;

import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.TempResources;
import com.example.noteitnow.statics_entity.items.ColorItems;
import com.example.noteitnow.statics_entity.items.DataItems;


public class CanvasActivity extends AppCompatActivity implements View.OnClickListener {
    // Массивы
    int[] colors;
    int[] width_ids;
    int[] opacity_ids;
    ArrayList<Drawable> brush_width_icons;
    ArrayList<Drawable> marker_width_icons;
    ArrayList<Drawable> marker_opacity_icons;

    NoteStructure current_note;

    // тег текущего рисунка
    private String tag;

    // элементы для панели инструментов
    ColorItems color_items;
    DataItems data_items;

    // inflater и ресурсы
    LayoutInflater inflater;
    Resources res;

    private RelativeLayout main_rl;
    private DrawingView draw_view;
    private ArrayList<ImageButton> panel_buttons;
    private Drawable active_panel_item_bg;
    private Drawable inactive_panel_item_bg;
    private ImageButton current_active_panel_btn;
    private HorizontalScrollView items_hsv,
            tools_panel_hsv;

    // Временные элементы этого класса
    private ImageButton current_active_item;
    private Doings canvas_state;
    Intent catched_result_intent;

    // текущий инструмент
    private int current_tool_id;

    // Сеттеры и геттеры

    private void setCurrentActivePanelBtn(ImageButton btn) {
        current_active_panel_btn = btn;
    }
    private ImageButton getCurrentActivePanelBtn() {
        return current_active_panel_btn;
    }

    // Методы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        canvas_state = Doings.NEW_CANVAS;
        tag = PublicResources.EXTRA_DEFAULT_STRING_VALUE;
        current_note = getIntent().getParcelableExtra(PublicResources.EXTRA_NOTE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initOnCreate();

        setCurrentColorOnPanel(panel_buttons.get(4));

        getIntentFromNote();

        setThemePropertiesToNote();
    }

    private void setThemePropertiesToNote() {
        boolean is_darcula_active = PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false);
        if (is_darcula_active) {
            tools_panel_hsv
                    .setBackgroundColor(res.getColor(R.color.mono_middle_grey, null));
            items_hsv.setBackgroundColor(res.getColor(R.color.blue_middle_grey, null));
            active_panel_item_bg
                    .setColorFilter(res.getColor(R.color.mono_medium_grey, null),
                            PorterDuff.Mode.SRC_IN);
        }
    }

    private void getIntentFromNote() {
        catched_result_intent = getIntent();
        if (catched_result_intent != null) {
            String action = catched_result_intent.getAction();
            if (action.equals(PublicResources.ACTION_EDIT_EXIST_CANVAS)) {
                canvas_state = Doings.EXIST_CANVAS;
                tag = catched_result_intent.getStringExtra(PublicResources.EXTRA_CANVAS_TAG);
                int index = catched_result_intent.getIntExtra(
                        PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                        PublicResources.EXTRA_DEFAULT_INT_VALUE);
                Bitmap bmp = TempResources.getTempDrawingsArray()
                        .get(index);
                draw_view.setDrawing(bmp, catched_result_intent
                        .getIntExtra(PublicResources.EXTRA_BG_CANVAS_COLOR,
                                PublicResources.DEFAULT_BG_COLOR));
            }
            else {
                tag = "";
            }
        }
    }

    public void initOnCreate() {
        // инициализация inflater
        res = getResources();
        inflater = getLayoutInflater();

        color_items = new ColorItems(res);
        data_items = new DataItems(res);

        // устанавливаем цвета по умолчанию
        PublicResources.DEFAULT_COLOR = res.getColor(R.color.blue_total_black, null);
        PublicResources.DEFAULT_BG_COLOR = res.getColor(R.color.transparent_, null);

        // инициализация для рисования
        main_rl = findViewById(R.id.main_place_rl);
        draw_view = new DrawingView(this,
                (int) (PublicResources.device_height
                        - PublicResources.SYSTEM_PANEL_HEIGHT * PublicResources.DP));
        main_rl.addView(draw_view);

        // Делаем Color List
        colors = fillIds(Doings.COLOR);
        opacity_ids = fillIds(Doings.OPACITY);
        brush_width_icons = fillIcons(Doings.BRUSH);
        marker_width_icons = fillIcons(Doings.MARKER);
        marker_opacity_icons = fillIcons(Doings.OPACITY);

        // инициализация кнопок на панели и их состояний
        items_hsv = findViewById(R.id.items_hsv);
        tools_panel_hsv = findViewById(R.id.tools_panel_hsv);
        active_panel_item_bg = getDrawable(R.drawable.active_panel_item_btn);
        inactive_panel_item_bg = getDrawable(R.drawable.icons_bg);
        panel_buttons = new ArrayList<ImageButton>();

        panel_buttons.add((ImageButton) findViewById(R.id.draw_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.eraser_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.draw_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.marker_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.bg_change_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.marker_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.stroke_width_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.marker_stroke_width_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.marker_opacity_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.clear_btn));

        for (ImageButton btn : panel_buttons) {
            btn.setOnClickListener(this);
        }

        panel_buttons.get(0).setBackground(active_panel_item_bg);
        setCurrentActivePanelBtn(panel_buttons.get(0));

        // текущий инструмент не выбран
        current_tool_id = PublicResources.DEFAULT_ID;
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
    public void onClick(View view) {
// код обработки кликов
        if (view == getCurrentActivePanelBtn()) {
            // Если эта кнопка уже активна, нет смысла что-то менять
            // Log.d(PublicResourсes.DEBUG_LOG_TAG, "It's already the current button.");
            if (items_hsv.getVisibility() == View.VISIBLE) {
                clearPanelItems();
            }
            return;
        }
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
            if (current_tool_id == view.getId()) {
                return;
            }
        }
        switch (view.getId()) {
            case R.id.draw_btn:
                // будем отслеживать изменения на холсте
                swapActivePanelItem(view);
                draw_view.setCurrentBrush(Doings.BRUSH);
                break;
            case R.id.marker_btn:
                // будем отслеживать изменения на холсте
                swapActivePanelItem(view);
                draw_view.setCurrentBrush(Doings.MARKER);
                break;
            case R.id.eraser_btn:
                // будем отслеживать изменения на холсте
                swapActivePanelItem(view);
                draw_view.setCurrentBrush(Doings.ERASER);
                break;
            case R.id.draw_color_btn:
                setColorChooserForPanelElement(panel_buttons.get(2), Doings.BRUSH);
                current_tool_id = view.getId();
                break;
            case R.id.marker_color_btn:
                setColorChooserForPanelElement(panel_buttons.get(3), Doings.MARKER);
                current_tool_id = view.getId();
                break;
            case R.id.bg_change_btn:
                setColorChooserForPanelElement(panel_buttons.get(4), Doings.BACKGROUND);
                current_tool_id = view.getId();
                break;
            case R.id.stroke_width_btn:
                width_ids = fillIds(Doings.BRUSH);
                setItemsChooserForPanelElement(panel_buttons.get(6), Doings.BRUSH);
                current_tool_id = view.getId();
                break;
            case R.id.marker_stroke_width_btn:
                width_ids = fillIds(Doings.MARKER);
                setItemsChooserForPanelElement(panel_buttons.get(7), Doings.MARKER);
                current_tool_id = view.getId();
                break;
            case R.id.marker_opacity_btn:
                setItemsChooserForPanelElement(panel_buttons.get(8), Doings.OPACITY);
                current_tool_id = view.getId();
                break;
            case R.id.clear_btn:
                draw_view.clearCanvas();
                break;
            default:

        }
    }

    // выбор цвета для рисования
    private void setColorChooserForPanelElement(ImageButton panel_element,
                                                Doings panel_element_color) {
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
        }
        items_hsv.setVisibility(View.VISIBLE);
        LinearLayout ll = PublicResources
                .getLLPanelWithColors(inflater, R.layout.popup_menu_layout, colors);
        items_hsv.addView(ll);
        View.OnClickListener color_cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d(PublicResourсes.DEBUG_LOG_TAG, String.valueOf(view.getId()));
                draw_view.setColor(view.getId(), panel_element_color);
                setCurrentColorOnPanel(panel_element);
                clearPanelItems();
            }
        };
        for (int i = 0; i < ll.getChildCount(); ++i) {
            (ll.getChildAt(i)).setOnClickListener(color_cl);
        }
        if (panel_element_color == Doings.BACKGROUND) {
            PublicResources.createCancelItem(inflater, R.layout.popup_menu_default_item_layout,
                    ll, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            draw_view.setColor(res.getColor(R.color.transparent_, null),
                                    panel_element_color);
                            setCurrentColorOnPanel(panel_element);
                            clearPanelItems();
                        }
                    });
        }
    }

    private void setItemsChooserForPanelElement(ImageButton panel_element,
                                                Doings draw_tool) {
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
        }
        items_hsv.setVisibility(View.VISIBLE);
        LinearLayout ll = getLayout(draw_tool);
        View.OnClickListener cl = getListener(panel_element, draw_tool);

        for (int i = 0; i < ll.getChildCount(); ++i) {
            (ll.getChildAt(i)).setOnClickListener(cl);
        }
    }

    // получение определённой разметки
    private LinearLayout getLayout(Doings draw_tool) {
        LinearLayout ll = null;
        switch (draw_tool) {
            case OPACITY:
                ll = PublicResources.getLLPanelWithItems(inflater,
                        R.layout.popup_menu_layout, opacity_ids, marker_opacity_icons);
                break;
            case MARKER:
                ll = PublicResources.getLLPanelWithItems(inflater,
                    R.layout.popup_menu_layout, width_ids, marker_width_icons);
                break;
            case BRUSH:
                ll = PublicResources.getLLPanelWithItems(inflater,
                        R.layout.popup_menu_layout, width_ids, brush_width_icons);
                break;
            default:
                // nothing
        }
        items_hsv.addView(ll);
        return ll;
    }
    // получение определённого слушателя кликов
    private View.OnClickListener getListener(ImageButton panel_element, Doings draw_tool) {
        View.OnClickListener cl;
        switch (draw_tool) {
            case OPACITY:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        draw_view.setOpacity(view.getId(), draw_tool);
                        setCurrentItemOnPanel(panel_element);
                        clearPanelItems();
                    }
                };
                break;
            default:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        draw_view.setStroke(view.getId(), draw_tool);
                        setCurrentItemOnPanel(panel_element);
                        clearPanelItems();
                    }
                };
                break;
            }
        return cl;
    }

    private void clearPanelItems() {
        items_hsv.removeAllViews();
        items_hsv.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        forActivityResult();
    }

    private void swapActivePanelItem(View view) {
        try {
            getCurrentActivePanelBtn().setBackground(inactive_panel_item_bg);
            view.setBackground(active_panel_item_bg);
            setCurrentActivePanelBtn((ImageButton) view);
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
        }
    }

    private void setCurrentItemOnPanel(ImageButton panel_element) {
        switch (panel_element.getId()) {
            case R.id.stroke_width_btn:
                int stroke_width = (int) (draw_view.getStroke(Doings.BRUSH));
                for (int i = 0; i < width_ids.length; ++i) {
                    if (width_ids[i] == stroke_width) {
                        panel_element.setImageDrawable(brush_width_icons.get(i));
                        break;
                    }
                }
                break;
            case R.id.marker_stroke_width_btn:
                int marker_stroke_width = (int) (draw_view.getStroke(Doings.MARKER));
                for (int i = 0; i < width_ids.length; ++i) {
                    if (width_ids[i] == marker_stroke_width) {
                        panel_element.setImageDrawable(marker_width_icons.get(i));
                        break;
                    }
                }
                break;
            case R.id.marker_opacity_btn:
                int marker_opacity = draw_view.getOpacity(Doings.OPACITY);
                for (int i = 0; i < opacity_ids.length; ++i) {
                    if (opacity_ids[i] == marker_opacity) {
                        panel_element.setImageDrawable(marker_opacity_icons.get(i));
                        break;
                    }
                }
                break;
            default:
                // nothing
        }
    }

    private void setCurrentColorOnPanel(ImageButton panel_element) {
        switch (panel_element.getId()) {
            case R.id.draw_color_btn:
                panel_element.setColorFilter(draw_view
                    .getColor(Doings.BRUSH), PorterDuff.Mode.SRC_IN);
                break;
            case R.id.marker_color_btn:
                panel_element.setColorFilter(draw_view
                        .getColor(Doings.MARKER), PorterDuff.Mode.SRC_IN);
                break;
            case R.id.bg_change_btn:
                panel_element.setColorFilter(draw_view
                        .getColor(Doings.BACKGROUND), PorterDuff.Mode.SRC_IN);
                break;
        }
    }

    // Заполнение
    private int[] fillIds(Doings tool) {
        switch (tool) {
            case BRUSH:
                return data_items.getBrushWidthIds();
            case MARKER:
                return data_items.getMarkerWidthIds();
            case OPACITY:
                return data_items.getOpacityIds();
            case COLOR:
                return color_items.getColorsWithoutTransparent();
        }
        return null;
    }

    private ArrayList<Drawable> fillIcons(Doings tool) {
        switch (tool) {
            case BRUSH:
                return data_items.getBrushDrawable();
            case MARKER:
                return data_items.getMarkerDrawable();
            case OPACITY:
                return data_items.getOpacityDrawable();
        }
        return null;
    }

    private void forActivityResult() {
        Intent intent = new Intent();
        if (canvas_state == Doings.EXIST_CANVAS) {
            // если пользователь стёр содержимое холста
            intent.putExtra(PublicResources.EXTRA_CANVAS_IS_EMPTY, draw_view.isCanvasEmpty());
            intent.putExtra(PublicResources.EXTRA_CANVAS_IS_EDIT, draw_view.isCanvasEdit());
//            intent.putExtra(PublicResources.EXTRA_NOTE_TAG, tag);
            // получаем индекс из временного массива
            int temp_index = catched_result_intent
                    .getIntExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                            PublicResources.EXTRA_DEFAULT_INT_VALUE);
            TempResources.getTempBGsForDrawings().remove(temp_index);
            TempResources.getTempBGsForDrawings().add(draw_view.getColor(Doings.BACKGROUND));
            TempResources.getTempDrawingsArray().remove(temp_index);
            TempResources.getTempDrawingsArray().add(draw_view.getDrawing());
            // поменяли индекс
            TempResources.setTempDrawingFromCanvas(draw_view.getDrawing());
            intent.putExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                    TempResources.getTempBGsForDrawings().size() - 1);

        }
        else {
            boolean is_canvas_empty = draw_view.isCanvasEmpty();
            intent.putExtra(PublicResources.EXTRA_CANVAS_IS_EMPTY, is_canvas_empty);
            if (!is_canvas_empty) {
                // если не пустой, добавить во временное хранилище для текущей заметки;
                TempResources.getTempBGsForDrawings()
                        .add(draw_view.getColor(Doings.BACKGROUND));
                TempResources.getTempDrawingsArray().add(draw_view.getDrawing());
            }
        }
        intent.putExtra(PublicResources.EXTRA_CANVAS_TAG, tag);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStop() {
//        if (tag.equals(PublicResources.EXTRA_DEFAULT_STRING_VALUE)) {
//            tag = NoteSavings.getNewFileName(Doings.PNG);
//        }
//        if (draw_view.isCanvasEmpty()) {
//            // добавление в массив
//            File file = PublicResources.createFile(tag, Doings.PNG);
//            DrawingStructure drawing = new DrawingStructure();
//            drawing.setDrawingBg(draw_view.getColor(Doings.BACKGROUND));
//            drawing.setDrawingFileName(tag);
//            ArrayList<DrawingStructure> drawings = current_note.getDrawings();
//            drawings.add(drawing);
//            current_note.setDrawings(drawings);
//            // добавление изображения в файл
//            Thread save_drawing = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    NoteSavings.saveOrReplaceDrawing(file, draw_view.getDrawing());
//                    NoteSavings.saveNote(current_note);
//                }
//            });
//            save_drawing.start();
//        }
        super.onStop();
    }
}