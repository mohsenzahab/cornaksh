package com.example.covidgame.data.models;

public class Wall extends BObject {
    public Wall(int position, Location location) {
        this.position = position;
        this.currentLocation = location;
    }
}
