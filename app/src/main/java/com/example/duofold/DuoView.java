package com.example.duofold;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

public class DuoView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private float angle = 0f;
    private boolean sensorAvailable = false;
    private long downAt;
    private float downX, downY;

    private final int[] iconGlyphs = new int[]{
            0x1F4DE, 0x1F4AC, 0x1F4F7, 0x1F4E7,
            0x1F4C5, 0x1F4CD, 0x1F3B5, 0x2699
    };

    public DuoView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        text.setTypeface(android.graphics.Typeface.create("sans", android.graphics.Typeface.NORMAL));
        setFocusable(true);
    }

    public void setHingeAngle(float newAngle) {
        // Light smoothing keeps the UI visually connected to the physical hinge.
        angle = angle * 0.72f + newAngle * 0.28f;
        invalidate();
    }

    public void setSensorAvailable(boolean available) {
        sensorAvailable = available;
        invalidate();
    }

    private float openT() {
        return smoothstep(0f, 180f, angle);
    }

    private float smoothstep(float a, float b, float x) {
        float t = Math.max(0f, Math.min(1f, (x - a) / (b - a)));
        return t * t * (3f - 2f * t);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int w = getWidth();
        int h = getHeight();
        float t = openT();

        // Wallpaper: a soft, Apple-like glass gradient without copying Apple assets.
        p.setShader(new LinearGradient(0, 0, w, h,
                Color.rgb(16, 18, 26), Color.rgb(47, 31, 62), Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);

        drawAmbientGlow(c, w, h, t);
        drawTopBar(c, w, h);

        if (w < h * 0.9f) {
            // Outer display posture: compact home.
            drawSingleHome(c, w, h, 1f - t * 0.15f);
        } else {
            // Inner display posture: two large panels which visually converge around the hinge.
            drawDualHome(c, w, h, t);
        }

        drawAngleDebug(c, w, h);
    }

    private void drawAmbientGlow(Canvas c, int w, int h, float t) {
        p.setShader(new LinearGradient(0, h * 0.15f, w, h * 0.85f,
                Color.argb(70, 120, 160, 255), Color.argb(8, 255, 255, 255), Shader.TileMode.CLAMP));
        c.drawRect(0, 0, w, h, p);
        p.setShader(null);
    }

    private void drawTopBar(Canvas c, int w, int h) {
        text.setColor(Color.argb(235, 255, 255, 255));
        text.setTextSize(Math.max(17, w * 0.045f));
        c.drawText("DuoFold", 20, 34, text);
        text.setTextSize(Math.max(11, w * 0.028f));
        text.setColor(Color.argb(150, 255, 255, 255));
        c.drawText("hinge-driven concept", 20, 54, text);
    }

    private void drawSingleHome(Canvas c, int w, int h, float scale) {
        float centerX = w / 2f;
        float gridTop = h * 0.23f;
        float gap = w * 0.045f;
        float card = Math.min(w * 0.22f, 92f) * scale;
        int cols = 3;
        int rows = 3;

        drawBigTime(c, centerX, h * 0.14f, w * 0.12f);

        for (int i = 0; i < 8; i++) {
            int col = i % cols;
            int row = i / cols;
            float x = centerX + (col - 1) * (card + gap) - card / 2f;
            float y = gridTop + row * (card + gap);
            drawIconCard(c, new RectF(x, y, x + card, y + card), i, 1f);
        }

        drawSwipeHint(c, centerX, h * 0.94f, "unfold to open");
    }

    private void drawDualHome(Canvas c, int w, int h, float t) {
        float seam = w / 2f;
        float reveal = 0.50f + 0.50f * t;
        float leftW = seam * reveal;
        float rightW = seam * reveal;

        drawBigTime(c, seam, h * 0.13f, Math.min(w * 0.08f, 86f));

        // The center seam darkens as the device opens, mimicking the physical hinge.
        float seamGlow = 0.16f + 0.55f * (1f - Math.abs(t - 0.5f) * 2f);
        p.setColor(Color.argb((int)(255 * seamGlow), 0, 0, 0));
        c.drawRect(seam - Math.max(2, w * 0.004f), 0, seam + Math.max(2, w * 0.004f), h, p);

        int count = 8;
        for (int i = 0; i < count; i++) {
            int col = i % 4;
            int row = i / 4;
            float panelGap = w * 0.025f;
            float cardW = Math.min(seam * 0.22f, 115f);
            float y = h * 0.32f + row * (cardW + h * 0.035f);

            float lx = seam / 2f + (col - 1.5f) * (cardW + panelGap) - cardW / 2f;
            float rx = seam + seam / 2f + (col - 1.5f) * (cardW + panelGap) - cardW / 2f;

            // Slight perspective/shear illusion driven by hinge angle.
            float skew = (1f - t) * 22f;
            drawTransformCard(c, new RectF(lx, y, lx + cardW, y + cardW), i, -skew);
            drawTransformCard(c, new RectF(rx, y, rx + cardW, y + cardW), i, skew);
        }

        drawSwipeHint(c, seam, h * 0.93f, t > 0.7f ? "opened" : "keep unfolding");
    }

    private void drawBigTime(Canvas c, float x, float y, float size) {
        text.setTextAlign(Paint.Align.CENTER);
        text.setColor(Color.WHITE);
        text.setTextSize(size);
        String time = "12:34";
        c.drawText(time, x, y, text);
        text.setTextAlign(Paint.Align.LEFT);
    }

    private void drawIconCard(Canvas c, RectF r, int index, float alpha) {
        drawTransformCard(c, r, index, 0f);
    }

    private void drawTransformCard(Canvas c, RectF r, int index, float skew) {
        c.save();
        float cx = r.centerX();
        float cy = r.centerY();
        c.rotate(skew * 0.22f, cx, cy);

        p.setColor(Color.argb(92, 255, 255, 255));
        c.drawRoundRect(r, r.width() * 0.22f, r.width() * 0.22f, p);
        p.setColor(Color.argb(40, 255, 255, 255));
        c.drawRoundRect(new RectF(r.left + 2, r.top + 2, r.right - 2, r.bottom - 2),
                r.width() * 0.22f, r.width() * 0.22f, p);

        text.setTextAlign(Paint.Align.CENTER);
        text.setTextSize(r.width() * 0.31f);
        text.setColor(Color.WHITE);
        String glyph = new String(Character.toChars(iconGlyphs[index % iconGlyphs.length]));
        c.drawText(glyph, cx, cy + r.width() * 0.105f, text);
        text.setTextAlign(Paint.Align.LEFT);
        c.restore();
    }

    private void drawSwipeHint(Canvas c, float x, float y, String s) {
        text.setTextAlign(Paint.Align.CENTER);
        text.setTextSize(12);
        text.setColor(Color.argb(170, 255, 255, 255));
        c.drawText(s, x, y, text);
        text.setTextAlign(Paint.Align.LEFT);
    }

    private void drawAngleDebug(Canvas c, int w, int h) {
        text.setTextSize(10);
        text.setColor(Color.argb(120, 255, 255, 255));
        String sensor = sensorAvailable ? "HINGE SENSOR" : "HINGE SENSOR NOT FOUND";
        c.drawText(sensor + "  " + Math.round(angle) + "°", 14, h - 14, text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downAt = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            long elapsed = System.currentTimeMillis() - downAt;
            float dx = event.getX() - downX;
            float dy = event.getY() - downY;
            if (elapsed > 700 && Math.abs(dx) < 40 && Math.abs(dy) < 40) {
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).openHomeSettings();
                }
            } else if (Math.abs(dx) < 30 && Math.abs(dy) < 30 && elapsed < 500) {
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).showMessage("Long-press to open Home app settings");
                }
            }
            return true;
        }
        return true;
    }
}
