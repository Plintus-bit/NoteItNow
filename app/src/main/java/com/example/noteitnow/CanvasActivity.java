package com.example.noteitnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.noteitnow.statics_entity.PublicResourсes;

import java.util.ArrayList;

import com.example.noteitnow.statics_entity.Doings;


public class CanvasActivity extends AppCompatActivity implements View.OnClickListener {
    // Массивы
    int[] colors;
    int[] width_ids;
    int[] opacity_ids;
    ArrayList<Drawable> width_icons;
    ArrayList<Drawable> marker_width_icons;
    ArrayList<Drawable> marker_opacity_icons;

    // inflater и ресурсы
    LayoutInflater inflater;
    Resources res;

    private RelativeLayout main_rl;
    private DrawingView draw_view;
    private ArrayList<ImageButton> panel_buttons;
    private Drawable active_panel_item_bg;
    private Drawable inactive_panel_item_bg;
    private ImageButton current_active_panel_btn;
    private HorizontalScrollView items_hsv;

    // Временные элементы этого класса
    private ImageButton current_active_item;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initOnCreate();

        setCurrentColorOnPanel(panel_buttons.get(4));
    }

    public void initOnCreate() {
        // инициализация inflater
        res = getResources();
        inflater = getLayoutInflater();

        // устанавливаем цвета по умолчанию
        PublicResourсes.DEFAULT_COLOR = res.getColor(R.color.blue_total_black, null);
        PublicResourсes.DEFAULT_BG_COLOR = res.getColor(R.color.transparent_, null);

        // инициализация для рисования
        main_rl = findViewById(R.id.main_place_rl);
        draw_view = new DrawingView(this);
        main_rl.addView(draw_view);

        // Делаем Color List
        colors = fillColors();
        width_ids = fillStrokeWidthIds();
        opacity_ids = fillOpacityIds();
        width_icons = fillStrokeWidthIcons();
        marker_width_icons = fillMarkerStrokeWidthIcons();
        marker_opacity_icons = fillMarkerOpacityIcons();

        // инициализация кнопок на панели и их состояний
        items_hsv = findViewById(R.id.items_hsv);
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

        for (ImageButton btn : panel_buttons) {
            btn.setOnClickListener(this);
        }

        panel_buttons.get(0).setBackground(active_panel_item_bg);
        setCurrentActivePanelBtn(panel_buttons.get(0));
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
                break;
            case R.id.marker_color_btn:
                setColorChooserForPanelElement(panel_buttons.get(3), Doings.MARKER);
                break;
            case R.id.bg_change_btn:
                setColorChooserForPanelElement(panel_buttons.get(4), Doings.BACKGROUND);
                break;
            case R.id.stroke_width_btn:
                setItemsChooserForPanelElement(panel_buttons.get(6), Doings.BRUSH);
                break;
            case R.id.marker_stroke_width_btn:
                setItemsChooserForPanelElement(panel_buttons.get(7), Doings.MARKER);
                break;
            case R.id.marker_opacity_btn:
                setItemsChooserForPanelElement(panel_buttons.get(8), Doings.OPACITY);
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
        LinearLayout ll = PublicResourсes
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
                ll = PublicResourсes.getLLPanelWithItems(inflater,
                        R.layout.popup_menu_layout, opacity_ids, marker_opacity_icons);
                break;
            case MARKER:
                ll = PublicResourсes.getLLPanelWithItems(inflater,
                    R.layout.popup_menu_layout, width_ids, marker_width_icons);
                break;
            case BRUSH:
                ll = PublicResourсes.getLLPanelWithItems(inflater,
                        R.layout.popup_menu_layout, width_ids, width_icons);
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
        super.onBackPressed();
        finish();
    }

    private void swapActivePanelItem(View view) {
        try {
            getCurrentActivePanelBtn().setBackground(inactive_panel_item_bg);
            view.setBackground(active_panel_item_bg);
            setCurrentActivePanelBtn((ImageButton) view);
        } catch (Exception e) {
            Log.d(PublicResourсes.DEBUG_LOG_TAG, e.getMessage());
        }
    }

    private void setCurrentItemOnPanel(ImageButton panel_element) {
        switch (panel_element.getId()) {
            case R.id.stroke_width_btn:
                int stroke_width = (int) (draw_view.getStroke(Doings.BRUSH));
                for (int i = 0; i < width_ids.length; ++i) {
                    if (width_ids[i] == stroke_width) {
                        panel_element.setImageDrawable(width_icons.get(i));
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
                for (int i = 0; i < width_ids.length; ++i) {
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
        }
    }

    // Заполнение

    private int[] fillColors() {
        return (new int[] {
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

    private int[] fillStrokeWidthIds() {
        return (new int[] {4, 6, 8, 12, 16, 24, 30, 36, 52});
    }

    private int[] fillOpacityIds() {
        return (new int[] {10, 20, 40, 60, 80});
    }

    private ArrayList<Drawable> fillStrokeWidthIcons() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_4px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_6px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_8px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_12px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_16px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_24px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_30px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_36px, null));
        draw_list.add(res.getDrawable(R.drawable.ic_52px, null));
        return draw_list;
    }

    private ArrayList<Drawable> fillMarkerStrokeWidthIcons() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_4px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_6px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_8px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_12px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_16px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_24px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_30px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_36px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_52px_sq, null));
        return draw_list;
    }

    private ArrayList<Drawable> fillMarkerOpacityIcons() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_10, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_20, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_40, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_60, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_80, null));
        return draw_list;
    }
}