package com.example.noteitnow.notes_entities;

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
    }

    public TextSpansEditor(ArrayList<TextSpans> spans) {
        this.spans = spans;
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
    public void addSpan(TextSpans span) {
        spans.add(span);
    }

    // Если новый span что-то перекрывает, удалить старый
    public void removeUselessTextSpans(TextSpans new_span) {
        ArrayList<Integer> index_for_delete = new ArrayList<Integer>();
        for (int i = 0; i < spans.size(); ++i) {
            if (!isDataTypeEquals(new_span, spans.get(i))) {
                // тут происходит особая обработка разнотипов
                continue;
            }
            // перебор возможных вариантов
            switch (new_span.getSpanType()) {
                case TEXT_BG:
                case COLOR:
                    if (new_span.getStart() <= spans.get(i).getStart()
                            && new_span.getEnd() >= spans.get(i).getEnd()) {
                        index_for_delete.add(i);
                    }
                    break;
                default:
                    // nothing
            }
        }
//        Log.d(PublicResources.DEBUG_LOG_TAG , "before delete: " + spans.size());
        if (index_for_delete.size() > 0) {
            for (int i = 0; i < index_for_delete.size(); ++i) {
                spans.remove(index_for_delete.get(i) - i);
            }
        }
        index_for_delete = null;
        spans.add(new_span);
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
//            Log.d(PublicResources.DEBUG_LOG_TAG, "FIXED SPAN >>> " +
//                    String.valueOf(spans.get(index).getStart()) + " "
//                    + String.valueOf(spans.get(index).getEnd()));
        }
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
            else if (new_edit_indexes.getEnd() > old_edit_indexes.getEnd()) {
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

    public void makeCleaning() {
        fixTextSpans();
        edit_indexes.clear();
    }
}
