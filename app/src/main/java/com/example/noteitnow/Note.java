package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        pin_btn = findViewById(R.id.pin_btn);

        panel_buttons.add((ImageButton) findViewById(R.id.text_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.draw_color_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.bg_change_btn));
        panel_buttons.add((ImageButton) findViewById(R.id.add_drawing_btn));


        for (ImageButton btn : panel_buttons) {
            btn.setOnClickListener(this);
        }

        note_name = findViewById(R.id.note_name);
        note_main_text = findViewById(R.id.note_main_text);
    }

    @Override
    public void onClick(View view) {
        // код обработки кликов
        switch (view.getId()) {
            case R.id.add_drawing_btn:
                Intent intent = new Intent(this, CanvasActivity.class);
                startActivityForResult(intent, PublicResourсes.REQUIRED_REQUEST_ANSWERS_NUMBER);
                break;
            default:

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // добавить изображение
        }
    }
}