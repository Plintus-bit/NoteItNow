package com.example.noteitnow.notes_entities;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;
import com.example.noteitnow.statics_entity.TempResources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteSavings {
    public static void saveNote(NoteStructure note) {
        // сохранение файла
        File file = PublicResources.createFile(note.getFileName(), Doings.GSON);
        PublicResources.saveOrReplaceGson(file.getAbsolutePath(),
                NoteStructure.getNoteToGsonString(note));
    }

    /**********************************************************************************
     * работа с файлами (рисунки) */

    // добавить рисунок
    public static void saveOrReplaceDrawing(File file, Bitmap bitmap) {
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.d(PublicResources.DEBUG_LOG_TAG, "error: " + e.getMessage());
        }
    }

    public static String getNewFileName(Doings file_extension) {
        String extension = "_";
        String time_stamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        switch (file_extension) {
            case GSON:
                extension = PublicResources.TEXT_GSON_PREFIX;
                break;
            case PNG:
                extension = PublicResources.IMAGE_PNG_PREFIX;
                break;
        }
        return (extension + time_stamp + "_");
    }

}
