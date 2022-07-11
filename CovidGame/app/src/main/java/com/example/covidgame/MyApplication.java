package com.example.covidgame;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.covidgame.activities.MyFlutterActivity;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MyApplication extends Application {
    public FlutterEngine flutterEngine;
//    public static MethodChannel channel;

    @Override
    public void onCreate() {
        super.onCreate();
        // Instantiate a FlutterEngine.
//        flutterEngine = new FlutterEngine(this);
//
//        flutterEngine.getNavigationChannel().setInitialRoute("/login");
//
//        // Start executing Dart code to pre-warm the FlutterEngine.
//        flutterEngine.getDartExecutor().executeDartEntrypoint(
//                DartExecutor.DartEntrypoint.createDefault()
//        );
//
//        // Cache the FlutterEngine to be used by FlutterActivity.
//        FlutterEngineCache
//                .getInstance()
//                .put("flutter_engine", flutterEngine);



//        channel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), "flutter.channel");
//        channel.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
//            @Override
//            public void onMethodCall( MethodCall call, MethodChannel.Result result) {
//                Log.i("flutter", "onMethodCall: flutter channel");
//
//
//            }
//        });

    }
}
