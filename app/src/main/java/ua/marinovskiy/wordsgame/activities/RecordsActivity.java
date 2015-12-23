package ua.marinovskiy.wordsgame.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;
import ua.marinovskiy.wordsgame.database.DataBase;

public class RecordsActivity extends AppCompatActivity {

    private static final String DB_NAME = "wordsgame_database.sqlite3";

    private static final String TABLE_NAME = "records";
    private static final String REC_ID = "record_id";
    private static final String RECORD = "record";

    private SQLiteDatabase database;

    static TextView tvr;

    public static int lastUserId, lastUserId1;

    public static ArrayList<String> myRecords = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        tvr = (TextView) findViewById(R.id.tvr);
        tvr.setTypeface(MyApplication.permanent);

        DataBase dbOpenHelper = new DataBase(this, DB_NAME);
        database = dbOpenHelper.openDataBase();

        myRecords.clear();
        takeRecords(myRecords);
        ListView lv = (ListView) findViewById(R.id.rlv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myRecords);
        lv.setAdapter(adapter);

    }

    private void takeRecords(ArrayList<String> myRecords) {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]
                        {REC_ID, RECORD},
                null, null, null, null, RECORD);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(1);
                myRecords.add(name);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public static void tU() {
        lastUserId = lastUserId1;
    }
}