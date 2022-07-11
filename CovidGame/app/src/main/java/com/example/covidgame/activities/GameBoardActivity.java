package com.example.covidgame.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.covidgame.R;
import com.example.covidgame.listeners.OnBoardStateChangeListener;
import com.example.covidgame.views.BoardView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;

enum SoundType {
    LOSS,
    SUCCESS,
    KILL,
    ALERT
}

public class GameBoardActivity extends Activity {
    private String TAG = "gameBoard";
    TextView timerText;
    BoardView game;
    Timer timer;
    GameTimer timerTask;
    int diff;

    MediaPlayer backGroundPlayer;

    public static GameBoardActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        initSounds();
        getWindow().getDecorView().setBackgroundColor(Color.GRAY);
        setContentView(R.layout.activity_game_board);
        Log.i(TAG, "onCreate: ");
//        FlutterEngineCache.getInstance().get("flutter_engine").getPlatformChannel().channel.setMethodCallHandler((call, result) -> {
//            if (call.method.equals("finish")) {
//                Intent intent = new Intent(this, MyFlutterActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                Log.i(TAG, "onCreate: finish");
//            }
//        });

        diff = getIntent().getIntExtra("difficulty", 1);


        LinearLayout linearLayout = findViewById(R.id.linearLayout1);
        TextView scoreText = findViewById(R.id.tv_score);
        TextView antiCovs = findViewById(R.id.tv_anti_covs);
        ((TextView) findViewById(R.id.tv_uname)).
                setText(getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
                        .getString("flutter.username", "username"));
        ((TextView) findViewById(R.id.tv_level)).setText("LEVEL "+String.valueOf(diff));

        ((ImageView) findViewById(R.id.imv_person)).setImageResource(R.drawable.person);
        timerText = findViewById(R.id.tv_time);
        game = new BoardView(this, diff);
        timer = new Timer();
        game.setOnBoardChangeListener(new OnBoardStateChangeListener() {
            @Override
            public void onScoreChange(int newScore) {
                if (newScore < 0) {
                    playMusic(SoundType.LOSS);
                    game.pauseGame();
                    timer.cancel();
                    new AlertDialog.Builder(GameBoardActivity.this)
                            .setTitle("You Lost!")
                            .setMessage("Do you wana try again?")

                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                game.resetState();
                                resetTimer();
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    scoreText.setText("Score: " + 0);
                } else {
                    scoreText.setText("Score: " + newScore);
                }

            }

            @Override
            public void onAntiCovCountChange(int newCount) {
                playMusic(SoundType.ALERT);
                antiCovs.setText(String.valueOf(newCount));

            }
        });
        linearLayout.addView(game);
        timerTask = new GameTimer(GameBoardActivity.this, 150 - diff * 30);


        timer.schedule(timerTask, 0, 1000);

//        startActivity(new Intent(GameBoardActivity.this,MainActivity.class));

    }

    public void playMusic(SoundType soundType) {
        switch (soundType) {
            case LOSS:
                backGroundPlayer.stop();
                MediaPlayer.create(GameBoardActivity.this, R.raw.gamelost).start();
                break;
            case SUCCESS:
                backGroundPlayer.stop();
                MediaPlayer.create(GameBoardActivity.this, R.raw.success).start();
                break;
            case ALERT:
                MediaPlayer.create(GameBoardActivity.this, R.raw.alert).start();
                break;
        }
    }

    private void initSounds() {
        backGroundPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
        backGroundPlayer.setLooping(true);
        backGroundPlayer.start();
    }

    private void resetTimer() {
        timer = new Timer();
        timerTask = new GameTimer(GameBoardActivity.this, 150 - 30 * diff);
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void onBackPressed() {
        if (game.isGameRunning()) {
            game.pauseGame();
            pauseTimer();

            FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get("flutter_engine");
            flutterEngine.getNavigationChannel().pushRoute("/menu");
            Intent intent = new Intent(this, MyFlutterActivity.class);
            intent.putExtra("id", "menu");

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

//            intent.setAction(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra("background_mode", "transparent");
//            intent.putExtra("difficulty",diff);

//            ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    new ActivityResultCallback<ActivityResult>() {
//                        @Override
//                        public void onActivityResult(ActivityResult result) {
//                            if (result.getResultCode() == Activity.RESULT_OK) {
//                                // There are no request codes
//                                Intent data = result.getData();
//
//                            }
//                        }
//                    });
//            someActivityResultLauncher.launch(intent);
            startActivity(intent);

        } else {
            resume();
//            MyFlutterActivity.main.onResume();

//            super.onBackPressed();
        }
    }

    private void pauseTimer() {

        timerTask.cancel();
        timer.cancel();
    }

    private void resumeTimer() {
        timerTask = new GameTimer(GameBoardActivity.this, timerTask.periodSeconds);
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish: ************************************  " + diff);

        game.pauseGame();
        game.destroyDrawingCache();
        pauseTimer();
        backGroundPlayer.setLooping(false);
        backGroundPlayer.stop();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        game.pauseGame();
        game.destroyDrawingCache();
        pauseTimer();
        backGroundPlayer.setLooping(false);
        backGroundPlayer.stop();
        super.onDestroy();
    }

    public void resume() {
        game.resumeGame();
        resumeTimer();
    }

    public void restart() {
        resetTimer();
        game.resetState();
    }
}

class GameTimer extends TimerTask {

    int periodSeconds = 120;
    GameBoardActivity activity;

    GameTimer(GameBoardActivity activity) {
        this.activity = activity;
    }

    GameTimer(GameBoardActivity activity, int newSeconds) {
        this.activity = activity;
        periodSeconds = newSeconds;
    }

    @Override
    public void run() {
        activity.runOnUiThread(() -> {
            int seconds = periodSeconds;
            periodSeconds -= 1;
            // if player won
            if (seconds == 0) {
                activity.playMusic(SoundType.SUCCESS);
                activity.game.pauseGame();
                cancel();
//                HashMap<String, Object> arguments = new HashMap<>();
//                arguments.put("score", activity.view.getScore());
//                arguments.put("difficulty",activity.diff);
//                FlutterEngineCache.getInstance().get("flutter_engine").
//                        getPlatformChannel().channel.
//                        invokeMethod("newRecord",arguments);
                SharedPreferences.Editor preferences = activity.
                        getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE).edit();
                preferences.putInt("flutter.difficulty" + activity.diff, activity.game.getScore());
                preferences.apply();
                new AlertDialog.Builder(activity)
                        .setTitle("You Won!\n" +
                                "your score is " + activity.game.getScore())
                        .setMessage("Do you wana try again?")

                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            activity.game.resetState();
                            activity.timer.schedule(new GameTimer(activity), 0, 1000);
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                GameBoardActivity.instance.finish();
                                Intent intent = new Intent(GameBoardActivity.instance, MyFlutterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                GameBoardActivity.instance.startActivity(intent);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            int minutes = seconds / 60;
            seconds = seconds % 60;

            activity.timerText.setText(String.format("Time: %d:%02d", minutes, seconds));
        });

    }
}