package com.example.noteitnow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.noteitnow.notes_entities.DrawingStructure;
import com.example.noteitnow.notes_entities.EditIndexes;
import com.example.noteitnow.notes_entities.NoteSavings;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.notes_entities.TextSpans;
import com.example.noteitnow.notes_entities.TextSpansEditor;
import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.example.noteitnow.statics_entity.TempResources;
import com.example.noteitnow.statics_entity.items.ColorItems;
import com.example.noteitnow.statics_entity.items.DataItems;
import com.google.android.material.shape.MarkerEdgeTreatment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note extends AppCompatActivity implements View.OnClickListener {
    private EditText note_name, note_main_text;
    private ImageButton pin_btn;

    // статичные массивы
    public int[] pin_ids;
    public int[] colors;
    ArrayList<ArrayList<Drawable>> bg_patterns_data;

    ColorItems color_items;
    DataItems data_items;

    private boolean is_creating_complete = false;

    Toast current_toast = null;

    // текущий цвет bg
    private int bg_color;

    // массив рисунков
    ArrayList<DrawingStructure> drawings;

    TextSpansEditor spans_editor;

    // текущее действие над заметкой, от которого зависит её выгрузка
    private String current_action;
    private String current_file_name;

    private Resources res;

    // попап
    private PopupMenu.OnMenuItemClickListener popup_cl;

    // inflater
    LayoutInflater inflater;

    // цвета
    private int current_text_color;
    private int current_text_bg_color;
    private int default_text_color;
    private int current_pattern_id;

    // вложенный класс для хранения элементов
    private class ExistView {
        private ImageButton item;
        public boolean is_active;
        ExistView(ImageButton item) {
            this.item = item;
            is_active = false;
        }
        ExistView(ImageButton item, boolean is_active) {
            this.item = item;
            this.is_active = is_active;
        }
        public void setActive(boolean is_active) {
            this.is_active = is_active;
        }
        private boolean getActive() {
            return is_active;
        }
        private ImageButton getItem() {
            return item;
        }
    }

    // активные элементы
    private ArrayList<ExistView> panel_views;
    private ImageView waiting_for_result;

    // HorizontalScrollView для загрузки элементов
    private HorizontalScrollView items_hsv,
            main_tools_panel_hsv;
    private ScrollView pins_sv;
    private LinearLayout pins_ll,
            main_ll,
            this_note_place_ll;

    // активные и неактивные элементы
    private Drawable active_panel_item_bg;
    private Drawable inactive_panel_item_bg;

    // текущее выделение
    private int start_selection;
    private int end_selection;

    // текущие изменённые индексы
    private int start_index, end_index, current_length;

    // закреплена ли заметка
    private boolean is_pinned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initOnCreate();

        getIntentFromMain();

        setThemePropertiesToNote();

        if (note_main_text.length() > 0) {
            setCursorOnPosition(note_main_text.length() - 1);
        }
    }

    /**********************************************************************************
     * получение намерения из MainActivity */
    private void getIntentFromMain() {
        Intent intent = getIntent();
        current_action = intent.getAction();
//        Log.d(PublicResources.DEBUG_LOG_TAG, current_action);

        if (current_action.equals(PublicResources.ACTION_EDIT_EXIST_NOTE)) {
            try {
                NoteStructure exist_note = (NoteStructure) intent
                        .getSerializableExtra(PublicResources.EXTRA_NOTE);
                note_name.setText(exist_note.getName());
                note_main_text.setText(exist_note.getText());
                for (int i = 0; i < pin_ids.length; ++i) {
                    if (pin_ids[i] == exist_note.getPin()) {
                        pin_btn.setTag(i);
                        break;
                    }
                }
                if (exist_note.getBgPattern() != PublicResources.CANCEL_ID) {
                    this_note_place_ll
                            .setBackground(bg_patterns_data.get(0).get(exist_note.getBgPattern()));
                }
                current_pattern_id = exist_note.getBgPattern();
                is_pinned = exist_note.getPinned();
                pin_btn.setImageDrawable(res.getDrawable(exist_note.getPin(), null));
                bg_color = exist_note.getBg();
                main_ll.setBackgroundColor(bg_color);
                current_file_name = exist_note.getFileName();
                drawings = exist_note.getDrawings();
                spans_editor = new TextSpansEditor(exist_note.getSpans());
                ArrayList<TextSpans> temp_spans = spans_editor.getTextSpans();
//                spans = exist_note.getSpans();
                if (temp_spans != null) {
                    for (TextSpans current_span : temp_spans) {
                        setSpansOnText(current_span);
                    }
                } else {
                    spans_editor.setTextSpans(new ArrayList<TextSpans>());
                }
                temp_spans = null;
                if (drawings != null) {
                    for (int i = 0; i < drawings.size(); ++i) {
                        File file = PublicResources.createFile(drawings.get(i)
                                .getDrawingFileName(), Doings.PNG);
                        Bitmap new_drawing = PublicResources
                                .parseBitmapFromDrawingFile(file.getAbsolutePath());
                        TempResources.getTempDrawingsArray().add(new_drawing);
                        TempResources.getTempBGsForDrawings().add(drawings.get(i).getDrawingBg());
                        addImageDrawingToNote(R.layout.drawing_layout,
                                drawings.get(i).getDrawingFileName(), new_drawing,
                                drawings.get(i).getDrawingBg());
                    }
                }
//                Log.d(PublicResources.DEBUG_LOG_TAG, ">>> Processed!");
            } catch (Exception e) {
                Log.d(PublicResources.DEBUG_LOG_TAG, "error (1): " + e.getMessage());
            }
        }

    }

    /**********************************************************************************
     * настройки вида при разных темах */
    private void setThemePropertiesToNote() {
        boolean is_darcula_active = PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false);
        if (is_darcula_active) {
            if (bg_color == PublicResources.DEFAULT_BG_COLOR_VALUE) {
                main_ll.setBackgroundColor(res.getColor(R.color.black, null));
            }
            note_name.setTextColor(res.getColor(R.color.mono_light_grey, null));
            note_main_text.setTextColor(res.getColor(R.color.mono_light_grey, null));
            pin_btn.setColorFilter(res.getColor(R.color.mono_medium_grey, null));

            // панели
            items_hsv.setBackgroundColor(res.getColor(R.color.blue_middle_grey, null));
            main_tools_panel_hsv
                    .setBackgroundColor(res.getColor(R.color.mono_middle_grey, null));

            pins_ll.setBackgroundColor(res.getColor(R.color.mono_middle_grey, null));
            int temp_pins_color = res.getColor(R.color.black, null);
            for (int i = 0; i < pins_ll.getChildCount(); ++i) {
                setColorFiltersOnPins((LinearLayout) pins_ll.getChildAt(i), temp_pins_color);
            }
        }
        else {
            if (bg_color == PublicResources.DEFAULT_BG_COLOR_VALUE) {
                main_ll.setBackgroundColor(res.getColor(R.color.white, null));
            }
        }
    }

    private void setColorFiltersOnPins(LinearLayout parent_ll, int color_for_pins) {
        for (int i = 0; i < parent_ll.getChildCount(); ++i) {
            ((ImageView) parent_ll.getChildAt(i)).setColorFilter(color_for_pins);
        }
    }

    /**********************************************************************************
     * начальная инициализация */
    private void initOnCreate() {
        // получение ресурсов
        res = getResources();

        color_items = new ColorItems(res);
        data_items = new DataItems(res);

        // сбор ресурсов
        pin_ids = getPinIds();
        colors = fillColors();
        bg_patterns_data = data_items.getBgPatterns();
        current_pattern_id = PublicResources.CANCEL_ID;

        // получение цветов
        default_text_color = res.getColor(R.color.black, null);
        current_text_bg_color = res.getColor(R.color.transparent_, null);
        current_text_color = default_text_color;
        bg_color = PublicResources.DEFAULT_BG_COLOR_VALUE;

        // инициализация inflater
        inflater = getLayoutInflater();

        // инициализация панелек
        items_hsv = findViewById(R.id.items_hsv);
        main_tools_panel_hsv = findViewById(R.id.main_tools_panel_hsv);
        pins_sv = findViewById(R.id.pins_sv);
        pins_ll = findViewById(R.id.pins_ll);
        main_ll = findViewById(R.id.main_ll);
         this_note_place_ll = findViewById(R.id.this_note_place_ll);
        panel_views = new ArrayList<ExistView>();
        active_panel_item_bg = getDrawable(R.drawable.active_panel_item_btn);
        inactive_panel_item_bg = getDrawable(R.drawable.icons_bg);
        pin_btn = findViewById(R.id.pin_btn);

        // настройки Layout'ов
        main_ll.setBackgroundColor(bg_color);

        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_color_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.bg_change_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.add_drawing_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.marker_list_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.text_background_color_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.bold_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.italic_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.normal_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.add_image_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.bg_pattern_change_btn)));
        panel_views.add(new ExistView((ImageButton) findViewById(R.id.clear_styles_btn)));

        // рисунков нет
        drawings = new ArrayList<DrawingStructure>();
        // спанов нет
        spans_editor = new TextSpansEditor(new ArrayList<TextSpans>());

        for (ExistView btn : panel_views) {
            btn.getItem().setOnClickListener(this);
        }
        pin_btn.setOnClickListener(this);
        pin_btn.setTag(28);

        note_name = findViewById(R.id.note_name);
        note_main_text = findViewById(R.id.note_main_text);
        View.OnClickListener note_text_cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pins_sv.getVisibility() == View.VISIBLE) {
                    pins_sv.setVisibility(View.GONE);
                }
            }
        };
        note_name.setOnClickListener(note_text_cl);
        note_main_text.setOnClickListener(note_text_cl);

        TempResources.setTempPinIcon(pin_btn.getDrawable());

        popup_cl = getPopupMenuItemCl();

        // по умолчанию не закреплена
        is_pinned = false;

        setListenerOnNoteMainText();

        current_file_name = PublicResources.EXTRA_DEFAULT_STRING_VALUE;
    }

    private void setListenerOnNoteMainText() {
        note_main_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence char_seq, int i0, int i1, int i2) {
                start_index = note_main_text.getSelectionStart();
                current_length = note_main_text.length();
            }

            @Override
            public void onTextChanged(CharSequence char_seq, int i0, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(note_main_text.length() == current_length)) {
                    end_index = note_main_text.getSelectionEnd();
                    if (end_index < start_index) {
                        spans_editor.addEditIndex(end_index, start_index, Doings.DELETION);
                    }
                    else {
                        spans_editor.addEditIndex(start_index, end_index, Doings.INSERTION);
                    }
                }
            }
        });
    }

    /**********************************************************************************
     * обработка меню */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_sharing_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                forActivityResult();
                return true;
            case R.id.share_btn:
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                saveTempImageFiles();
                while (!is_creating_complete) {
                    // do nothing
                }
                ArrayList<Uri> files_data = getImagesUri();
                is_creating_complete = false;
                // настройки намерения
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_TITLE, note_name.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, note_name.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT, note_main_text.getText().toString());
                if (files_data != null) {
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files_data);
                }
                List<ResolveInfo> ready_for_get_activities = getPackageManager()
                        .queryIntentActivities(intent, 0);
                if (ready_for_get_activities.size() > 0) {
                    startActivityForResult(intent, PublicResources.REQUEST_SEND_MESSAGE);
                }
                else {
                    if (current_toast != null) {
                        current_toast.cancel();
                    }
                    current_toast = Toast.makeText(this,
                            R.string.activities_not_found,
                            Toast.LENGTH_SHORT);
                    current_toast.show();
                }
                break;
            default:
                // nothing
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveTempImageFiles() {
        Thread saving = new Thread(new Runnable() {
            @Override
            public void run() {
                savingTemporaryImages();
                is_creating_complete = true;
            }
        });
        saving.start();
    }

    protected void savingTemporaryImages() {
        for (int i = 0; i <drawings.size(); ++i) {
            File file = PublicResources
                    .createFile(drawings.get(i).getDrawingFileName(), Doings.TEMP_PNG);
            File source_file = PublicResources
                    .createFile(drawings.get(i).getDrawingFileName(), Doings.PNG);
            Bitmap drawing_image = PublicResources
                    .parseBitmapFromDrawingFile(source_file.getAbsolutePath());
            Bitmap bitmap_bg = Bitmap.createBitmap(drawing_image.getWidth(),
                    drawing_image.getHeight(), drawing_image.getConfig());
            Canvas canvas = new Canvas(bitmap_bg);
            canvas.drawColor(drawings.get(i).getDrawingBg());
            canvas.drawBitmap(drawing_image, 0, 0, null);
            NoteSavings.saveOrReplaceDrawing(file, bitmap_bg);
        }
    }

    // попап меню
    private void showPopupForDrawing(View view) {
        PopupMenu popup_for_drawing = new PopupMenu(this, view);
        popup_for_drawing.setOnMenuItemClickListener(popup_cl);
        popup_for_drawing.inflate(R.menu.popup_menu_for_image_drawing);
        popup_for_drawing.show();
    }

    @Override
    public void onClick(View view) {
        // код обработки кликов
        if (pins_sv.getVisibility() == View.VISIBLE) {
            clearPinsPanel();
            if (view.getId() == R.id.pin_btn) {
                return;
            }
        }
        start_selection = note_main_text.getSelectionStart();
        end_selection = note_main_text.getSelectionEnd();

        TextSpans span = null;
        switch (view.getId()) {
            case R.id.add_drawing_btn:
                Intent intent = getDrawingIntent();
                intent.setAction(PublicResources.ACTION_CREATE_NEW_CANVAS);
                startActivityForResult(intent, PublicResources.REQUEST_NEW_CANVAS);
                break;
            case R.id.pin_btn:
                pins_sv.setVisibility(View.VISIBLE);
                View.OnClickListener cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TempResources.setTempPinIcon(((ImageButton) view).getDrawable());
                        pin_btn.setTag(view.getTag());
                        pin_btn.setImageDrawable(TempResources.getTempPinIcon());
                        clearPinsPanel();
                    }
                };
                for (int line_number = 0; line_number < pins_ll.getChildCount(); ++line_number) {
                    LinearLayout line = (LinearLayout) pins_ll.getChildAt(line_number);
                    for (int pin_item_number = 0; pin_item_number < line.getChildCount();
                         ++pin_item_number) {
                        line.getChildAt(pin_item_number).setTag(line_number * 7 + pin_item_number);
                        line.getChildAt(pin_item_number).setOnClickListener(cl);
                    }
                }
                break;
            case R.id.text_color_btn:
                setColorChooserForPanelElement(panel_views.get(0), Doings.TEXT);
                break;
            case R.id.bg_change_btn:
                setColorChooserForPanelElement(panel_views.get(1), Doings.BACKGROUND);
                break;
            case R.id.text_background_color_btn:
                setColorChooserForPanelElement(panel_views.get(4), Doings.TEXT_BG);
                break;
            case R.id.bold_btn:
                span = new TextSpans(
                        note_main_text.getSelectionStart(), note_main_text.getSelectionEnd(),
                        Doings.BOLD,
                        Typeface.BOLD, Spannable.SPAN_USER);
                setSpansOnText(span);
                setNewSpannedText(span);
                break;
            case R.id.italic_btn:
                span = new TextSpans(
                        note_main_text.getSelectionStart(), note_main_text.getSelectionEnd(),
                        Doings.ITALIC,
                        Typeface.ITALIC, Spannable.SPAN_USER);
                setSpansOnText(span);
                setNewSpannedText(span);
                break;
            case R.id.normal_btn:
                span = new TextSpans(
                        note_main_text.getSelectionStart(), note_main_text.getSelectionEnd(),
                        Doings.NORMAL,
                        Typeface.NORMAL, Spannable.SPAN_USER);
                setNewSpannedText(span);
                break;
            case R.id.add_image_btn:
                showNotSupportedMessage();
//                verifyStoragePermissions(Note.this);
//                if (!PublicResources.has_external_permission) {
//                    return;
//                }
//                Intent pick_image_intent = new Intent(Intent.ACTION_PICK);
//                pick_image_intent.setType("image/*");
//                startActivityForResult(pick_image_intent, PublicResources.REQUEST_PICK_IMAGE);
                break;
            case R.id.bg_pattern_change_btn:
                setItemsChooserForPanelElement(panel_views.get(9).getItem());
                break;
            case R.id.clear_styles_btn:
                span = new TextSpans(
                        note_main_text.getSelectionStart(), note_main_text.getSelectionEnd(),
                        Doings.CLEAR,
                        PublicResources.CANCEL_ID, Spannable.SPAN_USER);
                setNewSpannedText(span);
                break;
            case R.id.marker_list_btn:
                showNotSupportedMessage();
                break;
            default:
                // nothing
        }
    }

    protected void showNotSupportedMessage() {
        if (current_toast != null) {
            current_toast.cancel();
        }
        current_toast = Toast.makeText(Note.this,
                "Функция находится в разработке :)",
                Toast.LENGTH_SHORT);
        current_toast.show();
    }

    /**********************************************************************************
     * установка курсора в нужную позицию */
    public void setCursorOnPosition(int position) {
        note_main_text.setSelection(position);
    }

    /**********************************************************************************
     * Spannable */
    public void setSpansOnText(TextSpans span) {
        SpannableStringBuilder span_text = new SpannableStringBuilder(
                note_main_text.getText());
        TextSpansEditor.getTextWithNewSpan(span_text, span);
        note_main_text.setText(span_text);
        setCursorOnPosition(span.getEnd());
    }

    /**********************************************************************************
     * выборка цвета */
    private void setColorChooserForPanelElement(ExistView existView, Doings color_of_what) {
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
        }
        items_hsv.setVisibility(View.VISIBLE);
        LinearLayout ll = PublicResources
                .getLLPanelWithColors(inflater, R.layout.popup_menu_layout, colors);
        items_hsv.addView(ll);
        View.OnClickListener color_cl = getColorCL(existView, color_of_what);
        for (int i = 0; i < ll.getChildCount(); ++i) {
            (ll.getChildAt(i)).setOnClickListener(color_cl);
        }
        if (color_of_what == Doings.BACKGROUND) {
            PublicResources.createCancelItem(
                    inflater, R.layout.popup_menu_default_item_layout,
                    ll, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setDefaultBgColorForNote();
                            clearPanelItems();
                        }
                    });
        }
    }

    private void setItemsChooserForPanelElement(ImageButton panel_element) {
        if (items_hsv.getVisibility() == View.VISIBLE) {
            clearPanelItems();
        }
        items_hsv.setVisibility(View.VISIBLE);
        LinearLayout ll = PublicResources
                .getLLPanelWithItems(inflater, R.layout.popup_menu_layout,
                        new int[] {0, 1, 2, 3}, bg_patterns_data.get(1));
        items_hsv.addView(ll);
        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() != PublicResources.CANCEL_ID) {
                    this_note_place_ll.setBackground(bg_patterns_data.get(0).get(view.getId()));
                }
                else {
                    this_note_place_ll
                            .setBackground(res.getDrawable(R.drawable.icons_bg, null));
                }
                current_pattern_id = view.getId();
                clearPanelItems();
            }
        };
        for (int i = 0; i < ll.getChildCount(); ++i) {
            (ll.getChildAt(i)).setOnClickListener(cl);
        }
        PublicResources.createCancelItem(
                inflater, R.layout.popup_menu_default_item_layout, ll, cl);
    }

    private void setDefaultBgColorForNote() {
        bg_color = PublicResources.DEFAULT_BG_COLOR_VALUE;
        boolean is_darcula_active = PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false);
        if (is_darcula_active) {
            main_ll.setBackgroundColor(res.getColor(R.color.black, null));
            panel_views.get(1).getItem().setColorFilter(res.getColor(R.color.black, null));
        }
        else {
            main_ll.setBackgroundColor(res.getColor(R.color.white, null));
            panel_views.get(1).getItem().setColorFilter(res.getColor(R.color.white, null));
        }
    }

    /**********************************************************************************
     * получение специального ClickListener'а */
    private View.OnClickListener getColorCL(ExistView existView, Doings color_of_what) {
        View.OnClickListener cl = null;
        switch (color_of_what) {
            case BACKGROUND:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        bg_color = view.getId();
                        main_ll.setBackgroundColor(bg_color);
                        clearPanelItems();
                    }
                };
                break;
            case TEXT_BG:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        current_text_bg_color = view.getId();
                        clearPanelItems();
                        TextSpans span = new TextSpans(start_selection, end_selection,
                                Doings.TEXT_BG,
                                current_text_bg_color, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        setSpansOnText(span);
                        // добавление и очистка
//                        spans_editor.makeCleaning();
                        setNewSpannedText(span);
                    }
                };
                break;
            case TEXT:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        current_text_color = view.getId();
                        clearPanelItems();
                        TextSpans span = new TextSpans(start_selection, end_selection,
                                Doings.COLOR,
                                current_text_color, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        setSpansOnText(span);
                        // добавление и очистка
//                        spans_editor.makeCleaning();
                        setNewSpannedText(span);
                    }
                };
                break;
            default:
                cl = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        existView.getItem().setColorFilter(view.getId());
                        clearPanelItems();
                    }
                };
        }
        return cl;
    }

    private void setNewSpannedText(TextSpans span) {
        SpannableStringBuilder new_text_data = spans_editor
                .removeUselessTextSpans(span,
                        note_main_text.getText().toString());
        if (new_text_data != null) {
            note_main_text.setText(new_text_data);
        }
    }

    @Override
    public void onBackPressed() {
        forActivityResult();
    }

    /**********************************************************************************
     * возврат результата */
    private void forActivityResult() {
        Intent intent = new Intent();
        boolean is_empty = getEmpty();
        // код обработки закрытия
        // Пустая заметка или нет
        intent.putExtra(PublicResources.EXTRA_NOTE_IS_EMPTY, is_empty);
        // Если не пустая, сообщить её данные
        NoteStructure created_note = getNewNote();
        if (!is_empty) {
            intent.putExtra(PublicResources.EXTRA_NOTE, created_note);
            // сохранение файла
            File file = PublicResources.createFile(created_note.getFileName(), Doings.GSON);
            PublicResources.saveOrReplaceGson(file.getAbsolutePath(),
                    NoteStructure.getNoteToGsonString(created_note));
        }
        else if (current_action.equals(PublicResources.ACTION_EDIT_EXIST_NOTE)) {
            intent.putExtra(PublicResources.EXTRA_NOTE_TAG, created_note.getFileName());
        }
        clearTempResources();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PublicResources.REQUEST_SEND_MESSAGE) {
            startDeletionTempFiles();
        }
        if (resultCode == RESULT_OK) {
            String file_name = "";
            NoteStructure editted_note = (NoteStructure) intent
                    .getSerializableExtra(PublicResources.EXTRA_NOTE);
            current_file_name = editted_note.getFileName();
            drawings = editted_note.getDrawings();
            switch (requestCode) {
                // получение рисунка
                case PublicResources.REQUEST_NEW_CANVAS:
                    boolean is_new_canvas_empty = intent
                            .getBooleanExtra(PublicResources.EXTRA_CANVAS_IS_EMPTY,
                                    PublicResources.EXTRA_NOTE_IS_EMPTY_DEFAULT_VALUE);
                    file_name = intent.getStringExtra(PublicResources.EXTRA_CANVAS_TAG);
                    if (!is_new_canvas_empty) {
                        ArrayList<Bitmap> bitmaps_array = TempResources.getTempDrawingsArray();
//                        file_name = NoteSavings.getNewFileName(Doings.PNG);
                        // добавить изображение в заметку
                        Bitmap new_drawing = bitmaps_array.get(bitmaps_array.size() - 1);
                        int bg_color = TempResources.getTempBGsForDrawings()
                                .get(bitmaps_array.size() - 1);
                        addImageDrawingToNote(R.layout.drawing_layout,
                                file_name,
                                new_drawing, bg_color);
                    }
                    break;
                case PublicResources.REQUEST_EDIT_CANVAS:
                    int temp_index = intent
                            .getIntExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                                    PublicResources.EXTRA_DEFAULT_INT_VALUE);
                    Drawable img_bg = waiting_for_result.getBackground();
                    img_bg.setColorFilter(TempResources.getTempBGsForDrawings().get(temp_index),
                            PorterDuff.Mode.SRC_IN);
                    waiting_for_result.setBackground(img_bg);
                    waiting_for_result.setImageBitmap(TempResources
                            .getTempDrawingsArray().get(temp_index));
                    break;
                case PublicResources.REQUEST_PICK_IMAGE:
                    if (intent != null) {
                        Uri image_uri = intent.getData();
                        InputStream image_is = null;
                        Bitmap image_bmp = null;
                        try {
                            image_is = getContentResolver().openInputStream(image_uri);
                            image_bmp = BitmapFactory.decodeStream(image_is);
                            if (image_bmp != null) {
                                file_name = NoteSavings.getNewFileName(Doings.PNG);
                                // добавить изображение в заметку
                                addImageDrawingToNote(R.layout.drawing_layout,
                                        file_name,
                                        image_bmp, res.getColor(R.color.transparent_));

                                // добавление в массив
                                File file_1 = PublicResources.createFile(file_name, Doings.PNG);
                                DrawingStructure catched_drawing = new DrawingStructure();
                                catched_drawing.setDrawingBg(bg_color);
                                catched_drawing.setDrawingFileName(file_name);
                                drawings.add(catched_drawing);
                                // добавление изображения в файл
                                Bitmap final_image_bmp = image_bmp;
                                Thread save_drawing = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NoteSavings.saveOrReplaceDrawing(file_1, final_image_bmp);
                                        Log.d(PublicResources.DEBUG_LOG_TAG, ">>> Get it!");
                                    }
                                });
                                save_drawing.start();
                            }
                        } catch (Exception e) {
                            Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
                        }
                    }
                    break;
                default:
                    // nothing
            }
        }
    }

    private void startDeletionTempFiles() {
        Thread deletion_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File[] temp_files = PublicResources.TEMP_IMAGES_DIR.listFiles();
                for (File file : temp_files) {
                    PublicResources.deleteFile(file);
                }
//                    Log.d(PublicResources.DEBUG_LOG_TAG,"temp_dir_size >>> " +
//                            String.valueOf(PublicResources.TEMP_IMAGES_DIR.listFiles().length));
            }
        });
        deletion_thread.start();
    }

    private void clearPanelItems() {
        items_hsv.removeAllViews();
        items_hsv.setVisibility(View.GONE);
    }
    private void clearPinsPanel() {
        pins_sv.setVisibility(View.GONE);
    }

    // Заполнение

    private int[] fillColors() {
        return color_items.getColorsWithoutTransparent();
    }

    // обработка добавления изображений
    private ImageView getImageDrawingForNote(int child_layout, Bitmap drawing,
                                             int bg_color, String tag) {
        // создание изображения
        ImageView drawing_img = (ImageView) inflater
                .inflate(child_layout, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (320 * PublicResources.DP));
        lp.bottomMargin = (int) (7 * PublicResources.DP);
        Drawable bg = drawing_img.getBackground();
        bg.setColorFilter(bg_color, PorterDuff.Mode.SRC_IN);
        drawing_img.setTag(tag);
        drawing_img.setBackground(bg);
        drawing_img.setLayoutParams(lp);
        drawing_img.setImageBitmap(drawing);
        // обработка клика по рисунку
        drawing_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView temp = (ImageView) view;
                waiting_for_result = temp;
                TempResources.setTempDrawingFromCanvas(
                        ((BitmapDrawable) temp.getDrawable()).getBitmap());
                showPopupForDrawing(temp);
            }
        });
        return drawing_img;
    }

    // popup слушатель
    private PopupMenu.OnMenuItemClickListener getPopupMenuItemCl() {
        return (new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.edit_draw_btn) {
                    // получаем данные о рисунке и его положении в массиве
                    ArrayList<Bitmap> temp_drawings = TempResources.getTempDrawingsArray();
                    Bitmap draw_bmp = ((BitmapDrawable)
                            (waiting_for_result.getDrawable())).getBitmap();
                    // редактируем
                    Intent intent = getDrawingIntent();
                    for (int index = 0; index < temp_drawings.size(); ++index) {
                        if (draw_bmp.equals(temp_drawings.get(index))) {
                            intent.putExtra(PublicResources.EXTRA_BG_CANVAS_COLOR,
                                    TempResources.getTempBGsForDrawings().get(index));
                            intent.putExtra(PublicResources.EXTRA_DRAWING_TEMP_INDEX,
                                    index);
                            TempResources.setTempDrawingFromCanvas(temp_drawings.get(index));
                            break;
                        }
                    }
                    intent.putExtra(PublicResources.EXTRA_CANVAS_TAG,
                            waiting_for_result.getTag().toString());
                    intent.setAction(PublicResources.ACTION_EDIT_EXIST_CANVAS);
                    startActivityForResult(intent, PublicResources.REQUEST_EDIT_CANVAS);
                    return true;
                }
                else if (menuItem.getItemId() == R.id.remove_draw_btn) {
                    // удаляем
                    String file_name = waiting_for_result.getTag().toString();
                    for (int i = 0; i < drawings.size(); ++i) {
                        if (drawings.get(i).getDrawingFileName().equals(file_name)) {
                            File file = PublicResources.createFile(file_name, Doings.PNG);
                            drawings.remove(i);
                            PublicResources.deleteFile(file);
                            waiting_for_result.setOnClickListener(null);
                            if (i < TempResources.getTempDrawingsArray().size()) {
                                TempResources.getTempDrawingsArray().remove(i);
                                TempResources.getTempBGsForDrawings().remove(i);
                            }
                            this_note_place_ll.removeView(waiting_for_result);
                            break;
                        }
                    }
                    Toast.makeText(Note.this,
                            "Удалили, пиу-пиу!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return true;
            }
        });
    }

    public Intent getDrawingIntent() {
        Intent intent = new Intent(Note.this, CanvasActivity.class);
        NoteStructure note = getNewNote();
        intent.putExtra(PublicResources.EXTRA_NOTE, note);
        return intent;
    }

    public void saveNote() {
        NoteStructure created_note = getNewNote();
        NoteSavings.saveNote(created_note);
    }

    public void addImageDrawingToNote(int image_layout, String tag,
                                      Bitmap drawing, int bg_color) {
        ImageView drawing_img = getImageDrawingForNote(image_layout,
                drawing, bg_color, tag);
        this_note_place_ll.addView(drawing_img);
    }

    // очистка временного хранилища после успешного сохранения данных
    protected void clearTempResources() {
        TempResources.getTempBGsForDrawings().clear();
        TempResources.getTempDrawingsArray().clear();
    }

    /**********************************************************************************
     * сохранение заметки */
    public NoteStructure getNewNote() {
        NoteStructure new_note = null;
        try {
            new_note = new NoteStructure();
            if (current_file_name.equals(PublicResources.EXTRA_DEFAULT_STRING_VALUE)) {
                current_file_name = NoteSavings.getNewFileName(Doings.GSON);
                new_note.setFileName(current_file_name);
            }
            else {
                new_note.setFileName(current_file_name);
            }
            new_note.setName(note_name.getText().toString());
            new_note.setText(note_main_text.getText().toString());
            new_note.setPin(pin_ids[Integer.parseInt(pin_btn.getTag().toString())]);
            new_note.setBg(bg_color);
            new_note.setDrawings(drawings);
            new_note.setIsPinned(is_pinned);
            new_note.setBgPattern(current_pattern_id);
            Date date = new Date();
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            new_note.setDate(PublicResources.getCollectedDate(date_format.format(date)));
            spans_editor.makeCleaning();
            new_note.setSpans(spans_editor.getTextSpans());
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
        return new_note;
    }

    /**********************************************************************************
     * инициализация пинов */
    private int[] getPinIds() {
        return data_items.getDrawableItemsIds();
    }

    /**********************************************************************************
     * МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        colors = null;
        pin_ids = null;
        startDeletionTempFiles();
        note_name.setOnClickListener(null);
        note_main_text.setOnClickListener(null);
        for (int i = 0; i < this_note_place_ll.getChildCount(); ++i) {
            this_note_place_ll.getChildAt(i).setOnClickListener(null);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // проверка наличия разрешения на запись
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // если нет разрешений
            ActivityCompat.requestPermissions(
                    activity,
                    PublicResources.PERMISSIONS_STORAGE,
                    PublicResources.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public boolean getEmpty() {
        return ((note_name.getText().length()
                + note_main_text.getText().length()) == 0) && drawings.isEmpty();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PublicResources.REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PublicResources.has_external_permission = true;
                }
                else {
                    PublicResources.has_external_permission = false;
                }
                return;
        }
    }

    private ArrayList<Uri> getImagesUri() {
        ArrayList<Uri> temp_image_uris = new ArrayList<>();
        File[] image_files = PublicResources.TEMP_IMAGES_DIR.listFiles();
        if (image_files.length > 0) {
            for (File image_file : image_files) {
                try {
                    temp_image_uris.add(FileProvider
                            .getUriForFile(Note.this,
                                    PublicResources.FILE_PROVIDER,
                                    image_file));
                    Log.d(PublicResources.DEBUG_LOG_TAG,"uri >>> " +
                            temp_image_uris.get(temp_image_uris.size() - 1).toString());
                } catch (Exception e) {
                    Log.d(PublicResources.DEBUG_LOG_TAG, e.getMessage());
                }
            }
        }
        else {
            Log.d(PublicResources.DEBUG_LOG_TAG,
                    "Current directory hasn't exists files.");
        }
        return temp_image_uris;
    }

    @Override
    protected void onStop() {
        boolean is_note_empty = getEmpty();
        if (!is_note_empty) {
            saveNote();
        }
        if (current_toast != null) {
            current_toast.cancel();
        }
        current_toast = null;
        super.onStop();
    }
}