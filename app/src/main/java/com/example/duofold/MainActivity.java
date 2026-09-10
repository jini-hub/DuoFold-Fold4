package com.example.duofold;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 일단 가장 단순하고 안전한 화면부터 표시
        View view = new DuoView(this);
        view.setBackgroundColor(Color.BLACK);

        setContentView(view);
    }
}
