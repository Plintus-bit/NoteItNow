package com.example.noteitnow.notehold;

import android.view.View;

import androidx.cardview.widget.CardView;

public interface NoteActionsListener {
    void onClick(View view);
    void onLongClick(View view, CardView card);
}
