package com.example.duofold;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.PathInterpolator;

public class DuoOverlayView extends View {

    private final Paint paint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Camera camera =
            new Camera();

    private final Matrix matrix =
            new Matrix();

    /*
     * 0 = 완전히 펼쳐진 상태
     * 1 = 완전히 접힌 상태
     */
    private float progress = 0f;

    private ValueAnimator animator;

    public DuoOverlayView(Context context) {
        super(context);

        setBackgroundColor(
                Color.TRANSPARENT
        );

        paint.setAntiAlias(true);

        setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
        );
    }

    /*
     * 180 → 90
     *
     * 접히기
     */
    public void startCloseAnimation() {

        animateTo(1f);
    }

    /*
     * 90 → 180
     *
     * 펼치기
     */
    public void startOpenAnimation() {

        animateTo(0f);
    }

    private void animateTo(
            float target
    ) {

        if (animator != null) {
            animator.cancel();
        }

        animator =
                ValueAnimator.ofFloat(
                        progress,
                        target
                );

        /*
         * 실제 기기에서
         * 너무 느리거나 빠르지 않도록
         * 우선 650ms.
         */
        animator.setDuration(650);

        animator.setInterpolator(
                new PathInterpolator(
                        0.16f,
                        1.0f,
                        0.3f,
                        1.0f
                )
        );

        animator.addUpdateListener(
                animation -> {

                    progress =
                            (float)
                                    animation
                                            .getAnimatedValue();

                    invalidate();
                }
        );

        animator.start();
    }

    @Override
    protected void onDraw(
            Canvas canvas
    ) {

        super.onDraw(canvas);

        /*
         * 완전히 펼쳐져 있을 때는
         * 아무것도 그리지 않는다.
         *
         * 즉 평소에는
         * 현재 실행 중인 앱 화면 그대로 보인다.
         */
        if (progress <= 0.001f) {
            return;
        }

        float screenWidth =
                getWidth();

        float screenHeight =
                getHeight();

        float centerX =
                screenWidth / 2f;

        /*
         * 화면 위에 표시할
         * Duo 형태의 영역
         */
        float deviceWidth =
                Math.min(
                        screenWidth * 0.92f,
                        1000f
                );

        float deviceHeight =
                Math.min(
                        screenHeight * 0.82f,
                        1600f
                );

        float left =
                centerX
                        - deviceWidth / 2f;

        float top =
                screenHeight / 2f
                        - deviceHeight / 2f;

        float halfWidth =
                deviceWidth / 2f;

        /*
         * 펼침 → 접힘
         *
         * 0 → 90도
         */
        float rotation =
                90f * progress;

        /*
         * --------------------
         * 왼쪽 화면
         * --------------------
         *
         * 오른쪽 모서리가
         * 중앙 힌지에 붙어있는 상태.
         */
        canvas.save();

        camera.save();

        camera.rotateY(
                rotation
        );

        camera.getMatrix(
                matrix
        );

        camera.restore();

        /*
         * 왼쪽 패널의 중심을
         * 중앙 힌지 기준으로 이동
         */
        matrix.preTranslate(
                -centerX,
                -screenHeight / 2f
        );

        matrix.postTranslate(
                centerX,
                screenHeight / 2f
        );

        canvas.concat(
                matrix
        );

        drawLeftPanel(
                canvas,
                centerX,
                top,
                halfWidth,
                deviceHeight
        );

        canvas.restore();

        /*
         * --------------------
         * 오른쪽 화면
         * --------------------
         */
        canvas.save();

        camera.save();

        camera.rotateY(
                -rotation
        );

        camera.getMatrix(
                matrix
        );

        camera.restore();

        matrix.preTranslate(
                -centerX,
                -screenHeight / 2f
        );

        matrix.postTranslate(
                centerX,
                screenHeight / 2f
        );

        canvas.concat(
                matrix
        );

        drawRightPanel(
                canvas,
                centerX,
                top,
                halfWidth,
                deviceHeight
        );

        canvas.restore();

        /*
         * 중앙 힌지
         */
        drawHinge(
                canvas,
                centerX,
                top,
                deviceHeight
        );
    }

    private void drawLeftPanel(
            Canvas canvas,
            float hingeX,
            float top,
            float width,
            float height
    ) {

        float left =
                hingeX - width;

        /*
         * 화면 외곽
         */
        paint.setColor(
                Color.argb(
                        180,
                        245,
                        245,
                        245
                )
        );

        canvas.drawRoundRect(
                left,
                top,
                hingeX,
                top + height,
                28f,
                28f,
                paint
        );

        /*
         * 내부 화면
         */
        paint.setColor(
                Color.argb(
                        75,
                        255,
                        255,
                        255
                )
        );

        canvas.drawRoundRect(
                left + width * 0.06f,
                top + height * 0.04f,
                hingeX - width * 0.04f,
                top + height * 0.96f,
                20f,
                20f,
                paint
        );

        /*
         * 접힐수록 어두워지는 음영
         */
        int shadowAlpha =
                (int)(
                        150f * progress
                );

        if (shadowAlpha > 0) {

            paint.setColor(
                    Color.argb(
                            shadowAlpha,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRect(
                    hingeX - width * 0.22f,
                    top,
                    hingeX,
                    top + height,
                    paint
            );
        }
    }

    private void drawRightPanel(
            Canvas canvas,
            float hingeX,
            float top,
            float width,
            float height
    ) {

        float right =
                hingeX + width;

        paint.setColor(
                Color.argb(
                        180,
                        245,
                        245,
                        245
                )
        );

        canvas.drawRoundRect(
                hingeX,
                top,
                right,
                top + height,
                28f,
                28f,
                paint
        );

        paint.setColor(
                Color.argb(
                        75,
                        255,
                        255,
                        255
                )
        );

        canvas.drawRoundRect(
                hingeX + width * 0.04f,
                top + height * 0.04f,
                right - width * 0.06f,
                top + height * 0.96f,
                20f,
                20f,
                paint
        );

        int shadowAlpha =
                (int)(
                        150f * progress
                );

        if (shadowAlpha > 0) {

            paint.setColor(
                    Color.argb(
                            shadowAlpha,
                            0,
                            0,
                            0
                    )
            );

            canvas.drawRect(
                    hingeX,
                    top,
                    hingeX + width * 0.22f,
                    top + height,
                    paint
            );
        }
    }

    private void drawHinge(
            Canvas canvas,
            float centerX,
            float top,
            float height
    ) {

        /*
         * 접힐수록 힌지가 조금 더 강조된다.
         */
        float hingeWidth =
                2f + progress * 7f;

        paint.setColor(
                Color.argb(
                        (int)(
                                120f * progress
                        ),
                        255,
                        255,
                        255
                )
        );

        canvas.drawRoundRect(
                centerX - hingeWidth,
                top,
                centerX + hingeWidth,
                top + height,
                hingeWidth,
                hingeWidth,
                paint
        );

        /*
         * 아주 미세한 중앙 그림자
         */
        paint.setColor(
                Color.argb(
                        (int)(
                                100f * progress
                        ),
                        0,
                        0,
                        0
                )
        );

        canvas.drawRect(
                centerX - 1f,
                top,
                centerX + 1f,
                top + height,
                paint
        );
    }
}
