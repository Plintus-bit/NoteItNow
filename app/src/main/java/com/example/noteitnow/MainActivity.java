package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteitnow.notehold.NoteActionsListener;
import com.example.noteitnow.notehold.NotesListAdapter;
import com.example.noteitnow.notes_entities.DrawingStructure;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LayoutInflater inflater;
    private ArrayList<NoteStructure> notes;
    private RecyclerView note_place_rv;
    private NotesListAdapter notes_adapter;
    private NoteActionsListener note_cl;
    private File[] note_files;
    private boolean was_loaded = false;
    private CardView selected_card;
    private TextView all_notes_view;
    // текущий попап
    private PopupMenu current_popup;

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

        PublicResources.res = getResources();
        initOnCreate();

        // получаем файлы
        if (!was_loaded) {
            Thread take_notes_files = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadFiles();
                    addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
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
            setAllNotesNumber(note_files.length);
            if (note_files != null) {
//                Log.d(PublicResources.DEBUG_LOG_TAG, String.valueOf(note_files.length));
                for (int i = 0; i < note_files.length; ++i) {
                    String file_path = note_files[i].getAbsolutePath();
//                    Log.d(PublicResources.DEBUG_LOG_TAG, file_path);
                    notes.add(PublicResources.parseGsonFromFile(file_path));
                }
                sortNotes();
                notes_adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
    }

    /**********************************************************************************
     * фильтрация заметок по дате */
    private ArrayList<NoteStructure> getDateFilteredNotes(ArrayList<NoteStructure> notes_list) {
        for (int i = 0; i < notes_list.size() - 1; ++i) {
            for (int j = i + 1; j < notes_list.size(); ++j) {
                int key_index = getDifferentKeysIndex(
                        notes_list.get(i).getDate(), notes_list.get(j).getDate());
                if (notes_list.get(i).getDate().get(key_index)
                        < notes_list.get(j).getDate().get(key_index)) {
                    NoteStructure temp_value = notes_list.get(i);
                    notes_list.set(i, notes_list.get(j));
                    notes_list.set(j, temp_value);
                }
            }
        }
        return notes_list;
    }

    private int getDifferentKeysIndex(ArrayList<Integer> value_1, ArrayList<Integer> value_2) {
        int key_index = 0;
        for (int i = 0; i < value_1.size(); ++i) {
            if (!(Objects.equals(value_1.get(i), value_2.get(i)))) {
                return i;
            }
        }
        return key_index;
    }

    /**********************************************************************************
     * фильтрация заметок закреплённых и нет */
    private void sortNotes() {
        ArrayList<NoteStructure> temp_pinned_notes = new ArrayList<NoteStructure>();
        ArrayList<NoteStructure> temp_not_pinned_notes = new ArrayList<NoteStructure>();
        for (int i = 0; i < notes.size(); ++i) {
            if (notes.get(i).getPinned()) {
                temp_pinned_notes.add(notes.get(i));
            }
            else {
                temp_not_pinned_notes.add(notes.get(i));
            }
        }
        notes.clear();
        notes.addAll(getDateFilteredNotes(temp_pinned_notes));
        notes.addAll(getDateFilteredNotes(temp_not_pinned_notes));
    }

    /**********************************************************************************
     * получение настроек */
    private void collectPreferences() {
        boolean is_darcula_active = PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false);
        if (is_darcula_active) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            search_bar.setBackground(getDrawable(R.drawable.search_bar_dark));
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            search_bar.setBackground(getDrawable(R.drawable.search_bar));
        }
    }

    @Override
    protected void onResume() {
        collectPreferences();
        super.onResume();
    }

    /**********************************************************************************
     * начальная инициализация */
    private void initOnCreate() {
        // получаю настройки
        PublicResources.preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
                if (current_popup != null) {
                    current_popup.dismiss();
                }
                editNoteWithGetResult(view);
            }

            @Override
            public void onLongClick(View view, CardView card) {
                if (current_popup != null) {
                    current_popup.dismiss();
                }
                selected_card = card;
                current_popup = showPopupForNote(view);
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

        all_notes_view = findViewById(R.id.all_notes_view);
    }

    /**********************************************************************************
     * установка числа заметок */
    private void setAllNotesNumber(int all_notes_number) {
        all_notes_view.setText(getResources().getString(R.string.all_notes)
                + String.valueOf(all_notes_number));
    }

    /**********************************************************************************
     * меню */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_btn:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                // nothing
        }
        return super.onOptionsItemSelected(item);
    }

    /**********************************************************************************
     * отправка заметки на редактирование */
    public void editNoteWithGetResult(View view) {
        try {
            String file_name = view.getTag().toString();
            NoteStructure exist_note = null;
            for (int i = 0; i < notes.size(); ++i) {
                if (notes.get(i).getFileName().equals(file_name)) {
//                    Log.d(PublicResources.DEBUG_LOG_TAG, "not null");
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
    private PopupMenu showPopupForNote(View view) {
        PopupMenu popup_for_note = new PopupMenu(this, view);
        popup_for_note.setOnMenuItemClickListener(popup_cl);
        popup_for_note.inflate(R.menu.popup_for_note);
        popup_for_note.show();
        return popup_for_note;
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
//                    Toast.makeText(MainActivity.this,
//                            "Закрепить/Открепить: " + selected_card.getTag().toString(),
//                            Toast.LENGTH_SHORT).show();
                    // закрепить или открепить
                    String file_name = selected_card.getTag().toString();
                    for (int i = 0; i < notes.size(); ++i) {
                        if (notes.get(i).getFileName().equals(file_name)) {
                            boolean new_pin_state = !notes.get(i).getPinned();
                            notes.get(i).setIsPinned(new_pin_state);
                            setNewPinState(notes.get(i));
                            sortNotes();
                            addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
                            break;
                        }
                    }
                    return true;
                }
                if (menuItem.getItemId() == R.id.delete_note_btn) {
                    String file_name = selected_card.getTag().toString();
                    noteDeletion(file_name);
//                    Toast.makeText(MainActivity.this,
//                            "Удалить: " + selected_card.getTag().toString(),
//                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        };
    }

    protected void noteDeletion(String file_name) {
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
                setAllNotesNumber(notes.size());
                // обновить
                addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
                break;
            }
        }
    }

    protected void updateRecycleView() {
        note_place_rv.setHasFixedSize(true);
        note_place_rv.setLayoutManager(
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notes_adapter = new NotesListAdapter(MainActivity.this, notes, note_cl,
                getResources());
        notes_adapter.setIsDarculaActive(PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false));
        note_place_rv.setAdapter(notes_adapter);
//        note_place_rv.post(new Runnable()
//        {
//            @Override
//            public void run() {
//                notes_adapter.notifyDataSetChanged();
//            }
//        });
    }

    protected void setNewPinState(NoteStructure note_with_new_state) {
        File file = PublicResources
                .createFile(note_with_new_state.getFileName(), Doings.GSON);
        PublicResources.saveOrReplaceGson(file.getAbsolutePath(),
                NoteStructure.getNoteToGsonString(note_with_new_state));
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
            || current_note.getText().toString()
                    .toLowerCase().contains(search_text.toLowerCase())) {
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
            boolean is_empty = intent
                    .getBooleanExtra(PublicResources.EXTRA_NOTE_IS_EMPTY,
                            PublicResources.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
            switch (requestCode) {
                case PublicResources.REQUEST_NOTE_EMPTY:
                    if (!is_empty) {
                        NoteStructure new_note = (NoteStructure) intent
                                .getSerializableExtra(PublicResources.EXTRA_NOTE);
                        notes.add(new_note);
                        setAllNotesNumber(notes.size());
                    }
                    break;
                case PublicResources.REQUEST_NOTE_EDIT:
                    try {
                        if (is_empty) {
                            noteDeletion(intent.getStringExtra(PublicResources.EXTRA_NOTE_TAG));
                        }
                        else {
                            NoteStructure exist_note = (NoteStructure) intent
                                    .getSerializableExtra(PublicResources.EXTRA_NOTE);
                            for (int i = 0; i < notes.size(); ++i) {
                                if (notes.get(i).getFileName().equals(exist_note.getFileName())) {
                                    notes.set(i, exist_note);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
                    }
                    break;
                default:
                    // nothing
            }
            sortNotes();
            addSearchFilter(PublicResources.EXTRA_DEFAULT_STRING_VALUE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (current_popup != null) {
            current_popup.dismiss();
            current_popup = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (current_popup != null) {
            current_popup.dismiss();
            current_popup = null;
        }
    }
}