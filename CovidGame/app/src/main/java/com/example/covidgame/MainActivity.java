package com.example.covidgame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covidgame.listeners.OnBoardStateChangeListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView timerText;
    BoardView view;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FrameLayout.LayoutParams


        LinearLayout linearLayout = findViewById(R.id.linearLayout1);
        TextView scoreText = (TextView) findViewById(R.id.tv_score);
        TextView antiCovs = (TextView) findViewById(R.id.tv_anti_covs);
        timerText = (TextView) findViewById(R.id.tv_time);
        view = new BoardView(this);
        timer = new Timer();
        view.setOnBoardChangeListener(new OnBoardStateChangeListener() {
            @Override
            public void onScoreChange(int newScore) {
                if (newScore < 0) {
                    view.pauseGame();
                    timer.cancel();
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("You Lost!")
                            .setMessage("Do you wana try again?")

                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                view.resetState();
                                timer = new Timer();
                                timer.schedule(new GameTimer(MainActivity.this), 0, 1000);
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
                antiCovs.setText(String.valueOf(newCount));

            }
        });
        linearLayout.addView(view);

        timer.schedule(new GameTimer(this), 0, 1000);


    }


}

class GameTimer extends TimerTask {

    int periodSeconds = 120;
    MainActivity activity;

    GameTimer(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        activity.runOnUiThread(() -> {
            int seconds = periodSeconds;
            periodSeconds -= 1;
            if (seconds == 0) {
                activity.view.pauseGame();
                cancel();
                new AlertDialog.Builder(activity)
                        .setTitle("You Won!")
                        .setMessage("Do you wana try again?")

                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            activity.view.resetState();
                            activity.timer.schedule(new GameTimer(activity), 0, 1000);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            int minutes = seconds / 60;
            seconds = seconds % 60;

            activity.timerText.setText(String.format("Time: %d:%02d", minutes, seconds));
        });

    }
}