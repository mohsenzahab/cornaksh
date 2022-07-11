package com.example.covidgame.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.covidgame.R;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.FlutterActivityLaunchConfigs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startActivity(new Intent(MainActivity.this,GameBoardActivity.class));
        startActivity(MyFlutterActivity.withCachedEngine("flutter_engine").
                backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent).
                build(this));
        startActivity(FlutterActivity.withNewEngine().
                backgroundMode(FlutterActivityLaunchConfigs.BackgroundMode.transparent).
                build(this));
//        startActivity(new Intent(MainActivity.this,MyFlutterActivity.class));
    }
}

