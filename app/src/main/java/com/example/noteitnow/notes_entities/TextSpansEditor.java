package com.example.noteitnow.notes_entities;

import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.EditText;

import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TextSpansEditor {

    private final static int UNKNOWN_STATE = -111;
    private final static int BEFORE = -1;
    private final static int INSIDE = 0;
    private final static int AFTER = 1;
    private final static int BEFORE_INSIDE = 2;
    private final static int AFTER_INSIDE = 3;
    private final static int ALL = 4;
    private final static int ALL_INSIDE = 5;
    private final static int START_INSIDE = 6;
    private final static int END_INSIDE = 7;

    // текущее количество span'ов
    private int spans_size;

    // массив span'ов
    private ArrayList<TextSpans> spans;

    // длина текста
    private int text_length;

    // массив индексов для редактирования span'ов
    private ArrayList<EditIndexes> edit_indexes;

    /**********************************************************************************
     * Конструкторы */
    public TextSpansEditor() {
        spans = new ArrayList<TextSpans>();
        edit_indexes = new ArrayList<EditIndexes>();
        spans_size = spans.size();
    }

    public TextSpansEditor(ArrayList<TextSpans> spans) {
        this.spans = spans;
        spans_size = this.spans.size();
        edit_indexes = new ArrayList<EditIndexes>();
    }

    /**********************************************************************************
     * Getter'ы */
    public ArrayList<TextSpans> getTextSpans() {
        return this.spans;
    }

    /**********************************************************************************
     * Setter'ы */
    public void setTextSpans(ArrayList<TextSpans> spans) {
        this.spans = spans;
    }

    /**********************************************************************************
     * Методы */
    // Если новый span что-то перекрывает, удалить старый
    public SpannableStringBuilder removeUselessTextSpans(TextSpans new_span, String text) {
        ArrayList<Integer> index_for_delete = new ArrayList<Integer>();
        String temp_text = null;
        boolean is_need_to_add_span = true;
        EditIndexes new_span_index = new EditIndexes(new_span.getStart(), new_span.getEnd());
//        Log.d(PublicResources.DEBUG_LOG_TAG, "spans before edit >>> " + spans_size);
        makeCleaning();
        for (int i = 0; i < spans.size(); ++i) {
            EditIndexes old_span_index =
                    new EditIndexes(spans.get(i).getStart(),spans.get(i).getEnd());
            int state = getSpecialState(new_span_index, old_span_index);
            if (!isDataTypeEquals(new_span, spans.get(i))) {
                // тут происходит особая обработка разнотипов
                switch (new_span.getSpanType()) {
                    case NORMAL:
                        is_need_to_add_span = false;
                        switch (spans.get(i).getSpanType()) {
                            case BOLD:
                            case ITALIC:
                                if (fixSpecialSpan(new_span_index, i, state,
                                        new_span.getSpanType())) {
                                    temp_text = text;
                                }
                                if (spans_size > spans.size()) {
                                    --i;
                                }
                                else if (spans_size < spans.size()) {
                                    ++i;
                                }
                                continue;
                            default:
                                continue;
                        }
                    case CLEAR:
                        is_need_to_add_span = false;
                        if (fixSpecialSpan(new_span_index, i, state, new_span.getSpanType())) {
                            temp_text = text;
                        }
                        if (spans_size > spans.size()) {
                            --i;
                        }
                        else if (spans_size < spans.size()) {
                            ++i;
                        }
                        continue;
                    default:
                        continue;
                }
            }
            // перебор возможных вариантов
            if (isDataEquals(new_span, spans.get(i))) {
                // при одинаковых данных бывает удаление
                if (fixSpecialSpan(new_span_index, i, state, new_span.getSpanType())) {
                    is_need_to_add_span = false;
                    temp_text = text;
                    if (spans_size > spans.size()) {
                        --i;
                    }
                    else if (spans_size < spans.size()) {
                        ++i;
                    }
                }
            }
            else {
                // при разных данных всегда вставка
                switch (new_span.getSpanType()) {
                    case TEXT_BG:
                    case COLOR:
//                        Log.d(PublicResources.DEBUG_LOG_TAG, "STATE >>> " + state);
                        switch (state) {
                            case ALL:
                            case ALL_INSIDE:
                                index_for_delete.add(i);
                                temp_text = text;
                                continue;
                            default:
                                // nothing
                        }
                        continue;
                    default:
                        // nothing
                }
            }
        }
//        Log.d(PublicResources.DEBUG_LOG_TAG , "before delete: " + spans.size());
        if (index_for_delete.size() > 0) {
            for (int i = 0; i < index_for_delete.size(); ++i) {
                spans.remove(index_for_delete.get(i) - i);
            }
        }
        index_for_delete = null;
        if (is_need_to_add_span) {
            spans.add(new_span);
        }
        if (temp_text != null) {
            return getSpannedString(temp_text);
        }
        spans_size = spans.size();
        return null;
    }

    private boolean isDataTypeEquals(TextSpans value_1, TextSpans value_2) {
        return value_1.getSpanType().equals(value_2.getSpanType());
    }

    private boolean isDataEquals(TextSpans value_1, TextSpans value_2) {
        return value_1.getData() == value_2.getData();
    }

    /**********************************************************************************
     * Сбор индексов изменений */
    public void addEditIndex(int start, int end, Doings operation_type) {
        if (spans.size() == 0) {
            return;
        }
        EditIndexes new_index = new EditIndexes(start, end, operation_type);
        for (int index = 0; index < edit_indexes.size(); ++index) {
            if (!(edit_indexes.get(index).getType() == operation_type)) {
                continue;
            }
            int state;
            EditIndexes temp_index = null;
            if (operation_type == Doings.INSERTION) {
                state = getInsertionState(new_index, edit_indexes.get(index));
                if (state == INSIDE) {
                    temp_index = edit_indexes.get(index);
                    temp_index.setEnd(edit_indexes.get(index).getEnd() + new_index.getLength());
                    edit_indexes.set(index, temp_index);
                    return;
                }
            }
            else {
                state = getDeletionState(new_index, edit_indexes.get(index));
                if (state == INSIDE || state == BEFORE) {
                    temp_index = edit_indexes.get(index);
                    temp_index.setStart(edit_indexes.get(index).getStart() - new_index.getLength());
                    edit_indexes.set(index, temp_index);
                    return;
                }
            }
        }
        edit_indexes.add(new_index);
    }

    // правка всех изменений
    public void fixTextSpans() {
        for (int i = 0; i < edit_indexes.size(); ++i) {
            fixIndexesInTextSpans(edit_indexes.get(i));
        }
    }

    private void fixIndexesInTextSpans(EditIndexes edit_index) {
        int before_fix_length = spans.size();
        for (int index = 0; index < spans.size(); ++index) {
            int state;
            EditIndexes previous_index = new EditIndexes(
                    spans.get(index).getStart(),
                    spans.get(index).getEnd()
            );
            if (edit_index.getType() == Doings.INSERTION) {
                state = getInsertionState(edit_index, previous_index);
                fixSpanInsertion(state, index, edit_index);
            }
            else {
                state = getDeletionState(edit_index, previous_index);
                fixSpanDeletion(state, index, edit_index);
                if (before_fix_length > spans.size()) {
                    --index;
                }
            }
        }
    }

    // правка определённого span'а
    private boolean fixSpecialSpan(EditIndexes edit_index, int span_index, int state, Doings type) {
        TextSpans old_span = spans.get(span_index);
        boolean is_need_to_redraw = true;
        if (type == Doings.NORMAL || type == Doings.CLEAR) {
            switch (state) {
                case ALL_INSIDE:
                case ALL:
                    spans.remove(span_index);
                    break;
                case AFTER_INSIDE:
                case END_INSIDE:
                    old_span.setEnd(edit_index.getStart());
                    spans.set(span_index, old_span);
                    break;
                case BEFORE_INSIDE:
                case START_INSIDE:
                    old_span.setStart(edit_index.getEnd());
                    spans.set(span_index, old_span);
                    break;
                case INSIDE:
                    TextSpans first_span = new TextSpans(
                            old_span.getStart(), edit_index.getStart(),
                            old_span.getSpanType(), old_span.getData(), old_span.getFlag()
                    );
                    TextSpans second_span = new TextSpans(
                            edit_index.getEnd(), old_span.getEnd(),
                            old_span.getSpanType(), old_span.getData(), old_span.getFlag()
                    );
                    spans.remove(span_index);
                    spans.add(span_index, second_span);
                    spans.add(span_index, first_span);
                    break;
                default:
                    is_need_to_redraw = false;
            }
            return is_need_to_redraw;
        }
        switch (state) {
            case ALL_INSIDE:
                spans.remove(span_index);
                break;
            case AFTER_INSIDE:
                old_span.setEnd(edit_index.getEnd());
                spans.set(span_index, old_span);
                break;
            case BEFORE_INSIDE:
                old_span.setStart(edit_index.getStart());
                spans.set(span_index, old_span);
                break;
            case INSIDE:
                TextSpans first_span = new TextSpans(
                        old_span.getStart(), edit_index.getStart(),
                        old_span.getSpanType(), old_span.getData(), old_span.getFlag()
                );
                TextSpans second_span = new TextSpans(
                        edit_index.getEnd(), old_span.getEnd(),
                        old_span.getSpanType(), old_span.getData(), old_span.getFlag()
                );
                spans.remove(span_index);
                spans.add(span_index, second_span);
                spans.add(span_index, first_span);
                break;
            case START_INSIDE:
                old_span.setStart(edit_index.getEnd());
                spans.set(span_index, old_span);
                break;
            case END_INSIDE:
                old_span.setEnd(edit_index.getStart());
                spans.set(span_index, old_span);
                break;
            case ALL:
                old_span.setStart(edit_index.getStart());
                old_span.setEnd(edit_index.getEnd());
                spans.set(span_index, old_span);
                break;
            default:
                is_need_to_redraw = false;
        }
        Log.d(PublicResources.DEBUG_LOG_TAG, "FIX STATE >>> " + state);
        return is_need_to_redraw;
    }

    private void fixSpanInsertion(int state, int span_index, EditIndexes new_index) {
        TextSpans temp_span = spans.get(span_index);
        switch (state) {
            case BEFORE:
                temp_span.setStart(spans.get(span_index).getStart() + new_index.getLength());
                temp_span.setEnd(spans.get(span_index).getEnd() + new_index.getLength());
                spans.set(span_index, temp_span);
                break;
            case INSIDE:
                temp_span.setEnd(spans.get(span_index).getEnd() + new_index.getLength());
                spans.set(span_index, temp_span);
                break;
            case AFTER:
            default:
                // nothing
        }
    }

    private void fixSpanDeletion(int state, int span_index, EditIndexes new_index) {
        TextSpans temp_span = spans.get(span_index);
        switch (state) {
            case INSIDE:
                temp_span.setEnd(spans.get(span_index).getEnd() - new_index.getLength());
                spans.set(span_index, temp_span);
                break;
            case BEFORE_INSIDE:
                temp_span.setStart(new_index.getStart());
                temp_span.setEnd(spans.get(span_index).getEnd() - new_index.getLength());
                spans.set(span_index, temp_span);
                break;
            case BEFORE:
                temp_span.setStart(temp_span.getStart() - new_index.getLength());
                temp_span.setEnd(temp_span.getEnd() - new_index.getLength());
                spans.set(span_index, temp_span);
                break;
            case AFTER_INSIDE:
                temp_span.setEnd(new_index.getStart());
                spans.set(span_index, temp_span);
                break;
            case ALL:
            case ALL_INSIDE:
                spans.remove(span_index);
                break;
            case AFTER:
            default:
                // nothing
        }
    }

    private int getInsertionState(EditIndexes new_edit_indexes, EditIndexes old_edit_indexes) {
        if (new_edit_indexes.getStart() < old_edit_indexes.getStart()) {
            return BEFORE;
        }
        else if (new_edit_indexes.getStart() > old_edit_indexes.getEnd()) {
            return AFTER;
        }
        else if (new_edit_indexes.getStart() <= old_edit_indexes.getEnd()) {
            return INSIDE;
        }
        return UNKNOWN_STATE;
    }

    private int getDeletionState(EditIndexes new_edit_indexes, EditIndexes old_edit_indexes) {
        if (new_edit_indexes.getEnd() <= old_edit_indexes.getStart()) {
            return BEFORE;
        }
        else if (new_edit_indexes.getStart() >= old_edit_indexes.getEnd()) {
            return AFTER;
        }
        else {
            return getDeletionInsideState(new_edit_indexes, old_edit_indexes);
        }
    }

    private int getDeletionInsideState(EditIndexes new_edit_indexes, EditIndexes old_edit_indexes) {
        if (new_edit_indexes.getStart() <= old_edit_indexes.getStart()) {
            if (new_edit_indexes.getEnd() == old_edit_indexes.getEnd()
                    && new_edit_indexes.getStart() == old_edit_indexes.getStart()) {
                return ALL_INSIDE;
            }
            else if (new_edit_indexes.getEnd() >= old_edit_indexes.getEnd()) {
                return ALL;
            }
            else if (new_edit_indexes.getStart() < old_edit_indexes.getStart()) {
                return BEFORE_INSIDE;
            }
            else {
                return INSIDE;
            }
        }
        else if (new_edit_indexes.getEnd() > old_edit_indexes.getEnd()) {
            return AFTER_INSIDE;
        }
        return INSIDE;
    }

    // состояния при одинаковых данных
    private int getSpecialState(EditIndexes new_edit_indexes, EditIndexes old_edit_indexes) {
//        Log.d(PublicResources.DEBUG_LOG_TAG, "NEW >>> start: " + new_edit_indexes.getStart()
//                + "; end: " + new_edit_indexes.getEnd());
//        Log.d(PublicResources.DEBUG_LOG_TAG, "OLD >>> start: " + old_edit_indexes.getStart()
//                + "; end: " + old_edit_indexes.getEnd());
        if (new_edit_indexes.getEnd() < old_edit_indexes.getStart()) {
            return BEFORE;
        }
        if (new_edit_indexes.getStart() > old_edit_indexes.getEnd()) {
            return AFTER;
        }
        if (new_edit_indexes.getStart() < old_edit_indexes.getStart()) {
            if (new_edit_indexes.getEnd() >= old_edit_indexes.getEnd()) {
                return ALL;
            }
            else {
                return BEFORE_INSIDE;
            }
        }
        else if (new_edit_indexes.getStart() == old_edit_indexes.getStart()) {
            if (new_edit_indexes.getEnd() > old_edit_indexes.getEnd()) {
                return ALL;
            }
            else if (new_edit_indexes.getEnd() == old_edit_indexes.getEnd()) {
                return ALL_INSIDE;
            }
            else {
                return START_INSIDE;
            }
        }
        else {
            if (new_edit_indexes.getEnd() < old_edit_indexes.getEnd()) {
                return INSIDE;
            }
            if (new_edit_indexes.getEnd() == old_edit_indexes.getEnd()) {
                return END_INSIDE;
            }
            else {
                return AFTER_INSIDE;
            }
        }
    }

    public void makeCleaning() {
        fixTextSpans();
        edit_indexes.clear();
    }

    /**********************************************************************************
     * Перерисовка span'ов */
    public SpannableStringBuilder getSpannedString(String source_text) {
        SpannableStringBuilder spanned_text = new SpannableStringBuilder(source_text);
        for (int i = 0; i < spans.size(); ++i) {
            getTextWithNewSpan(spanned_text, spans.get(i));
        }
        return spanned_text;
    }

    public static SpannableStringBuilder getTextWithNewSpan(
            SpannableStringBuilder text, TextSpans span) {
        switch (span.getSpanType()) {
            case COLOR:
                text.setSpan(
                        new ForegroundColorSpan(span.getData()),
                        span.getStart(), span.getEnd(),
                        span.getFlag());
                break;
            case TEXT_BG:
                text.setSpan(
                        new BackgroundColorSpan(span.getData()),
                        span.getStart(), span.getEnd(),
                        span.getFlag());
                break;
            case BOLD:
            case NORMAL:
            case ITALIC:
                text.setSpan(
                        new StyleSpan(span.getData()),
                        span.getStart(), span.getEnd(),
                        span.getFlag());
                break;
            default:
                //nothing
        }
        return text;
    }
}
