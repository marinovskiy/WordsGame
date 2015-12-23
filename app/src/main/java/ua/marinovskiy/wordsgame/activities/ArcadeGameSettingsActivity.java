package ua.marinovskiy.wordsgame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;

public class ArcadeGameSettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcade_game_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        TextView mTvAppName = (TextView) findViewById(R.id.arcade_g_s_app_name);
        Button mBtnEasy = (Button) findViewById(R.id.btn_easy);
        Button mBtnMiddle = (Button) findViewById(R.id.btn_middle);
        Button mBtnHard = (Button) findViewById(R.id.btn_hard);
        TextView mTvVersOfGame = (TextView) findViewById(R.id.arcade_g_s_vers_of_game);

        mTvAppName.setTypeface(MyApplication.permanent);
        mBtnEasy.setTypeface(MyApplication.comic);
        mBtnMiddle.setTypeface(MyApplication.comic);
        mBtnHard.setTypeface(MyApplication.comic);
        mTvVersOfGame.setTypeface(MyApplication.comic);

        mBtnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArcadeGameSettingsActivity.this, ArcadeGameActivity.class);
                intent.putExtra("timer", "easy");
                intent.putExtra("l", "1");
                startActivity(intent);
            }
        });

        mBtnMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArcadeGameSettingsActivity.this, ArcadeGameActivity.class);
                intent.putExtra("timer", "middle");
                intent.putExtra("l", "9");
                startActivity(intent);
            }
        });

        mBtnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArcadeGameSettingsActivity.this, ArcadeGameActivity.class);
                intent.putExtra("timer", "hard");
                intent.putExtra("l", "9");
                startActivity(intent);
            }
        });

    }

}