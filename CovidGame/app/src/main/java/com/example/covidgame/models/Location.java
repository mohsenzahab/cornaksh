package com.example.covidgame.models;

public class Location {

    public static final int MOVE_UNIT = 5;
    public float x, y;

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean canReach(Location dest) {
        return Math.abs(dest.x - x) <= MOVE_UNIT && Math.abs(dest.y - y) <= MOVE_UNIT;
    }

    boolean equals(Location o) {
        return x == o.x && y == o.y;
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
