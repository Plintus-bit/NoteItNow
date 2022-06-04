package com.example.noteitnow.statics_entity.items;

import android.content.res.Resources;

import com.example.noteitnow.R;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Months {
    private Map<Integer, Integer> month_dict = new HashMap<Integer, Integer>();

    public Months() {
        month_dict.put(1, R.string.january);
        month_dict.put(2, R.string.february);
        month_dict.put(3, R.string.march);
        month_dict.put(4, R.string.april);
        month_dict.put(5, R.string.may);
        month_dict.put(6, R.string.june);
        month_dict.put(7, R.string.july);
        month_dict.put(8, R.string.august);
        month_dict.put(9, R.string.september);
        month_dict.put(10, R.string.october);
        month_dict.put(11, R.string.november);
        month_dict.put(12, R.string.december);
    }

    public Integer getMonth(int key) {
        return month_dict.get(key);
    }
}
