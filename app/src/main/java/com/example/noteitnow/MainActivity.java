package com.example.noteitnow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.noteitnow.notehold.NoteActionsListener;
import com.example.noteitnow.notehold.NotesListAdapter;
import com.example.noteitnow.notes_entities.DrawingStructure;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LayoutInflater inflater;
    private ArrayList<NoteStructure> notes;
    private RecyclerView note_place_rv;
    private NotesListAdapter notes_adapter;
    private NoteActionsListener note_cl;
    private File[] note_files;
    private boolean was_loaded = false;
    private CardView selected_card;
    // notes popup listener
    PopupMenu.OnMenuItemClickListener popup_cl;
    // поиск
    private EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // получение директорий с заметками и рисунками
        PublicResources.NOTES_DIR = new File(getFilesDir(), PublicResources.FILES_DIRECTORY[1]);
        PublicResources.NOTES_DIR.mkdir();
        PublicResources.IMAGES_DIR = new File(getFilesDir(), PublicResources.FILES_DIRECTORY[0]);
        PublicResources.IMAGES_DIR.mkdir();

        initOnCreate();

        // получаем файлы
        if (!was_loaded) {
            Thread take_notes_files = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadFiles();
                }
            });
            take_notes_files.start();
            was_loaded = true;
        }
    }

    // загрузка файлов
    private void loadFiles() {
        try {
            note_files = PublicResources.NOTES_DIR.listFiles();
            if (note_files != null) {
                Log.d(PublicResources.DEBUG_LOG_TAG, String.valueOf(note_files.length));
                for (int i = 0; i < note_files.length; ++i) {
                    String file_path = note_files[i].getAbsolutePath();
                    Log.d(PublicResources.DEBUG_LOG_TAG, file_path);
                    notes.add(PublicResources.parseGsonFromFile(file_path));
                }
                notes_adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
    }

    /**********************************************************************************
     * начальная инициализвация */
    private void initOnCreate() {
        // получаю метрики устройства
        DisplayMetrics dm = getResources().getDisplayMetrics();
        PublicResources.device_width = dm.widthPixels;
        PublicResources.device_height = dm.heightPixels;
        PublicResources.DP = dm.density;

        // значение имени непустой заметки по умолчанию
        PublicResources.DEFAULT_NOTE_NAME = getResources()
                .getString(R.string.default_not_empty_note_name);

        // инициализация
        inflater = getLayoutInflater();
        FloatingActionButton add_note_btn = findViewById(R.id.add_new_note_btn);

        add_note_btn.setOnClickListener(this);

        popup_cl = getNotePopupListener();

        // получение записей
        notes = new ArrayList<NoteStructure>();
        note_cl = new NoteActionsListener() {
            @Override
            public void onClick(View view) {
                editNoteWithGetResult(view);
            }

            @Override
            public void onLongClick(View view, CardView card) {
                selected_card = card;
                showPopupForNote(view);
            }
        };

        // получение поиска
        search_bar = findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
//                Log.d(PublicResources.DEBUG_LOG_TAG, s.toString());
                addSearchFilter(s.toString());
            }
        });

        // обновление RecycleView
        note_place_rv = findViewById(R.id.note_place_rv);
        updateRecycleView();
    }

    /**********************************************************************************
     * отправка заметки на редактирование */
    public void editNoteWithGetResult(View view) {
        try {
            String file_name = view.getTag().toString();
            NoteStructure exist_note = null;
            for (int i = 0; i < notes.size(); ++i) {
                if (notes.get(i).getFileName().equals(file_name)) {
                    Log.d(PublicResources.DEBUG_LOG_TAG, "not null");
                    exist_note = notes.get(i);
                }
            }
            if (exist_note != null) {
                Intent intent = new Intent(MainActivity.this, Note.class);
                intent.putExtra(PublicResources.EXTRA_NOTE, exist_note);
                intent.setAction(PublicResources.ACTION_EDIT_EXIST_NOTE);
                startActivityForResult(intent, PublicResources.REQUEST_NOTE_EDIT);
            }
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
    }

    // попап меню
    private void showPopupForNote(View view) {
        PopupMenu popup_for_note = new PopupMenu(this, view);
        popup_for_note.setOnMenuItemClickListener(popup_cl);
        popup_for_note.inflate(R.menu.popup_for_note);
        popup_for_note.show();
    }

    private PopupMenu.OnMenuItemClickListener getNotePopupListener() {
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.edit_note_btn) {
//                    Toast.makeText(MainActivity.this,
//                            "Редактировать: " + selected_card.getTag().toString(),
//                            Toast.LENGTH_SHORT).show();
                    editNoteWithGetResult((View) selected_card);
                    return true;
                }
                if (menuItem.getItemId() == R.id.clip_note_btn) {
                    Toast.makeText(MainActivity.this,
                            "Закрепить/Открепить: " + selected_card.getTag().toString(),
                            Toast.LENGTH_SHORT).show();
                    // закрепить или открепить,
                    // но пока без обработки самого закрепления или открепления
                    String file_name = selected_card.getTag().toString();
                    for (int i = 0; i < notes.size(); ++i) {
                        if (notes.get(i).getFileName().equals(file_name)) {
                            boolean new_pin_state = !notes.get(i).getPinned();
                            notes.get(i).setIsPinned(new_pin_state);
                            break;
                        }
                    }
                    return true;
                }
                if (menuItem.getItemId() == R.id.delete_note_btn) {
                    String file_name = selected_card.getTag().toString();
                    for (int i = 0; i < notes.size(); ++i) {
                        if (notes.get(i).getFileName().equals(file_name)) {
                            ArrayList<DrawingStructure> will_be_deleted_drawings = notes
                                    .get(i).getDrawings();
                            for (DrawingStructure temp_drawing : will_be_deleted_drawings) {
                                // удалить картинки
                                File file_png = PublicResources
                                        .createFile(temp_drawing.getDrawingFileName(), Doings.PNG);
                                PublicResources.deleteFile(file_png);
                            }
                            // удалить файл заметки
                            File file_gson = PublicResources.createFile(file_name, Doings.GSON);
                            PublicResources.deleteFile(file_gson);
                            notes.remove(i);
                            // обновить
                            addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
                            break;
                        }
                    }
//                    Toast.makeText(MainActivity.this,
//                            "Удалить: " + selected_card.getTag().toString(),
//                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        };
    }

    private void updateRecycleView() {
        note_place_rv.setHasFixedSize(true);
        note_place_rv.setLayoutManager(
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notes_adapter = new NotesListAdapter(MainActivity.this, notes, note_cl);
        note_place_rv.setAdapter(notes_adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_note_btn:
                Intent create_note_intent = new Intent(MainActivity.this, Note.class);
                create_note_intent.setAction(PublicResources.ACTION_CREATE_NEW_NOTE);
                 startActivityForResult(create_note_intent,
                         PublicResources.REQUEST_NOTE_EMPTY);
                break;
        }
    }

    /**********************************************************************************
     * фильтр по поиску */
    protected void addSearchFilter(String search_text) {
        ArrayList<NoteStructure> filtered_list = new ArrayList<NoteStructure>();
        for (NoteStructure current_note : notes) {
            if (current_note.getName().toLowerCase().contains(search_text.toLowerCase())
            || current_note.getText().toLowerCase().contains(search_text.toLowerCase())) {
                filtered_list.add(current_note);
            }
        }
        notes_adapter.addFilteredNotes(filtered_list);
    }

    /**********************************************************************************
     * обработка результата */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        Log.d(PublicResources.DEBUG_LOG_TAG,
//                "result: " + String.valueOf(resultCode) + "; requestCode: " + requestCode);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case PublicResources.REQUEST_NOTE_EMPTY:
                    boolean is_empty = intent
                            .getBooleanExtra(PublicResources.EXTRA_NOTE_IS_EMPTY,
                                    PublicResources.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
                    if (!is_empty) {
                        NoteStructure new_note = (NoteStructure) intent
                                .getSerializableExtra(PublicResources.EXTRA_NOTE);
//                        createNotePresentation(new_note.getName(),
//                                getDrawable(new_note.getPin()));
                        notes.add(new_note);
                        addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
                        // notes_adapter.notifyDataSetChanged();
                    }
                    break;
                case PublicResources.REQUEST_NOTE_EDIT:
                    try {
                        NoteStructure exist_note = (NoteStructure) intent
                                .getSerializableExtra(PublicResources.EXTRA_NOTE);
                        for (int i = 0; i < notes.size(); ++i) {
                            if (notes.get(i).getFileName().equals(exist_note.getFileName())) {
                                notes.set(i, exist_note);
//                                notes_adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
                    } catch (Exception e) {
                        Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
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
//        LinearLayout note = PublicResources
//                .getNewNote(inflater, note_name, drawable);
//        ll.addView(note);
    }


}