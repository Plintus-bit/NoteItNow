package com.example.noteitnow.statics_entity.items;

import android.content.res.Resources;

import com.example.noteitnow.R;
import com.example.noteitnow.statics_entity.Doings;

public class ColorItems extends Items {

    public ColorItems(Resources res) {
        super(res);
    }

    public int[] getAllColors() {
        return new int[] {
                res.getColor(R.color.transparent_, null),
                res.getColor(R.color.black, null),
                res.getColor(R.color.mono_total_white, null),
                res.getColor(R.color.soft_cian, null),
                res.getColor(R.color.soft_grass, null),
                res.getColor(R.color.soft_lemon, null),
                res.getColor(R.color.soft_lime, null),
                res.getColor(R.color.soft_orange, null),
                res.getColor(R.color.soft_peach, null),
                res.getColor(R.color.soft_pink, null),

                res.getColor(R.color.pastel_marker_yellow, null),
                res.getColor(R.color.pastel_marker_orange, null),
                res.getColor(R.color.pastel_marker_magenta, null),
                res.getColor(R.color.pastel_marker_pink, null),
                res.getColor(R.color.pastel_marker_purple, null),
                res.getColor(R.color.pastel_marker_blue, null),
                res.getColor(R.color.pastel_marker_green, null),
                res.getColor(R.color.pastel_marker_lime, null),


                res.getColor(R.color.dark_grass, null),
                res.getColor(R.color.dark_grey_blue, null),
                res.getColor(R.color.dark_night_sky, null),
                res.getColor(R.color.dark_orange, null),
                res.getColor(R.color.dark_pink, null),
                res.getColor(R.color.dark_sea_wave, null),
                res.getColor(R.color.dark_sky, null),
                res.getColor(R.color.dark_tomato, null),

                res.getColor(R.color.programming_blue, null),
                res.getColor(R.color.programming_green, null),
                res.getColor(R.color.programming_lime, null),
                res.getColor(R.color.programming_purple, null),

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

                res.getColor(R.color.blue_total_black, null),
                res.getColor(R.color.blue_dark_gray, null),
                res.getColor(R.color.blue_middle_grey, null),
                res.getColor(R.color.blue_medium_grey, null),
                res.getColor(R.color.blue_grey, null),
                res.getColor(R.color.blue_light_grey, null),
                res.getColor(R.color.blue_total_white, null)};
    }

    public int[] getColorsWithoutTransparent() {
        return new int[] {
                res.getColor(R.color.black, null),
                res.getColor(R.color.mono_total_white, null),
                res.getColor(R.color.soft_cian, null),
                res.getColor(R.color.soft_grass, null),
                res.getColor(R.color.soft_lemon, null),
                res.getColor(R.color.soft_lime, null),
                res.getColor(R.color.soft_orange, null),
                res.getColor(R.color.soft_peach, null),
                res.getColor(R.color.soft_pink, null),

                res.getColor(R.color.pastel_marker_yellow, null),
                res.getColor(R.color.pastel_marker_orange, null),
                res.getColor(R.color.pastel_marker_magenta, null),
                res.getColor(R.color.pastel_marker_pink, null),
                res.getColor(R.color.pastel_marker_purple, null),
                res.getColor(R.color.pastel_marker_blue, null),
                res.getColor(R.color.pastel_marker_green, null),
                res.getColor(R.color.pastel_marker_lime, null),

                res.getColor(R.color.dark_grass, null),
                res.getColor(R.color.dark_grey_blue, null),
                res.getColor(R.color.dark_night_sky, null),
                res.getColor(R.color.dark_orange, null),
                res.getColor(R.color.dark_pink, null),
                res.getColor(R.color.dark_sea_wave, null),
                res.getColor(R.color.dark_sky, null),
                res.getColor(R.color.dark_tomato, null),

                res.getColor(R.color.programming_blue, null),
                res.getColor(R.color.programming_green, null),
                res.getColor(R.color.programming_lime, null),
                res.getColor(R.color.programming_purple, null),

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

                res.getColor(R.color.blue_total_black, null),
                res.getColor(R.color.blue_dark_gray, null),
                res.getColor(R.color.blue_middle_grey, null),
                res.getColor(R.color.blue_medium_grey, null),
                res.getColor(R.color.blue_grey, null),
                res.getColor(R.color.blue_light_grey, null),
                res.getColor(R.color.blue_total_white, null)};
    }

    public int[] getColors(Doings what) {
        switch (what) {
            case PINNED:
                return new int[] {
                        res.getColor(R.color.pinned_green),
                        res.getColor(R.color.pinned_blue),
                        res.getColor(R.color.pinned_orange),
                        res.getColor(R.color.pinned_pink),
                        res.getColor(R.color.pinned_purple),
                        res.getColor(R.color.pinned_red),
                        res.getColor(R.color.pinned_sky)
                };
            case PASTEL_SOFT_PINNED:
                return new int[] {
                        res.getColor(R.color.soft_cian),
                        res.getColor(R.color.pastel_lavender),
                        res.getColor(R.color.soft_grass),
                        res.getColor(R.color.pastel_lilac),
                        res.getColor(R.color.soft_orange),
                        res.getColor(R.color.pastel_orange),
                        res.getColor(R.color.soft_peach),
                        res.getColor(R.color.soft_pink),
                        res.getColor(R.color.pastel_pink_lilac)
                };
            default:
                return null;
        }
    }


}
