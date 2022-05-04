package com.example.noteitnow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.noteitnow.statics_entity.PublicResourсes;
import com.example.noteitnow.statics_entity.TempResources;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout ll;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOnCreate();
    }

    private void initOnCreate() {
        // получаю метрики устройства
        DisplayMetrics dm = getResources().getDisplayMetrics();
        PublicResourсes.device_width = dm.widthPixels;
        PublicResourсes.device_height = dm.heightPixels;
        PublicResourсes.DP = dm.density;

        // значение имени непустой заметки по умолчанию
        PublicResourсes.DEFAULT_NOTE_NAME = getResources()
                .getString(R.string.default_not_empty_note_name);

        // инициализация
        inflater = getLayoutInflater();
        FloatingActionButton add_note_btn = findViewById(R.id.add_new_note_btn);
        ll = findViewById(R.id.notes_place_ll);

        add_note_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_note_btn:
                Intent create_note_intent = new Intent(MainActivity.this, Note.class);
                 startActivityForResult(create_note_intent,
                         PublicResourсes.REQUEST_NOTE_EMPTY);
//                startActivity(create_note_intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        Log.d(PublicResourses.DEBUG_LOG_TAG, String.valueOf(resultCode));
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case PublicResourсes.REQUEST_NOTE_EMPTY:
                    boolean is_empty = intent
                            .getBooleanExtra(PublicResourсes.EXTRA_NOTE_IS_EMPTY,
                                    PublicResourсes.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
//            Log.d(PublicResourses.DEBUG_LOG_TAG,
//                    "EXTRA MESSAGE: " + String.valueOf(is_empty));
                    if (!is_empty) {
                        createNotePresentation(intent.getStringExtra(PublicResourсes.EXTRA_NOTE_NAME),
                                TempResources.getTempPinIcon());
                    }
                    break;
                default:
                    // nothing
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void createNotePresentation(String note_name, Drawable drawable) {
        // получение элементов
//        LinearLayout ll_container = PublicResourses
//                .getNoteLL(new LinearLayout(this), getDrawable(R.drawable.note_bg));
        LinearLayout note = PublicResourсes
                .getNewNote(inflater, note_name, drawable);
        ll.addView(note);
    }

}