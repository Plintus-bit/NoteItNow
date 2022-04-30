package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.noteitnow.statics_entity.PublicResourсes;
import com.example.noteitnow.statics_entity.TempResources;

import java.util.ArrayList;

public class Note extends AppCompatActivity implements View.OnClickListener {
    private EditText note_name, note_main_text;
    private DrawView draw_view;
    private RelativeLayout main_rl;
    private ImageButton pin_btn;
    private ArrayList<ImageButton> panel_buttons;
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
        panel_buttons = new ArrayList<ImageButton>();

        active_panel_item_bg = getDrawable(R.drawable.active_panel_item_btn);
        inactive_panel_item_bg = getDrawable(R.drawable.icons_bg);
        main_rl = findViewById(R.id.main_place_rl);
        draw_view = new DrawView(this);
        pin_btn = findViewById(R.id.pin_btn);

        main_rl.addView(draw_view);
        note_name = findViewById(R.id.note_name);
        note_main_text = findViewById(R.id.note_main_text);

        panel_buttons.add((ImageButton) findViewById(R.id.write_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.draw_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.eraser_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.text_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.draw_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.bg_change_btn));

        for (ImageButton btn : panel_buttons) {
            btn.setOnClickListener(this);
        }

        panel_buttons.get(0).setBackground(active_panel_item_bg);
        TempResources.setCurrentActivePanelBtn(panel_buttons.get(0));
    }

    @Override
    public void onClick(View view) {
        // код обработки кликов
        if (view == TempResources.getCurrentActivePanelBtn()) {
            // Если эта кнопка уже активна, нет смысла что-то менять
            // Log.d(PublicResourсes.DEBUG_LOG_TAG, "It's already the current button.");
            return;
        }
        switch (view.getId()) {
            case R.id.write_btn:
                // будем отслеживать изменения в тексте
                swapActivePanelItem(view, false);
                note_main_text.setEnabled(true);

                break;
            case R.id.draw_btn:
                // будем отслеживать изменения на холсте
                swapActivePanelItem(view, true);
                note_main_text.setEnabled(false);

                break;
            default:

        }
    }

    private void swapActivePanelItem(View view, boolean draw_status) {
        try {
            TempResources.getCurrentActivePanelBtn().setBackground(inactive_panel_item_bg);
            view.setBackground(active_panel_item_bg);
            TempResources.setCurrentActivePanelBtn((ImageButton) view);
            PublicResourсes.setDrawStatus(draw_status);
        } catch (Exception e) {
            Log.d(PublicResourсes.DEBUG_LOG_TAG, e.getMessage());
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
        boolean is_empty = (note_name.getText().toString().length()
                + note_main_text.getText().toString().length()) == 0;
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
            TempResources.setTempDrawable(pin_btn.getDrawable());
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
        return text;
    }

}