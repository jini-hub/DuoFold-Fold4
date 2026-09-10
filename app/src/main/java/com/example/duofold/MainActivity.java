package com.example.duofold;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
    private DuoView duoView;
    private SensorManager sensorManager;
    private Sensor hingeSensor;
    private float lastAngle = 0f;
    private boolean sensorAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setNavigationBarColor(0xFF000000);
        window.setStatusBarColor(0xFF000000);
        hideBars();

        duoView = new DuoView(this);
        setContentView(duoView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            hingeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);
            sensorAvailable = hingeSensor != null;
            if (sensorAvailable) {
                sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }

        duoView.setHingeAngle(lastAngle);
        duoView.setSensorAvailable(sensorAvailable);
    }

    private void hideBars() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HINGE_ANGLE && event.values.length > 0) {
            // Android defines TYPE_HINGE_ANGLE in degrees. Fold devices normally report 0..180.
            lastAngle = clamp(event.values[0], 0f, 180f);
            duoView.setHingeAngle(lastAngle);
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume() {
        super.onResume();
        hideBars();
        if (sensorManager != null && hingeSensor != null) {
            sensorManager.registerListener(this, hingeSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        super.onPause();
    }

    public void openHomeSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
        } catch (Exception e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
