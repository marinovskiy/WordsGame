package ua.marinovskiy.wordsgame.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class ClassicGameSettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        TextView mTvAppName = (TextView) findViewById(R.id.classic_g_s_app_name);
        Button mBtnNew = (Button) findViewById(R.id.btn_new);
        Button mBtnContinue = (Button) findViewById(R.id.btn_cont);
        TextView mTvVersOfGame = (TextView) findViewById(R.id.classic_g_s_vers_of_game);

        mTvAppName.setTypeface(MyApplication.permanent);
        mBtnNew.setTypeface(MyApplication.comic);
        mBtnContinue.setTypeface(MyApplication.comic);
        mTvVersOfGame.setTypeface(MyApplication.comic);

        mBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newGame = new AlertDialog.Builder(ClassicGameSettingsActivity.this);
                newGame.setTitle("Увага")
                        .setMessage("Якщо ви розпочнете нову гру, ви втратите попередні результати (це стосується лише данного режиму)")
                        .setCancelable(false)
                        .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Згоден", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(ClassicGameSettingsActivity.this, ClassicGameActivity.class);
                                intent.putExtra("l", "1");
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = newGame.create();
                alert.show();
            }
        });

        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassicGameSettingsActivity.this, ClassicGameActivity.class);
                intent.putExtra("l", "nothing");
                startActivity(intent);
            }
        });

    }
}