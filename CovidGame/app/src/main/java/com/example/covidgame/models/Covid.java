package com.example.covidgame.models;

import com.example.covidgame.BoardView;
import com.example.covidgame.enums.CovidType;
import com.example.covidgame.enums.Direction;

import java.util.HashMap;

public class Covid extends MovableObject {

    public CovidType covidType;

    public Covid(int position, Location location) {
        this.position = position;
        this.currentLocation = location;
        int i = (int) (Math.floor(Math.random() * 2));
        if (i == 0) {
            covidType = CovidType.red;
        } else if (i == 1) {
            covidType = CovidType.green;
        }
    }

    @Override
    float getSpeed() {
        return Location.MOVE_UNIT;
    }

    @Override
    protected float afterDestRemainingSteps() {
        float nextStep;
        if (destination.direction == Direction.up || destination.direction == Direction.down) {
            nextStep = getSpeed() - Math.abs(currentLocation.y - destination.location.y);
        } else {
            nextStep = getSpeed() - Math.abs(currentLocation.x - destination.location.x);
        }
        return nextStep;
    }

    public int getKillScore() {
        if (covidType == CovidType.red) {
            return 12;
        } else {
            return 10;
        }
    }

    public int getAntiCovCost() {
        if (covidType == CovidType.red) {
            return 4;
        } else {
            return 3;
        }
    }


    @Override
    protected boolean onNewMove(BoardView v) {
        if (destination == null) {
            destination = selectNewDestination(v);
            return true;
        }
        return true;
    }

    @Override
    protected boolean onDestinationReached(BoardView v, int position) {
        BObject object = v.map.get(position);
        if (object != null && object.getClass() == Person.class) {
            Person person = (Person) object;
            if (getAntiCovCost() <= person.antiCovs) {
                person.antiCovs -= getAntiCovCost();
                v.changeListener.onAntiCovCountChange(person.antiCovs);
                person.score += getKillScore();
                v.changeListener.onScoreChange(person.score);
                v.deletedObjects.add(this);
                return true;
            } else {
                boolean willBeDead = person.infectPerson();
                v.changeListener.onAntiCovCountChange(person.antiCovs);
                v.changeListener.onScoreChange(person.score);
                if (willBeDead) {
                    v.deletedObjects.add(person);
                }
            }

        }
        destination = selectNewDestination(v);
        if (object == null) {
            v.map.put(destination.position, new PlaceHolder());
        }
        return false;
    }

    private Direction randomDirection() {
        double i = Direction.values().length * Math.random();
        int j = (int) Math.floor(i);
        return Direction.values()[j];
    }


    @Override
    protected boolean canTakePosition(int position, HashMap<Integer, BObject> map) {
        BObject object = map.get(position);
        return object == null || object.getClass() == Person.class;
    }

    public Coordinate selectNewDestination(BoardView v) {

        Direction newDirection;
        MoveResult result;
        do {
            newDirection = randomDirection();
            result = canMove(v, newDirection);
        } while (!result.canMove);
        return new Coordinate(newDirection, v.positionToLocation(result.newPosition), result.newPosition);
    }


    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == Covid.class && currentLocation.equals(((Covid) obj).currentLocation);
    }
}



