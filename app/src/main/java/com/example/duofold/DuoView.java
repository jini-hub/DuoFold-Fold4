package com.example.duofold;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DuoView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DuoView(Context context) {
        super(context);
        paint.setTypeface(
                android.graphics.Typeface.create(
                        "sans",
                        android.graphics.Typeface.NORMAL
                )
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        canvas.drawColor(Color.rgb(12, 12, 16));

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(Math.max(40, w * 0.08f));

        canvas.drawText(
                "DuoFold",
                w / 2f,
                h / 2f,
                paint
        );

        paint.setTextSize(Math.max(16, w * 0.035f));
        paint.setColor(Color.GRAY);

        canvas.drawText(
                "Fold 4",
                w / 2f,
                h / 2f + 40,
                paint
        );
    }
}
