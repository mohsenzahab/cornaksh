package com.example.covidgame.models;

import com.example.covidgame.enums.Direction;

public class Coordinate {

    public Direction direction;
    public Location location;
    public int position;
    public Coordinate(Direction direction, Location location, int position) {
        this.location = location;
        this.direction = direction;
        this.position = position;
    }
}
