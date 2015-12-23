package ua.marinovskiy.wordsgame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        TextView mTvAppName = (TextView) findViewById(R.id.tv_main_app_name);
        TextView mTvVersionOfGame = (TextView) findViewById(R.id.tv_vers_of_game);

        Button mBtnGame = (Button) findViewById(R.id.btn_game);
        Button mBtnRecords = (Button) findViewById(R.id.btn_records);
        Button mBtnHelp = (Button) findViewById(R.id.btn_help);

        mTvAppName.setTypeface(MyApplication.permanent);
        mBtnGame.setTypeface(MyApplication.comic);
        mBtnRecords.setTypeface(MyApplication.comic);
        mBtnHelp.setTypeface(MyApplication.comic);
        mTvVersionOfGame.setTypeface(MyApplication.comic);

        mBtnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameModeActivity.class);
                startActivity(intent);
            }
        });

        mBtnRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });

        mBtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

    }

}
