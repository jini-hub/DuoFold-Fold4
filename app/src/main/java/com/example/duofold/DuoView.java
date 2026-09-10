package com.example.duofold;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class DuoView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float hingeAngle = 180f;

    public DuoView(Context context) {
        super(context);

        paint.setTypeface(
                Typeface.create(
                        "sans",
                        Typeface.NORMAL
                )
        );

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setHingeAngle(float angle) {

        if (angle < 0f) {
            angle = 0f;
        }

        if (angle > 180f) {
            angle = 180f;
        }

        hingeAngle = angle;

        // 화면을 다시 그리도록 요청
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.drawColor(
                Color.rgb(10, 10, 14)
        );

        float centerX = width / 2f;
        float centerY = height / 2f;

        // -------------------------
        // 제목
        // -------------------------

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(
                Math.max(36f, width * 0.075f)
        );

        canvas.drawText(
                "DuoFold",
                centerX,
                centerY - 130f,
                paint
        );

        // -------------------------
        // 힌지 각도
        // -------------------------

        paint.setTextSize(
                Math.max(28f, width * 0.06f)
        );

        canvas.drawText(
                String.format("%.1f°", hingeAngle),
                centerX,
                centerY - 70f,
                paint
        );

        // -------------------------
        // 상태
        // -------------------------

        paint.setTextSize(
                Math.max(16f, width * 0.032f)
        );

        String state;

        if (hingeAngle >= 165f) {
            state = "OPEN";
        } else if (hingeAngle <= 30f) {
            state = "CLOSED";
        } else {
            state = "FLEX";
        }

        paint.setColor(Color.LTGRAY);

        canvas.drawText(
                state,
                centerX,
                centerY - 35f,
                paint
        );

        // -------------------------
        // 힌지 애니메이션
        // -------------------------

        float radius = Math.min(
                width,
                height
        ) * 0.20f;

        // 0~180도를 0~180도로 변환
        float rotation = hingeAngle - 90f;

        canvas.save();

        canvas.rotate(
                rotation,
                centerX,
                centerY + 80f
        );

        // 왼쪽 패널
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setColor(Color.WHITE);

        float panelWidth = radius * 1.25f;
        float panelHeight = radius * 0.9f;

        canvas.drawRoundRect(
                centerX - panelWidth,
                centerY + 30f,
                centerX,
                centerY + 30f + panelHeight,
                25f,
                25f,
                paint
        );

        // 오른쪽 패널
        canvas.drawRoundRect(
                centerX,
                centerY + 30f,
                centerX + panelWidth,
                centerY + 30f + panelHeight,
                25f,
                25f,
                paint
        );

        // 중앙 힌지
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(
                centerX,
                centerY + 30f + panelHeight / 2f,
                14f,
                paint
        );

        canvas.restore();

        // -------------------------
        // 센서 안내
        // -------------------------

        paint.setColor(Color.GRAY);
        paint.setTextSize(
                Math.max(14f, width * 0.028f)
        );

        canvas.drawText(
                "Hinge sensor active",
                centerX,
                height - 60f,
                paint
        );
    }
}
