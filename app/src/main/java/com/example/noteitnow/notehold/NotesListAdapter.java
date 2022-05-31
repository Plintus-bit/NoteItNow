package com.example.noteitnow.notehold;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteitnow.R;
import com.example.noteitnow.notes_entities.NoteStructure;

import java.util.ArrayList;
import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    Context context;
    ArrayList<NoteStructure> notes_list;
    NoteActionsListener note_cl;

    public NotesListAdapter(Context context, ArrayList<NoteStructure> notes,
                            NoteActionsListener note_cl) {
        this.context = context;
        this.notes_list = notes;
        this.note_cl = note_cl;
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
        holder.note_card_holder.setCardBackgroundColor(notes_list.get(position).getBg());
        holder.note_card_holder.setTag(notes_list.get(position).getFileName());
        holder.note_pin_btn
                .setImageDrawable(context.getDrawable(notes_list.get(position).getPin()));
        holder.note_title_txt.setText(notes_list.get(position).getName());
        holder.note_text_txt.setText(notes_list.get(position).getText());
        if (notes_list.get(position).getPinned()) {
            holder.pinned_icon.setVisibility(View.VISIBLE);
        }
        else {
            holder.pinned_icon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notes_list.size();
    }

    public void addFilteredNotes(ArrayList<NoteStructure> filtered_list) {
        notes_list = filtered_list;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    CardView note_card_holder;
    ImageButton note_pin_btn;
    TextView note_title_txt, note_text_txt;
    ImageView pinned_icon;


    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        note_card_holder = itemView.findViewById(R.id.note_card_holder);
        note_pin_btn = itemView.findViewById(R.id.note_pin_btn);
        note_title_txt = itemView.findViewById(R.id.note_title_txt);
        note_text_txt = itemView.findViewById(R.id.note_text_txt);
        pinned_icon = itemView.findViewById(R.id.pinned_icon);
    }
}

