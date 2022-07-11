package com.example.covidgame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;

import com.example.covidgame.R;
import com.example.covidgame.data.enums.CovidType;
import com.example.covidgame.data.enums.Direction;
import com.example.covidgame.listeners.OnBoardStateChangeListener;
import com.example.covidgame.data.models.AntiCov;
import com.example.covidgame.data.models.BObject;
import com.example.covidgame.data.models.Covid;
import com.example.covidgame.data.models.Location;
import com.example.covidgame.data.models.Person;
import com.example.covidgame.data.models.Wall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardView extends View {
    static Person person;
    public OnBoardStateChangeListener changeListener;
    public int columns = 7;
    public int rows = 8;
    public float tileHeight;
    public float tileWidth;
    public float rowLength;
    public float columnLength;
    public HashMap<Integer, BObject> map;
    public ArrayList<BObject> objects;
    public ArrayList<BObject> deletedObjects;
    String TAG = "boardView";
    Paint paintLine;
    int difficulty;
    int fps=20;
    int secondsPerFrame;
    int lineStrokeWidth = 10;
    List<Float> horizontalPoints = new ArrayList<>();
    List<Float> verticalPoints = new ArrayList<>();
    int covidsCount ;
    int[] wallPositions = {8, 9, 10, 11, 12, 15, 22, 26, 29, 33, 36, 40, 53, 52, 51};
    Rect dest = new Rect();
    private Bitmap bitmapRedCovid;
    private Bitmap bitmapGreenCovid;
    private Bitmap bitmapWall;
    private Bitmap bitmapPerson;
    private Bitmap bitmapAntiCov;
    private boolean gameRunning;

    public boolean isGameRunning(){
        return  gameRunning;
    }

    public int getScore(){
        return  person.score;
    }

    public BoardView(Context context,int difficulty) {
        super(context);
        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(lineStrokeWidth);
        this.difficulty=difficulty;
        covidsCount=5+difficulty;
        fps=20+(difficulty-1)*13;
        secondsPerFrame=1000/fps;

    }

    public void setOnBoardChangeListener(OnBoardStateChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    public Location positionToLocation(int p) {
        return new Location((p % columns) * tileWidth, (int) (p / columns) * tileHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBasement(w, h);
        initObjects();
    }

    private void initObjects() {
        gameRunning = true;
        map = new HashMap<>();
        objects = new ArrayList<>();
        deletedObjects = new ArrayList<>();
        int initialPersonPostion = 48;
        person = new Person(initialPersonPostion, positionToLocation(initialPersonPostion));
        addObject(initialPersonPostion, person);
        initialPersonPostion += columns;
        addObject(initialPersonPostion, new AntiCov(initialPersonPostion, positionToLocation(initialPersonPostion)));
        for (int wallPosition : wallPositions) {
            addObject(wallPosition, new Wall(wallPosition, positionToLocation(wallPosition)));
        }
        for (int j = 0; j < covidsCount; j++) {
            int pos = randomPosForCov();
            addObject(pos, new Covid(pos, positionToLocation(pos)));
        }

        changeListener.onScoreChange(person.score);
        changeListener.onAntiCovCountChange(person.antiCovs);
    }

    private void initBasement(int w, int h) {
        tileHeight = (float) h / rows;
        tileWidth = (float) w / columns;
        columnLength = h;
        rowLength = w;
        for (int i = 0; i < rows; i++) {
            float[] a = {0, i * tileHeight + 10, w, i * tileHeight + 10};
            horizontalPoints.addAll(arrayToList(a));
        }
        for (int i = 0; i < columns; i++) {
            float[] b = {i * tileWidth, 10, i * tileWidth, h + 10};
            verticalPoints.addAll(arrayToList(b));
        }
        bitmapRedCovid = BitmapFactory.decodeResource(getResources(), R.drawable.covid_red);
        bitmapGreenCovid = BitmapFactory.decodeResource(getResources(), R.drawable.covid_green);
        bitmapPerson = BitmapFactory.decodeResource(getResources(), R.drawable.mask);
        bitmapWall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        bitmapAntiCov = BitmapFactory.decodeResource(getResources(), R.drawable.anti_cov);
    }

    int randomPosForCov() {
        int p;
        do {
            p = (int) (Math.random() * columns * rows);
        } while (map.get(p) != null);
        return p;
    }

    private void addObject(int index, BObject object) {
        map.put(index, object);
        objects.add(object);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate: ");
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        Log.i(TAG, "onCreateContextMenu: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLines(listToArray(horizontalPoints), paintLine);
        canvas.drawLines(listToArray(verticalPoints), paintLine);

        for (BObject o : objects) {

            dest.set((int) o.currentLocation.x + 5, (int) o.currentLocation.y + 15,
                    (int) o.currentLocation.x + (int) tileWidth - 5,
                    (int) o.currentLocation.y + (int) tileHeight + 5);
            if (o.getClass() == Covid.class) {
                if (((Covid) o).covidType == CovidType.red) {
                    canvas.drawBitmap(bitmapRedCovid, null, dest, null);
                } else {
                    canvas.drawBitmap(bitmapGreenCovid, null, dest, null);
                }
                ((Covid) o).move(this);
            } else if (o.getClass() == Person.class) {
                canvas.drawBitmap(bitmapPerson, null, dest, null);
                ((Person) o).move(this);
            } else if (o.getClass() == Wall.class) {
                canvas.drawBitmap(bitmapWall, null, dest, null);
            } else if (o.getClass() == AntiCov.class) {
                canvas.drawBitmap(bitmapAntiCov, null, dest, null);
            }

        }
        objects.removeAll(deletedObjects);

        try {
            Thread.sleep(secondsPerFrame);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (gameRunning)
            invalidate();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Direction direction = handlePersonMoveDirection(event.getX(), event.getY());
            person.directPerson(direction, this);
        }
        return super.onTouchEvent(event);
    }

    Direction handlePersonMoveDirection(float dx, float dy) {
        float x = person.currentLocation.x;
        float y = person.currentLocation.y;
        Direction direction = Direction.none;
        if (dx < x) {

            if (dy > y && (dy - y) < tileHeight && x - dx < tileWidth) {
                direction = Direction.left;
            }

        } else if ((dx - x) >= tileWidth) {
            if (dy > y && (dy - y) < tileHeight && (dx - x) < 2 * tileWidth) {
                direction = Direction.right;
            }
        } else {
            if (dy < y && y - dy < tileHeight) {
                direction = Direction.up;
            } else if (dy - y >= tileHeight && dy - y < 2 * tileHeight) {
                direction = Direction.down;
            }
        }
        return direction;
    }


    public void resetState() {
        initObjects();
        invalidate();

    }

    public void pauseGame() {
        gameRunning = false;
    }

    public  void resumeGame(){
        gameRunning=true;
        invalidate();
    }

    List<Float> arrayToList(float[] arrayPoints) {
        List<Float> points = new ArrayList<>(4);
        for (float arrayPoint : arrayPoints) {
            points.add(arrayPoint);
        }
        return points;
    }

    float[] listToArray(List<Float> list) {

        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
