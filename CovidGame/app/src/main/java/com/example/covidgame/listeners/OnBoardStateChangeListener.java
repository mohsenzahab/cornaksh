package com.example.covidgame.listeners;

public interface OnBoardStateChangeListener {

    void onScoreChange(int newScore);

    void onAntiCovCountChange(int newCount);
}
