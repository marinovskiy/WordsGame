package ua.marinovskiy.wordsgame.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;
import ua.marinovskiy.wordsgame.database.DataBase;

public class ArcadeGameActivity extends AppCompatActivity {

    private static final String DB_NAME = "wordsgame_database.sqlite3";

    private static final String TABLE_NAME = "task";
    private static final String LVL_ID = "lvl_id";
    private static final String WORD_TASK = "w_task";

    private static final String TABLE_ANS = "answers";
    private static final String LVL_ID_ANS = "lvl_id";
    private static final String ANSWER = "w_ans";
    private static final String ANSWER_POS = "w_position";

    private SQLiteDatabase database;
    DataBase db;
    SQLiteDatabase sqld;

    TextView letters, letters4, timeF, foot_bres;
    float x, x_started, y, y_started;
    float isT, isB, isL, isR;
    float widthL, heightL;
    String sDown, sMove, sUp, check = "", agf = "", chp = "", checkpos = "";
    GridView grid;
    int selected = -1, s = 0, lvl = 1, slvl = 0, count = 2, p, td, penalty, multiplier, tick, adt = 0;
    String wordsFromDb, tds, rt;
    String[] words;
    ArrayList<Integer> selectedW = new ArrayList<Integer>();
    ArrayList<String> checkedW = new ArrayList<String>();
    ArrayList<String> task = new ArrayList<String>();
    ArrayList<String> answer = new ArrayList<String>();
    ArrayList<Integer> correctlyW = new ArrayList<Integer>();
    ArrayList<String> chPosition = new ArrayList<String>();
    String selection = "lvl_id = ?";
    String[] selectionArgs = new String[]{String.valueOf(lvl)};
    ArrayAdapter<String> myadapter;
    CountDownTimer myTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade_game);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        final DataBase dbOpenHelper = new DataBase(this, DB_NAME);
        database = dbOpenHelper.openDataBase();
        db = new DataBase(this, "wordsgame_database.sqlite3");
        sqld = db.getWritableDatabase();

        grid = (GridView) findViewById(R.id.grid);
        letters = (TextView) findViewById(R.id.letters);
        letters4 = (TextView) findViewById(R.id.letters4);
        timeF = (TextView) findViewById(R.id.timeF);
        foot_bres = (TextView) findViewById(R.id.foot_bres);

        TextView mTvAppName = (TextView) findViewById(R.id.arcade_app_name);
        TextView mTvTime = (TextView) findViewById(R.id.timeF);
        TextView mTvMode = (TextView) findViewById(R.id.foot_gmode);
        TextView mTvBres = (TextView) findViewById(R.id.foot_bres);

        mTvAppName.setTypeface(MyApplication.permanent);
        mTvTime.setTypeface(MyApplication.comic);
        mTvMode.setTypeface(MyApplication.comic);
        mTvBres.setTypeface(MyApplication.comic);

        foot_bres.setText("Рекорд: ?");

        String lvlag = getIntent().getExtras().getString("l");
        lvl = Integer.valueOf(lvlag);
        selectionArgs = new String[]{String.valueOf(lvl)};

        takeTask(task, selectionArgs);
        takeAnswer(answer, selectionArgs);
        takeAnswerPos(chPosition, selectionArgs);

        count = answer.size();

        wordsFromDb = "";
        for (String c : task) {
            wordsFromDb = wordsFromDb + c;
        }
        words = wordsFromDb.split(" ");
        task.clear();

        if (lvl < 9) {
            for (int i = 0; i < 9; i++) {
                task.add(words[i]);
            }
            grid = (GridView) findViewById(R.id.grid);
            grid.setNumColumns(3);
            myadapter = new ArrayAdapter<String>(this,
                    R.layout.mygridview, R.id.letters, task);
            grid.setAdapter(myadapter);
        } else if ((lvl >= 9) && (lvl <= 15)) {
            for (int i = 0; i < 16; i++) {
                task.add(words[i]);
            }
            grid = (GridView) findViewById(R.id.grid);
            grid.setNumColumns(4);
            myadapter = new ArrayAdapter<String>(this,
                    R.layout.mygridview4, R.id.letters4, task);
            grid.setAdapter(myadapter);
        }

        tds = getIntent().getExtras().getString("timer");
        long tDuration = 0;
        if (tds.equals("easy")) {
            tDuration = 31000;
            multiplier = 15;
            penalty = 200;
        } else if (tds.equals("middle")) {
            tDuration = 61000;
            multiplier = 20;
            penalty = 300;
        } else if (tds.equals("hard")) {
            tDuration = 46000;
            multiplier = 25;
            penalty = 400;
        }
        td = (int) tDuration;
        tick = td;
        myTimer = new CountDownTimer(tDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                timeF.setText("Ваш час: " + millisUntilFinished / 1000);
                tick = tick - 1000;
            }

            public void onFinish() {
                if (adt == 0) {
                    p = (tick / 10000) * count * multiplier - penalty;
                    timeF.setText("Кінець");
                    AlertDialog.Builder builder = new AlertDialog.Builder(ArcadeGameActivity.this);
                    builder.setTitle("Час вийшов. Ви набрали " + p + " очків")
                            .setCancelable(false)
                            .setItems(R.array.time_out, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            Intent intent = new Intent(ArcadeGameActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            break;
                                        case 1:
                                            reloadLvl();
                                            break;
                                        case 2:
                                            Intent intent1 = new Intent(ArcadeGameActivity.this, ArcadeGameSettingsActivity.class);
                                            startActivity(intent1);
                                            break;
                                    }
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }.start();


        grid.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // pressing
                        sDown = "Down: " + x + "," + y;
                        x_started = x;
                        y_started = y;
                        sMove = "";
                        sUp = "";
                        check = "";
                        break;
                    case MotionEvent.ACTION_MOVE: // moving
                        sMove = "Move: " + x + "," + y;

                        if (lvl < 9) {
                            if (selected >= 0) {

                                //move right
                                if ((x - x_started >= 20) && (x >= isR) && (selected != 2) && (selected != 5) && (selected != 8)) {
                                    for (Integer j : selectedW) {
                                        if (j == (selected + 1)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        x_started = x_started + widthL;
                                        isR = isR + widthL;
                                        isL = isL + widthL;
                                        selected = selected + 1;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move left
                                if ((x - x_started <= -20) && (x <= isL) && (selected != 0) && (selected != 3) && (selected != 6)) {
                                    for (int j : selectedW) {
                                        if (j == (selected - 1)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        x_started = x_started - widthL;
                                        isR = isR - widthL;
                                        isL = isL - widthL;
                                        selected = selected - 1;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move down
                                if ((y - y_started >= 50) && (y >= isB) && (selected != 6) && (selected != 7) && (selected != 8)) {
                                    for (int j : selectedW) {
                                        if (j == (selected + 3)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        y_started = y_started + heightL;
                                        isT = isT + heightL;
                                        isB = isB + heightL;
                                        selected = selected + 3;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move up
                                if ((y - y_started <= -50) && (y <= isT) && (selected != 0) && (selected != 1) && (selected != 2)) {
                                    for (int j : selectedW) {
                                        if (j == (selected - 3)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        y_started = y_started - heightL;
                                        isT = isT - heightL;
                                        isB = isB - heightL;
                                        selected = selected - 3;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }
                            }
                        } else if ((lvl >= 9) && (lvl <= 15)) {
                            if (selected >= 0) {

                                //move right
                                if ((x - x_started >= 20) && (x >= isR) && (selected != 3) && (selected != 7) && (selected != 11) && (selected != 15)) {
                                    for (Integer j : selectedW) {
                                        if (j == (selected + 1)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        x_started = x_started + widthL;
                                        isR = isR + widthL;
                                        isL = isL + widthL;
                                        selected = selected + 1;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move left
                                if ((x - x_started <= -20) && (x <= isL) && (selected != 0) && (selected != 4) && (selected != 8) && (selected != 12)) {
                                    for (int j : selectedW) {
                                        if (j == (selected - 1)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        x_started = x_started - widthL;
                                        isR = isR - widthL;
                                        isL = isL - widthL;
                                        selected = selected - 1;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move down
                                if ((y - y_started >= 50) && (y >= isB) && (selected != 12) && (selected != 13) && (selected != 14) && (selected != 15)) {
                                    for (int j : selectedW) {
                                        if (j == (selected + 4)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        y_started = y_started + heightL;
                                        isT = isT + heightL;
                                        isB = isB + heightL;
                                        selected = selected + 4;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }

                                //move up
                                if ((y - y_started <= -50) && (y <= isT) && (selected != 0) && (selected != 1) && (selected != 2) && (selected != 3)) {
                                    for (int j : selectedW) {
                                        if (j == (selected - 4)) {
                                            s = s + 1;
                                        }
                                    }
                                    if (s == 0) {
                                        y_started = y_started - heightL;
                                        isT = isT - heightL;
                                        isB = isB - heightL;
                                        selected = selected - 4;
                                        selectedW.add(selected);
                                        checkedW.add(words[selected]);
                                        grid.getChildAt(selected).setBackgroundColor(Color.GREEN);
                                    }
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP: // unpressing
                    case MotionEvent.ACTION_CANCEL:
                        sMove = "";
                        sUp = "Up: " + x + "," + y;
                        s = 0;
                        selected = -1;

                        for (String c : checkedW) {
                            check = check + c;
                        }

                        for (int d : selectedW) {
                            checkpos = checkpos + Integer.toString(d);
                        }


                        for (String b : chPosition) {
                            chp = b;
                            if (checkpos.equals(b)) {
                                break;
                            }
                        }

                        for (String a : answer) {
                            agf = a;
                            if (check.equals(a)) {
                                break;
                            }
                        }

                        if ((check.equals(agf)) && (checkpos.equals(chp))) {
                            correctlyW.addAll(selectedW);

                            slvl = slvl + 1;
                            int flc = 0;
                            if (((tds.equals("easy")) && (lvl == 8) && (slvl == count)) || (lvl == 15) && (slvl == count)) {
                                flc = 1;
                                myTimer.cancel();
                                AlertDialog.Builder allLvlsCompl = new AlertDialog.Builder(ArcadeGameActivity.this);
                                allLvlsCompl.setTitle("Вітаємо! Ви пройшли всі рівні для данної складності")
                                        .setCancelable(false)
                                        .setItems(R.array.all_lvl_comp, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        Intent intent = new Intent(ArcadeGameActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    case 1:
                                                        Intent intent1 = new Intent(ArcadeGameActivity.this, ArcadeGameSettingsActivity.class);
                                                        startActivity(intent1);
                                                        break;
                                                }
                                            }
                                        });
                                AlertDialog alert = allLvlsCompl.create();
                                alert.show();
                            }

                            if ((slvl == count) && (flc == 0)) {
                                myTimer.cancel();
                                p = (tick / 10000) * count * multiplier;
                                ContentValues cv = new ContentValues();
                                RecordsActivity.tU();
                                cv.put("record", p);
                                sqld.insert("records", "record", cv);
                                adt = 1;
                                AlertDialog.Builder lvlCompl = new AlertDialog.Builder(ArcadeGameActivity.this);
                                lvlCompl.setTitle("Вітаємо! Ви пройшли рівень, набравши " + p + " очок")
                                        .setCancelable(false)
                                        .setItems(R.array.lvl_comp, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        Intent intent = new Intent(ArcadeGameActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        break;
                                                    case 1:
                                                        myTimer.start();
                                                        reloadLvl();
                                                        break;
                                                    case 2:
                                                        moveToNextLvl();
                                                        myTimer.start();
                                                        break;
                                                }
                                            }
                                        });
                                AlertDialog alert = lvlCompl.create();
                                alert.show();
                                slvl = 0;
                            }

                        } else if ((check.equals(agf)) && (checkpos.equals(chp) == false)) {
                            AlertDialog.Builder error = new AlertDialog.Builder(ArcadeGameActivity.this);
                            error.setTitle("Помилка")
                                    .setMessage("Спробуйте вказати слово по іншому")
                                    .setCancelable(false)
                                    .setNegativeButton("Окей",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    checkedW.clear();
                                                    selectedW.clear();
                                                }
                                            });
                            AlertDialog alert = error.create();
                            alert.show();
                            for (int i : selectedW) {
                                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
                            }

                            for (int i : correctlyW) {
                                grid.getChildAt(i).setBackgroundColor(Color.GREEN);
                            }
                        } else {
                            for (int i : selectedW) {
                                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
                            }

                            for (int i : correctlyW) {
                                grid.getChildAt(i).setBackgroundColor(Color.GREEN);
                            }
                        }

                        checkedW.clear();
                        selectedW.clear();
                        check = "";
                        checkpos = "";
                        chp = "";
                        agf = "";

                        break;
                }
                return false;
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                for (Integer j : selectedW) {
                    if (j == i) {
                        s = s + 1;
                    }
                }
                if (s == 0) {
                    isT = view.getTop();
                    isB = view.getBottom();
                    isL = view.getLeft();
                    isR = view.getRight();
                    widthL = isR - isL;
                    heightL = isB - isT;
                    selected = i;
                    selectedW.add(selected);
                    checkedW.add(words[selected]);
                    grid.getChildAt(i).setBackgroundColor(Color.GREEN);
                }
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void takeTask(ArrayList<String> task, String[] selectionArgs) {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]
                        {LVL_ID, WORD_TASK},
                selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(1);
                task.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void takeAnswer(ArrayList<String> answer, String[] selectionArgs) {
        Cursor cursor = database.query(TABLE_ANS,
                new String[]
                        {LVL_ID_ANS, ANSWER},
                selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(1);
                answer.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void takeAnswerPos(ArrayList<String> chPosition, String[] selectionArgs) {
        Cursor cursor = database.query(TABLE_ANS,
                new String[]
                        {LVL_ID_ANS, ANSWER_POS},
                selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(1);
                chPosition.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void reloadLvl() {
        if (lvl < 9) {
            for (int i = 0; i < 9; i++) {
                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
            }
        } else if ((lvl >= 9) && (lvl <= 15)) {
            for (int i = 0; i < 16; i++) {
                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
            }
        }
        check = "";
        checkpos = "";
        chp = "";
        agf = "";
        selectedW.clear();
        correctlyW.clear();
        checkedW.clear();
    }

    public void moveToNextLvl() {
        if (lvl < 9) {
            for (int i = 0; i < 9; i++) {
                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
            }
        } else if ((lvl >= 9) && (lvl <= 15)) {
            for (int i = 0; i < 16; i++) {
                grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
            }
        }
        check = "";
        checkpos = "";
        chp = "";
        agf = "";
        task.clear();
        answer.clear();
        chPosition.clear();
        selectedW.clear();
        correctlyW.clear();
        checkedW.clear();
        lvl = lvl + 1;
        selectionArgs = new String[]{String.valueOf(lvl)};
        takeTask(task, selectionArgs);
        takeAnswer(answer, selectionArgs);
        takeAnswerPos(chPosition, selectionArgs);
        wordsFromDb = "";
        for (String c : task) {
            wordsFromDb = wordsFromDb + c;
        }
        words = wordsFromDb.split(" ");
        task.clear();
        if (lvl < 9) {
            for (int i = 0; i < 9; i++) {
                task.add(words[i]);
            }
            myadapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.mygridview, R.id.letters, task);
            grid.setAdapter(myadapter);
        } else if ((lvl >= 9) && (lvl <= 15)) {
            for (int i = 0; i < 16; i++) {
                task.add(words[i]);
            }
            grid.setNumColumns(4);
            myadapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.mygridview4, R.id.letters4, task);
            grid.setAdapter(myadapter);
        }

        myadapter.notifyDataSetChanged();
        myadapter.notifyDataSetInvalidated();
        count = answer.size();
    }

}
