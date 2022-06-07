package com.example.noteitnow.statics_entity.items;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.example.noteitnow.R;
import com.example.noteitnow.statics_entity.PublicResources;

import java.util.ArrayList;
import java.util.Arrays;

public class DataItems extends Items {
    public DataItems(Resources res) {
        super(res);
    }

    public int[] getDrawableItemsIds() {
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

    public int[] getOpacityIds() {
        return new int[] {
                (int) (10 * PublicResources.ALPHA / 100),
                (int) (20 * PublicResources.ALPHA / 100),
                (int) (40 * PublicResources.ALPHA / 100),
                (int) (60 * PublicResources.ALPHA / 100),
                (int) (80 * PublicResources.ALPHA / 100)};
    }

    public int[] getBrushWidthIds() {
        return new int[] {
                4, 6, 8, 12, 16, 24, 30, 36, 52
        };
    }

    public int[] getMarkerWidthIds() {
        return new int[] {
                12, 16, 24, 30, 36, 52, 72, 100
        };
    }

    protected ArrayList<Drawable> getBgPatternDrawable() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.bg_grid_large, null));
        draw_list.add(res.getDrawable(R.drawable.bg_grid_medium, null));
        draw_list.add(res.getDrawable(R.drawable.bg_line_large, null));
        draw_list.add(res.getDrawable(R.drawable.bg_line_medium, null));
        return draw_list;
    }

    protected ArrayList<Drawable> getBgPatternIcons() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_bg_grid_large_icon, null));
        draw_list.add(res.getDrawable(R.drawable.ic_bg_grid_medium_icon, null));
        draw_list.add(res.getDrawable(R.drawable.ic_bg_line_large_icon, null));
        draw_list.add(res.getDrawable(R.drawable.ic_bg_line_medium_icon, null));
        return draw_list;
    }

    public ArrayList<ArrayList<Drawable>> getBgPatterns() {
        ArrayList<ArrayList<Drawable>> draw_list = new ArrayList<>();
        draw_list.add(getBgPatternDrawable());
        draw_list.add(getBgPatternIcons());
        return draw_list;
    }

    public ArrayList<Drawable> getBrushDrawable() {
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

    public ArrayList<Drawable> getMarkerDrawable() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_12px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_16px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_24px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_30px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_36px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_52px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_72px_sq, null));
        draw_list.add(res.getDrawable(R.drawable.ic_100px_sq, null));
        return draw_list;
    }

    public ArrayList<Drawable> getOpacityDrawable() {
        ArrayList<Drawable> draw_list = new ArrayList<Drawable>();
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_10, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_20, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_40, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_60, null));
        draw_list.add(res.getDrawable(R.drawable.ic_opacity_80, null));
        return draw_list;
    }
}
