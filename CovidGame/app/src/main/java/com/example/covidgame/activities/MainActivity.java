package com.example.covidgame.activities;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import io.flutter.embedding.android.FlutterActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onStart() {
        super.onStart();
//        startActivity(new Intent(MainActivity.this,GameBoardActivity.class));
        startActivity(FlutterActivity.withCachedEngine("flutter_engine").build(this));
    }
}

