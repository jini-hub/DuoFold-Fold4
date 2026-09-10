package com.example.duofold;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Locale;

public class DuoView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float hingeAngle = -1f;
    private boolean sensorAvailable = false;
    private long eventCount = 0;

    public DuoView(MainActivity context) {
        super(context);

        paint.setTypeface(android.graphics.Typeface.create(
                "sans",
                android.graphics.Typeface.NORMAL
        ));
    }

    public void setSensorStatus(boolean available) {
        sensorAvailable = available;
        invalidate();
    }

    public void setHingeAngle(float angle) {

        hingeAngle = angle;
        eventCount++;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);

        float width = getWidth();
        float height = getHeight();

        // 제목
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(42);

        canvas.drawText(
                "DuoFold",
                width / 2,
                90,
                paint
        );

        // HINGE SENSOR
        paint.setTextSize(22);
        paint.setColor(Color.LTGRAY);

        canvas.drawText(
                "HINGE SENSOR",
                width / 2,
                160,
                paint
        );

        // ANGLE
        paint.setTextSize(20);
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "RAW ANGLE",
                width / 2,
                220,
                paint
        );

        // 실제 센서값
        paint.setTextSize(64);
        paint.setColor(Color.WHITE);

        String angleText;

        if (hingeAngle < 0) {
            angleText = "---";
        } else {
            angleText = String.format(
                    Locale.US,
                    "%.2f°",
                    hingeAngle
            );
        }

        canvas.drawText(
                angleText,
                width / 2,
                300,
                paint
        );

        // EVENT COUNT
        paint.setTextSize(20);
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "EVENT COUNT",
                width / 2,
                370,
                paint
        );

        paint.setTextSize(32);
        paint.setColor(Color.WHITE);

        canvas.drawText(
                String.valueOf(eventCount),
                width / 2,
                415,
                paint
        );

        // SENSOR STATUS
        paint.setTextSize(20);
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "SENSOR",
                width / 2,
                485,
                paint
        );

        paint.setTextSize(30);

        if (sensorAvailable) {
            paint.setColor(Color.WHITE);

            canvas.drawText(
                    "ACTIVE",
                    width / 2,
                    530,
                    paint
            );
        } else {
            paint.setColor(Color.RED);

            canvas.drawText(
                    "NOT FOUND",
                    width / 2,
                    530,
                    paint
            );
        }

        // 현재 상태
        paint.setTextSize(20);
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "STATE",
                width / 2,
                600,
                paint
        );

        paint.setTextSize(30);
        paint.setColor(Color.WHITE);

        String state;

        if (hingeAngle < 0) {
            state = "WAITING";
        } else if (hingeAngle >= 160) {
            state = "OPEN";
        } else if (hingeAngle <= 20) {
            state = "CLOSED";
        } else {
            state = "FLEX";
        }

        canvas.drawText(
                state,
                width / 2,
                645,
                paint
        );

        // 안내
        paint.setTextSize(18);
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "Fold / unfold the device slowly",
                width / 2,
                height - 70,
                paint
        );

        canvas.drawText(
                "Watch the RAW ANGLE value",
                width / 2,
                height - 40,
                paint
        );
    }
}
