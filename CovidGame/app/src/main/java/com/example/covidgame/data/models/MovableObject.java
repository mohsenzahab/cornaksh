package com.example.covidgame.data.models;

import android.util.Log;

import com.example.covidgame.views.BoardView;
import com.example.covidgame.data.enums.Direction;

import java.util.HashMap;

public abstract class MovableObject extends BObject {
    public Coordinate destination;

    abstract float getSpeed();

    public void move(BoardView v) {
        if (!onNewMove(v)) {
            return;
        }
        boolean setReached = currentLocation.canReach(destination.location);

        float nextStep;
        if (setReached) {
            nextStep = afterDestRemainingSteps();
            v.map.remove(position);
            currentLocation = destination.location;
            position = destination.position;
            boolean terminated = onDestinationReached(v, position);
            if (!terminated) {
                v.map.put(position, this);
            } else {
                return;
            }

        } else {
            nextStep = getSpeed();
        }


        switch (desDir()) {

            case up:
                currentLocation.y -= nextStep;
                break;
            case down:
                currentLocation.y += nextStep;
                break;
            case left:
                currentLocation.x -= nextStep;
                break;
            case right:
                currentLocation.x += nextStep;
                break;
        }
//        Log.i("boardView", "move: " + desDir() + "  " + currentLocation);

    }

    protected abstract float afterDestRemainingSteps();

    protected abstract boolean onDestinationReached(BoardView v, int position);

    protected abstract boolean onNewMove(BoardView v);

    Direction desDir() {
        return destination.direction;
    }

    protected MoveResult canMove(BoardView v, Direction newDirection) {

        MoveResult result = new MoveResult();
        switch (newDirection) {
            case up:
                result.newPosition = position - v.columns;
                result.canMove = result.newPosition >= 0 && canTakePosition(result.newPosition, v.map);
                break;
            case down:
                result.newPosition = position + v.columns;
                result.canMove = result.newPosition < v.columns * v.rows && canTakePosition(result.newPosition, v.map);
                break;
            case left:
                result.newPosition = position - 1;
                result.canMove = (position % v.columns) - 1 >= 0 && canTakePosition(result.newPosition, v.map);
                break;
            case right:
                result.newPosition = position + 1;
                result.canMove = (position % v.columns) + 1 < v.columns && canTakePosition(result.newPosition, v.map);
                break;
            default:
                result.newPosition = -1;
                result.canMove = false;

        }
        return result;
    }

    protected abstract boolean canTakePosition(int position, HashMap<Integer, BObject> map);
}

class MoveResult {

    int newPosition;
    boolean canMove;
}