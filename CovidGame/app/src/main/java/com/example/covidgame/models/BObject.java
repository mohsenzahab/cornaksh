package com.example.covidgame.models;

import java.util.Objects;

public abstract class BObject {

    public Location currentLocation;
    public int position;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BObject object = (BObject) o;
        return position == object.position &&
                Objects.equals(currentLocation, object.currentLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentLocation, position);
    }
}
