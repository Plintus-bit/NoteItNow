package com.example.noteitnow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.example.noteitnow.statics_entity.PublicResourсes;

import java.util.ArrayList;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private class DrawThread extends Thread {
        private boolean is_running = true;
        private SurfaceHolder surface_holder;

        private int current_paint_color;
        private Bitmap canvas_bitmap;
        private Path draw_path;
        private Paint draw_paint;

        public DrawThread(SurfaceHolder surface_holder) {
            this.surface_holder = surface_holder;

            draw_paint = new Paint();
            current_paint_color = getResources().getColor(R.color.black, null);
            draw_paint.setColor(current_paint_color);
        }

        // методы вложенного класса
        public void setRunning(boolean is_running) {
            this.is_running = is_running;
        }

        public boolean getRunning() {
            return is_running;
        }

        @Override
        public void run() {
            Canvas canvas;
            Log.d(PublicResourсes.DEBUG_LOG_TAG, ">>> run: the run is start!");
            while (is_running) {
                canvas = null;
                try {
                    if (PublicResourсes.isDraw()) {
                        canvas = surface_holder.lockCanvas(null);
                        if (canvas == null) {
                            continue;
                        }
                        // отдельный поток для рисования
                        // тут будет происходить рисование
                        canvas.drawRect(new Rect(20, 20, 600, 600), draw_paint);
                    }
                }
                finally {
                    if (canvas != null) {
                        surface_holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
            Log.d(PublicResourсes.DEBUG_LOG_TAG, ">>> run: End of Thread!");
        }
    }
    private DrawThread draw_thread;
    private Canvas canvas;

    // методы основного класса
    public DrawView(Context context) {
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
//        Bitmap canvas_bitmap = Bitmap.createBitmap(3000, 3000, Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(canvas_bitmap);
        // surfaceCreated(getHolder());
        // draw_thread = new DrawThread(getHolder());
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(PublicResourсes.DEBUG_LOG_TAG, ">>> Surface create");
        draw_thread = new DrawThread(getHolder());
        draw_thread.setRunning(true);
        draw_thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder,
                               int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d(PublicResourсes.DEBUG_LOG_TAG, ">>> Surface destroy");
        boolean is_retry = true;
        draw_thread.setRunning(false);
        while (is_retry) {
            try {
                draw_thread.join();
                is_retry = false;
            } catch (Exception e) {
                Log.d(PublicResourсes.DEBUG_LOG_TAG, e.getMessage());
            }
        }
    }



    // методы для самого рисования

}
