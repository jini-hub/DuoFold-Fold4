package com.example.duofold;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;

public class DuoService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "DuoFoldService";

    private SensorManager sensorManager;
    private Sensor hingeSensor;

    private WindowManager windowManager;
    private DuoOverlayView overlayView;

    private float lastSensorAngle = 180f;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("DuoFold")
                        .setContentText("폴더블 상태 감시 중")
                        .setSmallIcon(android.R.drawable.ic_menu_view)
                        .setOngoing(true)
                        .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            startForeground(
                    1,
                    notification,
                    android.content.pm.ServiceInfo
                            .FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            );

        } else {

            startForeground(
                    1,
                    notification
            );
        }

        setupSensor();

        setupOverlay();
    }

    private void setupSensor() {

        sensorManager =
                (SensorManager) getSystemService(
                        Context.SENSOR_SERVICE
                );

        if (sensorManager != null) {

            hingeSensor =
                    sensorManager.getDefaultSensor(
                            Sensor.TYPE_HINGE_ANGLE
                    );

            if (hingeSensor != null) {

                sensorManager.registerListener(
                        this,
                        hingeSensor,
                        SensorManager.SENSOR_DELAY_GAME
                );
            }
        }
    }

    private void setupOverlay() {

        if (!Settings.canDrawOverlays(this)) {
            return;
        }

        windowManager =
                (WindowManager) getSystemService(
                        WINDOW_SERVICE
                );

        overlayView =
                new DuoOverlayView(this);

        int windowType;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            windowType =
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {

            windowType =
                    WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        windowType,

                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,

                        PixelFormat.TRANSLUCENT
                );

        params.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(
                overlayView,
                params
        );
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType()
                != Sensor.TYPE_HINGE_ANGLE) {
            return;
        }

        if (event.values.length == 0) {
            return;
        }

        float angle = event.values[0];

        if (angle > 150f) {

            if (lastSensorAngle <= 150f) {

                if (overlayView != null) {
                    overlayView.startOpenAnimation();
                }
            }

            lastSensorAngle = 180f;

        } else if (angle < 120f) {

            if (lastSensorAngle >= 150f) {

                if (overlayView != null) {
                    overlayView.startCloseAnimation();
                }
            }

            lastSensorAngle = 90f;
        }
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "DuoFold",
                            NotificationManager.IMPORTANCE_LOW
                    );

            channel.setDescription(
                    "DuoFold 폴더블 상태 감시"
            );

            NotificationManager manager =
                    (NotificationManager)
                            getSystemService(
                                    NOTIFICATION_SERVICE
                            );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        if (windowManager != null
                && overlayView != null) {

            try {
                windowManager.removeView(
                        overlayView
                );
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
