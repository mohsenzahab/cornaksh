package com.example.covidgame.models;

import com.example.covidgame.BoardView;
import com.example.covidgame.enums.Direction;

import java.util.HashMap;

public class Person extends MovableObject {
    public int antiCovs;
    public int score = 0;
    boolean moving = false;
    public Person(int position, Location location) {
        this.position = position;
        this.currentLocation = location;
        this.antiCovs = 12;
    }

    @Override
    float getSpeed() {
        return (float) 2 * Location.MOVE_UNIT;
    }

    @Override
    protected float afterDestRemainingSteps() {
        return 0;
    }

    public void directPerson(Direction direction, BoardView v) {
        if (moving) {
            return;
        }
        int destPosition = position;
        switch (direction) {

            case up:
                if (position >= v.columns) {
                    destPosition -= v.columns;
                }
                break;
            case down:
                if (position <= v.columns * v.rows - v.columns) {
                    destPosition += v.columns;
                }
                break;
            case left:
                if (position % v.columns > 0) {
                    destPosition -= 1;
                }
                break;
            case right:
                if (position % v.columns < v.columns - 1) {
                    destPosition += 1;
                }
                break;
            case none:
                return;
        }
        if (!canTakePosition(destPosition, v.map)) {
            return;
        }
        setPersonMoving();
        destination = new Coordinate(direction, v.positionToLocation(destPosition), destPosition);
    }

    void setPersonMoving() {
        moving = true;
    }

    void setPersonIdle() {
        moving = false;
    }


    @Override
    protected boolean onNewMove(BoardView v) {
//        if person is still moving.
        if (moving) {
            return true;
        }
        return moving;
    }

    @Override
    protected boolean canTakePosition(int position, HashMap<Integer, BObject> map) {
        return map.get(position) == null || map.get(position).getClass() != Wall.class;
    }

    @Override
    protected boolean onDestinationReached(BoardView v, int position) {
        BObject object = v.map.get(position);
        setPersonIdle();
        if (object != null && object.getClass() == Covid.class) {
            Covid covid = (Covid) object;
            if (antiCovs >= covid.getAntiCovCost()) {
                if (v.map.get(covid.destination.position) != null &&
                        v.map.get(covid.destination.position).getClass() == PlaceHolder.class) {
                    v.map.remove(covid.destination.position);
                }
                v.deletedObjects.add(covid);
                antiCovs -= covid.getAntiCovCost();
                v.changeListener.onAntiCovCountChange(antiCovs);
                score += covid.getKillScore();
                v.changeListener.onScoreChange(score);
                return false;
            } else {
                boolean willBeDead = infectPerson();
                v.changeListener.onAntiCovCountChange(antiCovs);
                v.changeListener.onScoreChange(score);
                if (willBeDead) {
                    v.deletedObjects.add(this);
                }
                return willBeDead;
            }
        } else {
            if (object != null && object.getClass() == AntiCov.class) {
                antiCovs = 12;
                v.changeListener.onAntiCovCountChange(antiCovs);
            }
            return false;
        }
    }

    public boolean infectPerson() {
        antiCovs = 0;
        score -= 10;
        return score < 0;
    }
}
