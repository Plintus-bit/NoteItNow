package com.example.noteitnow.notehold;

import android.content.Context;
import android.content.res.Resources;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteitnow.R;
import com.example.noteitnow.notes_entities.NoteStructure;
import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.example.noteitnow.statics_entity.items.ColorItems;
import com.example.noteitnow.statics_entity.items.Months;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    ArrayList<NoteStructure> notes_list;
    Animation anim;
    NoteActionsListener note_cl;
    int[] colors;
    Months months;
    Resources res;
    boolean is_darcula_active;

    public NotesListAdapter(Context context, ArrayList<NoteStructure> notes,
                            NoteActionsListener note_cl, Resources res) {
        this.res = res;
        this.context = context;
        this.notes_list = notes;
        this.note_cl = note_cl;
        months = new Months();
        colors = new ColorItems(res).getColors(Doings.PASTEL_SOFT_PINNED);
        is_darcula_active = PublicResources.preferences
                .getBoolean(PublicResources.THEME_KEY, false);
        anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.note_card_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.note_card_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note_cl.onClick(view);
            }
        });
        holder.note_card_holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                note_cl.onLongClick(view, holder.note_card_holder);
                return false;
            }
        });
        if (is_darcula_active) {
            holder.note_title_txt.setTextColor(res.getColor(R.color.very_light_grey, null));
            holder.note_text_txt.setTextColor(res.getColor(R.color.mono_light_grey, null));
            holder.note_pin_btn.setColorFilter(res.getColor(R.color.mono_medium_grey, null));
            holder.note_date_txt.setTextColor(res.getColor(R.color.mono_middle_grey, null));
        }
        if (notes_list.get(position).getBg() == PublicResources.DEFAULT_BG_COLOR_VALUE) {
            if (is_darcula_active) {
                holder.note_card_holder
                        .setCardBackgroundColor(res.getColor(R.color.darkest_grey, null));
            }
            else {
                holder.note_card_holder
                        .setCardBackgroundColor(res.getColor(R.color.white, null));
            }
        }
        else {
            holder.note_card_holder.setCardBackgroundColor(notes_list.get(position).getBg());
        }
        holder.note_card_holder.setTag(notes_list.get(position).getFileName());
        holder.note_pin_btn
                .setImageDrawable(context.getDrawable(notes_list.get(position).getPin()));
        if (notes_list.get(position).getName()
                .equals(PublicResources.EXTRA_DEFAULT_STRING_VALUE)) {
            holder.note_title_txt
                    .setText(getNewNoteName(notes_list.get(position).getText()));
        }
        else {
            holder.note_title_txt
                    .setText(notes_list.get(position).getName());
        }
        holder.note_text_txt.setText(notes_list.get(position).getText());
        if (notes_list.get(position).getPinned()) {
            holder.pinned_icon.setVisibility(View.VISIBLE);
            holder.pinned_icon.setColorFilter(getRandomColor());
        }
        else {
            holder.pinned_icon.setVisibility(View.GONE);
        }
        ArrayList<Integer> date = notes_list.get(position).getDate();
        holder.note_date_txt.setText(getDateFormat(date));
//        holder.note_date_txt.startAnimation(anim);
    }

    private String getDateFormat(ArrayList<Integer> date) {
        String date_format = "";
        String current_month = res.getString(months.getMonth(date.get(1)));
        date_format += getNormalDateForm(date.get(3)) + ":"
                + getNormalDateForm(date.get(4)) + ":"
                + getNormalDateForm(date.get(5)) + ", "
                + current_month + " "
                + String.valueOf(date.get(2)) +
                ", " + String.valueOf(date.get(0));
        return date_format;
    }

    private int getRandomColor() {
        Random random_int = new Random();
        return colors[random_int.nextInt(colors.length)];
    }

    private String getNormalDateForm(Integer date_part) {
        if (date_part < 10) {
            return "0" + String.valueOf(date_part);
        }
        return String.valueOf(date_part);
    }

    public void setIsDarculaActive(boolean is_darcula_active) {
        this.is_darcula_active = is_darcula_active;
    }

    /**********************************************************************************
     * получение видимого имени заметки */
    private String getNewNoteName(String text) {
        String new_note_name = "";
        final int MAX_CHARS_LEN = 14;
        final String SEP = "..";
        final int MAX_CHAR_LEN_WITH_SEP = 16;
        if (text.length() > MAX_CHAR_LEN_WITH_SEP) {
            for (int i = 0; i < MAX_CHARS_LEN; ++i) {
                if (text.charAt(i) == '\n') {
                    break;
                }
                else {
                    new_note_name += text.charAt(i);
                }
            }
            return new_note_name + SEP;
        }
        else if (text.length() == 0) {
            new_note_name = PublicResources.DEFAULT_NOTE_NAME;
            return new_note_name;
        }
        return text;
    }

    @Override
    public int getItemCount() {
        return notes_list.size();
    }

    public void addFilteredNotes(ArrayList<NoteStructure> filtered_list) {
        notes_list = filtered_list;
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    CardView note_card_holder;
    ImageButton note_pin_btn;
    TextView note_title_txt, note_text_txt, note_date_txt;
    ImageView pinned_icon;


    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        note_card_holder = itemView.findViewById(R.id.note_card_holder);
        note_pin_btn = itemView.findViewById(R.id.note_pin_btn);
        note_title_txt = itemView.findViewById(R.id.note_title_txt);
        note_text_txt = itemView.findViewById(R.id.note_text_txt);
        pinned_icon = itemView.findViewById(R.id.pinned_icon);
        note_date_txt = itemView.findViewById(R.id.note_date_txt);
    }
}

