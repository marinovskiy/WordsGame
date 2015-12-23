package ua.marinovskiy.wordsgame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;

public class GameModeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        TextView mTvAppName = (TextView) findViewById(R.id.tv_game_mode_app_name);
        Button mBtnGameClassic = (Button) findViewById(R.id.btn_game_classic);
        Button mBtnGameArcade = (Button) findViewById(R.id.btn_game_arcade);
        TextView mTvVersOfGame = (TextView) findViewById(R.id.tv_game_mode_vers_of_game);

        mTvAppName.setTypeface(MyApplication.permanent);
        mBtnGameClassic.setTypeface(MyApplication.comic);
        mBtnGameArcade.setTypeface(MyApplication.comic);
        mTvVersOfGame.setTypeface(MyApplication.comic);

        mBtnGameClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameModeActivity.this, ClassicGameSettingsActivity.class);
                startActivity(intent);
            }
        });

        mBtnGameArcade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameModeActivity.this, ArcadeGameSettingsActivity.class);
                startActivity(intent);
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
}
