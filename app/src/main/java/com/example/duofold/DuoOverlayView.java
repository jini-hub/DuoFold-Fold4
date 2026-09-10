package com.example.duofold;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class DuoOverlayView extends View {

    private final Paint paint = new Paint(
            Paint.ANTI_ALIAS_FLAG
    );

    private float animationProgress = 0f;

    private ValueAnimator animator;

    public DuoOverlayView(Context context) {
        super(context);

        setBackgroundColor(Color.TRANSPARENT);

        paint.setStyle(Paint.Style.FILL);
    }

    public void startCloseAnimation() {

        startAnimation(
                0f,
                1f
        );
    }

    public void startOpenAnimation() {

        startAnimation(
                1f,
                0f
        );
    }

    private void startAnimation(
            float from,
            float to
    ) {

        if (animator != null) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(
                from,
                to
        );

        animator.setDuration(550);

        animator.setInterpolator(
                new DecelerateInterpolator()
        );

        animator.addUpdateListener(
                animation -> {

                    animationProgress =
                            (float) animation.getAnimatedValue();

                    invalidate();
                }
        );

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (animationProgress <= 0.001f) {
            return;
        }

        float width = getWidth();
        float height = getHeight();

        /*
         * 실제 화면은 완전히 투명.
         * 아래의 그래픽만 다른 앱 위에 나타납니다.
         */

        float centerX = width / 2f;
        float centerY = height / 2f;

        float maxWidth =
                Math.min(width * 0.42f, 430f);

        float maxHeight =
                Math.min(height * 0.72f, 760f);

        float panelWidth =
                maxWidth * (1f - animationProgress * 0.55f);

        float panelHeight =
                maxHeight * (1f - animationProgress * 0.08f);

        float gap =
                35f + animationProgress * 70f;

        float left =
                centerX - gap / 2f - panelWidth;

        float right =
                centerX + gap / 2f;

        float top =
                centerY - panelHeight / 2f;

        /*
         * 왼쪽 화면
         */

        canvas.save();

        canvas.rotate(
                -18f * animationProgress,
                left + panelWidth,
                centerY
        );

        drawPanel(
                canvas,
                left,
                top,
                panelWidth,
                panelHeight
        );

        canvas.restore();

        /*
         * 오른쪽 화면
         */

        canvas.save();

        canvas.rotate(
                18f * animationProgress,
                right,
                centerY
        );

        drawPanel(
                canvas,
                right,
                top,
                panelWidth,
                panelHeight
        );

        canvas.restore();

        /*
         * 중앙 힌지
         */

        paint.setColor(
                Color.argb(
                        (int)(170 * animationProgress),
                        255,
                        255,
                        255
                )
        );

        float hingeWidth =
                5f + animationProgress * 8f;

        canvas.drawRoundRect(
                centerX - hingeWidth,
                top + 30,
                centerX + hingeWidth,
                top + panelHeight - 30,
                hingeWidth,
                hingeWidth,
                paint
        );
    }

    private void drawPanel(
            Canvas canvas,
            float left,
            float top,
            float width,
            float height
    ) {

        paint.setColor(
                Color.argb(
                        (int)(90 * animationProgress),
                        255,
                        255,
                        255
                )
        );

        paint.setStyle(
                Paint.Style.STROKE
        );

        paint.setStrokeWidth(2f);

        canvas.drawRoundRect(
                left,
                top,
                left + width,
                top + height,
                28f,
                28f,
                paint
        );

        /*
         * 내부 빛
         */

        paint.setStrokeWidth(1f);

        paint.setColor(
                Color.argb(
                        (int)(45 * animationProgress),
                        255,
                        255,
                        255
                )
        );

        float innerLeft =
                left + width * 0.12f;

        float innerRight =
                left + width * 0.88f;

        float innerTop =
                top + height * 0.18f;

        canvas.drawLine(
                innerLeft,
                innerTop,
                innerRight,
                innerTop,
                paint
        );

        paint.setStyle(
                Paint.Style.FILL
        );
    }
}
