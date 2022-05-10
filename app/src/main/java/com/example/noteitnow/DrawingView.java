package com.example.noteitnow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.noteitnow.statics_entity.Doings;
import com.example.noteitnow.statics_entity.PublicResources;

public class DrawingView extends View {
    // пуст ли canvas
    private boolean is_empty;

    // был ли отредактирован
    private boolean is_edit;

    // Текущие цвета
    private int current_paint_color;
    private int current_marker_paint_color;
    private int current_canvas_bg_color;
    private int current_marker_opacity;

    // текущие значения ширины
    private float current_stroke_width;
    private float current_marker_stroke_width;

    // Путь для хранения точек
    private Path draw_path;

    // Сами кисти
    private Paint draw_paint;
    private Paint brush;
    private Paint marker;
    private Paint eraser;

    private Canvas canvas;

    // Нужные переменные
    private Bitmap canvas_bitmap;

    // Для рисования
    private float x, y;
    private static final float TOUCH_TOLERANCE = 4;

    public DrawingView(Context context) {
        super(context);
        init();
        canvas_bitmap = Bitmap
                .createBitmap(PublicResources.device_width, (int) (640 * PublicResources.DP),
                        Bitmap.Config.ARGB_8888);
        canvas = new Canvas(canvas_bitmap);
        is_empty = true;
        is_edit = false;
    }

    // методы для самого рисования

    public void setMarker() {
        marker = new Paint();
        marker.setColor(current_marker_paint_color);
        marker.setStrokeWidth(current_marker_stroke_width);
        marker.setStrokeCap(Paint.Cap.SQUARE);
        marker.setDither(true);
        marker.setAntiAlias(true);
        marker.setStrokeJoin(Paint.Join.BEVEL);
        marker.setStyle(Paint.Style.STROKE);
    }

    public void setBrush() {
        brush = new Paint(Paint.DITHER_FLAG);
        brush.setColor(current_paint_color);
        brush.setStrokeWidth(current_stroke_width);
        brush.setStrokeCap(Paint.Cap.ROUND);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setDither(true);
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
    }

    public void setEraser() {
        eraser = new Paint();
        eraser.setAlpha(0xFF);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setStrokeWidth(current_stroke_width);
        eraser.setMaskFilter(null);
        eraser.setStrokeCap(Paint.Cap.SQUARE);
        eraser.setAntiAlias(true);
        eraser.setStrokeJoin(Paint.Join.BEVEL);
        eraser.setStyle(Paint.Style.STROKE);
    }

    //        public void setTextBrush() {
//
//        }

    private void init() {
        draw_path = new Path();
        setDefaultColor();
        setDefaultStrokeWidth();
        current_marker_opacity = PublicResources.DEFAULT_OPACITY;
        setBrush();
        setMarker();
        setEraser();
        draw_paint = brush;
    }

    private void setDefaultColor() {
        current_paint_color = PublicResources.DEFAULT_COLOR;
        current_marker_paint_color = PublicResources.DEFAULT_COLOR;
        current_canvas_bg_color = PublicResources.DEFAULT_BG_COLOR;
    }

    private void setDefaultStrokeWidth() {
        current_stroke_width = PublicResources.DEFAULT_STROKE_WIDTH;
        current_marker_stroke_width = PublicResources.DEFAULT_STROKE_WIDTH * 2;
    }

    // Касания
    public void touchStart(float x, float y) {
        draw_path.reset();
        draw_path.moveTo(x, y);
        this.x = x;
        this.y = y;
    }

    public void touchInMove(float x, float y) {
        float dx = Math.abs(x - this.x),
                dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            draw_path.quadTo(this.x, this.y, (x + this.x) / 2, (y + this.y) / 2);
            this.x = x;
            this.y = y;
        }
    }

    public  void touchEnd(float x, float y) {
        draw_path.lineTo(x, y);
        canvas.drawPath(draw_path, draw_paint);
        draw_path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (is_empty || !is_edit) {
            is_empty = false;
            is_edit = true;
        }
        float x = event.getX(),
                y = event.getY();
        marker.setAlpha(current_marker_opacity);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchInMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchEnd(x, y);
                invalidate();
                break;
        }
        marker.setAlpha(0xFF);
        eraser.setXfermode(null);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(current_canvas_bg_color);
        canvas.drawBitmap(canvas_bitmap, 0, 0, draw_paint);
        canvas.drawPath(draw_path, draw_paint);
    }

    public void clearCanvas() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        is_empty = true;
        invalidate();
    }

    public void setOpacity(int opacity, Doings draw_tool) {
        current_marker_opacity = opacity;
        marker.setAlpha(opacity);
    }

    public void setColor(int color, Doings draw_tool) {
        switch (draw_tool) {
            case BRUSH:
                current_paint_color = color;
                brush.setColor(color);
                break;
            case MARKER:
                current_marker_paint_color = color;
                marker.setColor(color);
                break;
            case BACKGROUND:
                current_canvas_bg_color = color;
                invalidate();
                break;
            default:
                // nothing
        }
    }

    public void setStroke(int width, Doings draw_tool) {
        switch (draw_tool) {
            case BRUSH:
                current_stroke_width = width;
                brush.setStrokeWidth(width);
                eraser.setStrokeWidth(width);
                break;
            case MARKER:
                current_marker_stroke_width = width;
                marker.setStrokeWidth(width);
                break;
        }
    }

    public int getColor(Doings draw_tool) {
        switch (draw_tool) {
            case BRUSH:
                return current_paint_color;
            case MARKER:
                return current_marker_paint_color;
            case BACKGROUND:
                return current_canvas_bg_color;
        }
        return current_paint_color;
    }

    public int getOpacity(Doings draw_tool) {
        return current_marker_opacity;
    }

    public float getStroke(Doings stroke_width) {
        switch (stroke_width) {
            case BRUSH:
                return current_stroke_width;
            case MARKER:
                return current_marker_stroke_width;
        }
        return current_stroke_width;
    }

    public void setCurrentBrush(Doings draw_tool) {
        switch (draw_tool) {
            case BRUSH:
                draw_paint = brush;
                break;
            case MARKER:
                draw_paint = marker;
                break;
            case ERASER:
                draw_paint = eraser;
                break;
            default:
                // nothing
        }
    }

    // пустой ли холст
    public boolean isCanvasEmpty() {
        return is_empty;
    }

    // методы получения рисунка
    public Bitmap getDrawing() {
        return canvas_bitmap;
    }

    public boolean isCanvasEdit() {
        return is_edit;
    }

    // метод установки существующего рисунка
    public void setDrawing(Bitmap exist_drawing, int bg_color) {
        current_canvas_bg_color = bg_color;
        Bitmap immutable_bmt = Bitmap.createBitmap(exist_drawing);
        canvas_bitmap = immutable_bmt.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(canvas_bitmap);
        is_empty = false;
    }
}