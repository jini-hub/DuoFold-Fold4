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
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private DuoView duoView;
    private SensorManager sensorManager;
    private Sensor hingeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        duoView = new DuoView(this);
        duoView.setBackgroundColor(Color.BLACK);
        setContentView(duoView);

        sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            hingeSensor = sensorManager.getDefaultSensor(
                    Sensor.TYPE_HINGE_ANGLE
            );
        }

        if (hingeSensor != null) {
            duoView.setSensorStatus(true);
        } else {
            duoView.setSensorStatus(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensorManager != null && hingeSensor != null) {
            sensorManager.registerListener(
                    this,
                    hingeSensor,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_HINGE_ANGLE) {

            float angle = event.values[0];

            if (duoView != null) {
                duoView.setHingeAngle(angle);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 특별히 처리하지 않음
    }

    public void openHomeSettings() {
        try {
            startActivity(
                    new Intent(Settings.ACTION_HOME_SETTINGS)
            );
        } catch (Exception e) {
            startActivity(
                    new Intent(Settings.ACTION_SETTINGS)
            );
        }
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}
