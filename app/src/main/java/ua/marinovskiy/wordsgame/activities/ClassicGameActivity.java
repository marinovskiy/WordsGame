package ua.marinovskiy.wordsgame.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
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

public class ClassicGameActivity extends AppCompatActivity {

    private static final String DB_NAME = "wordsgame_database.sqlite3";
    private static final String TABLE_NAME = "task";
    private static final String LVL_ID = "lvl_id";
    private static final String WORD_TASK = "w_task";
    private static final String TABLE_ANS = "answers";
    private static final String LVL_ID_ANS = "lvl_id";
    private static final String ANSWER = "w_ans";
    private static final String ANSWER_POS = "w_position";

    private SQLiteDatabase database;

    private SharedPreferences currentLvl;

    private TextView letters, letters4, foot_clvl;

    // variables for onTouchListener
    private float x, x_started, y, y_started;
    private float isT, isB, isL, isR;
    float widthL, heightL;
    String sDown, sMove, sUp, check = "", agf = "", chp = "", checkpos = "";
    GridView grid;
    int selected = -1, s = 0, lvl = 1, slvl = 0, count = 2;
    String wordsFromDb;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        grid = (GridView) findViewById(R.id.grid);
        letters = (TextView) findViewById(R.id.letters);
        letters4 = (TextView) findViewById(R.id.letters4);
        foot_clvl = (TextView) findViewById(R.id.classic_foot_clvl);

        TextView mTvAppName = (TextView) findViewById(R.id.classic_app_name);
        TextView mTvMode = (TextView) findViewById(R.id.foot_gmode);
        TextView mTvLvl = (TextView) findViewById(R.id.classic_foot_clvl);

        mTvAppName.setTypeface(MyApplication.permanent);
        mTvMode.setTypeface(MyApplication.comic);
        mTvLvl.setTypeface(MyApplication.comic);

        DataBase dbOpenHelper = new DataBase(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        loadLvl();
        if (lvl == 15) {
            AlertDialog.Builder gameComp = new AlertDialog.Builder(ClassicGameActivity.this);
            gameComp.setTitle("Увага")
                    .setMessage("Ви вже пройшли гру. Бажаєте заново пройти гру? Ви втратите попередні результати (це стосується лише данного режиму)")
                    .setCancelable(false)
                    .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ClassicGameActivity.this, ClassicGameSettingsActivity.class);
                            startActivity(intent);
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Згоден", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            lvl = 1;
                            saveLvl();
                            moveToFirstLvl();
                            myadapter = new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.mygridview, R.id.letters, task);
                            grid.setAdapter(myadapter);
                            myadapter.notifyDataSetChanged();
                            myadapter.notifyDataSetInvalidated();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = gameComp.create();
            alert.show();
        }

        if (lvl != 1) {
            saveLvl();
            loadLvl();
        }

        String lvlag = getIntent().getExtras().getString("l");
        if (lvlag.equals("1")) {
            lvl = Integer.valueOf(lvlag);
            saveLvl();
        }
        selectionArgs = new String[]{String.valueOf(lvl)};

        takeTask(task, selectionArgs);
        takeAnswer(answer, selectionArgs);
        takeAnswerPos(chPosition, selectionArgs);

        count = answer.size();

        foot_clvl.setText("Рівень: " + lvl);

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
                            if (slvl == count) {
                                slvl = 0;
                                if (lvl == 15) {
                                    AlertDialog.Builder endOfGame = new AlertDialog.Builder(ClassicGameActivity.this);
                                    endOfGame.setTitle("Вітаємо")
                                            .setMessage("Ви пройшли гру")
                                            .setCancelable(false)
                                            .setPositiveButton("До головного меню", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(ClassicGameActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = endOfGame.create();
                                    alert.show();
                                    for (int i = 0; i < 16; i++) {
                                        grid.getChildAt(i).setBackgroundColor(Color.GREEN);
                                    }
                                } else {
                                    moveToNextLvl();
                                    saveLvl();
                                }
                                if ((lvl >= 9) && (lvl <= 15)) {
                                    myadapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.mygridview4, R.id.letters4, task);
                                    grid.setAdapter(myadapter);
                                }
                                myadapter.notifyDataSetChanged();
                                myadapter.notifyDataSetInvalidated();
                            }
                        } else if ((check.equals(agf)) && (checkpos.equals(chp) == false)) {
                            AlertDialog.Builder error = new AlertDialog.Builder(ClassicGameActivity.this);
                            error.setTitle("Помилка")
                                    .setMessage("Спробуйте вказати слово по іншому")
                                    .setCancelable(false)
                                    .setNegativeButton("ОК",
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

    public void moveToFirstLvl() {
        for (int i = 0; i < 9; i++) {
            grid.getChildAt(i).setBackgroundColor(Color.argb(255, 228, 228, 228));
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
        lvl = 1;
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
        for (int i = 0; i < 9; i++) {
            task.add(words[i]);
        }
        grid.setNumColumns(3);
        count = answer.size();
        foot_clvl.setText("Рівень: " + lvl);
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
                myadapter = new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.mygridview, R.id.letters, task);
                grid.setAdapter(myadapter);
            }
        } else if ((lvl >= 9) && (lvl <= 15)) {
            for (int i = 0; i < 16; i++) {
                task.add(words[i]);
            }
            grid.setNumColumns(4);
            myadapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.mygridview4, R.id.letters4, task);
            grid.setAdapter(myadapter);
        }

        count = answer.size();
        foot_clvl.setText("Рівень: " + lvl);
    }

    public void saveLvl() {
        currentLvl = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = currentLvl.edit();
        ed.putString("curLvl", Integer.toString(lvl));
        ed.commit();
    }

    public void loadLvl() {
        currentLvl = getPreferences(MODE_PRIVATE);
        String lv = currentLvl.getString("curLvl", "");
        if (!lv.equals("")) {
            lvl = Integer.valueOf(lv);
        } else if (lv.equals("")) {
        }
    }

}
