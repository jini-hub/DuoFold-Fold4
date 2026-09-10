package com.example.duofold;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int OVERLAY_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Settings.canDrawOverlays(this)) {

            Toast.makeText(
                    this,
                    "DuoFold를 사용하려면 '다른 앱 위에 표시' 권한이 필요합니다.",
                    Toast.LENGTH_LONG
            ).show();

            try {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );

                startActivityForResult(intent, OVERLAY_REQUEST);

            } catch (Exception e) {
                startActivity(
                        new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                );
            }

            return;
        }

        startDuoService();

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Settings.canDrawOverlays(this)) {
            startDuoService();
        }
    }

    private void startDuoService() {

        Intent intent = new Intent(this, DuoService.class);

        try {
            startForegroundService(intent);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "DuoFold 서비스를 시작할 수 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
