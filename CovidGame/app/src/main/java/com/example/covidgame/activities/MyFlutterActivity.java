package com.example.covidgame.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.LinkedList;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodChannel;

public class MyFlutterActivity extends FlutterActivity {


    LinkedList<MyFlutterActivity> stack=new LinkedList<>();

    MethodChannel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String id=getIntent().getStringExtra("id");
        if(id!=null && !id.equals("main_menu")){
            stack.add(this);
            Log.i("stack", "stack: added "+hashCode());
            Log.i("stack", "stack: "+ stack.size());
        }

//        channel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
//            @Override
//            public void onMethodCall(MethodCall call, MethodChannel.Result result) {
////                if (call.method.equals("finish")){
//
//                System.out.print("Channel print %%%%%%%%%%%%%%");
////                    finish();
////                }
//            }
//        });

    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public FlutterEngine provideFlutterEngine(@NonNull Context context) {
        FlutterEngineCache engineCache = FlutterEngineCache
                .getInstance();

        FlutterEngine flutterEngine;
        if (!engineCache.contains("flutter_engine")) {
            flutterEngine = new FlutterEngine(this);

//            flutterEngine.getNavigationChannel().setInitialRoute("/login");

            flutterEngine.getDartExecutor().executeDartEntrypoint(
                    DartExecutor.DartEntrypoint.createDefault()
            );

            FlutterEngineCache
                    .getInstance()
                    .put("flutter_engine", flutterEngine);
            engineCache.put("flutter_engine", flutterEngine);
        } else {
            flutterEngine = engineCache.get("flutter_engine");
        }
        return flutterEngine;
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "flutter.channel");
        channel.setMethodCallHandler((call, result) -> {
            Log.i("flutter", "onMethodCall: flutter channel");
            switch (call.method) {
                case "selectLevel":
                    int difficulty = (int) call.argument("difficulty");
                    Log.i("flutter", "difficulty: " + difficulty);
                    Intent intent = new Intent(MyFlutterActivity.this, GameBoardActivity.class);
                    intent.putExtra("difficulty", difficulty);

                    startActivityForResult(intent, difficulty);
                    break;
                case "exit":
                    if (call.arguments != null) {
//                    int difficulty = (int) call.argument("difficulty");
//                    finish();
//                    finishActivity(difficulty);
//                    finish();
                        flutterEngine.getNavigationChannel().pushRoute("/main_menu");
//                    stack.pollLast().finish();
                        GameBoardActivity.instance.finish();
                        Log.i("stack", "stack: " + stack.size());


//                    Intent intent = new Intent(GameBoardActivity.instance, MyFlutterActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
//                    Log.i(TAG, "onCreate: finish");

                    } else {
                        finish();
                    }
                    break;
                case "resume":
                    stack.pollLast().finish();
                    Log.i("stack", "stack: " + stack.size());
                    GameBoardActivity.instance.resume();
                    break;
                case "restart":
                    stack.pollLast().finish();

                    GameBoardActivity.instance.restart();
                    Log.i("stack", "stack: " + Arrays.toString(stack.toArray()));
                    break;
            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("flutter", "onActivityResult: requestCode="+requestCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        channel.invokeMethod("update_menu",null);
    }

    @Override
    protected void onStop() {
        Log.i("flutter", "onStop: &&&&&&&&&&&&&&&&&&&&&");

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Log.i("flutter", "backButton: &&&&&&&&&&&&&&&&&&&&&");

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.i("flutter", "onDestroy: &&&&&&&&&&&&&&&&&&&&&");
        super.onDestroy();

    }
}

