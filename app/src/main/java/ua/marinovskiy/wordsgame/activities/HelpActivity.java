package ua.marinovskiy.wordsgame.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ua.marinovskiy.wordsgame.MyApplication;
import ua.marinovskiy.wordsgame.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        TextView mTvAppName = (TextView) findViewById(R.id.tv_help_app_name);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        TextView mTvText = (TextView) findViewById(R.id.tv_text);
        TextView mTvHowToTitle = (TextView) findViewById(R.id.tv_how_to_text);
        TextView mTvHowToText = (TextView) findViewById(R.id.tv_how_to_title);

        mTvAppName.setTypeface(MyApplication.permanent);
        mTvTitle.setTypeface(MyApplication.comic);
        mTvText.setTypeface(MyApplication.comic);
        mTvHowToTitle.setTypeface(MyApplication.comic);
        mTvHowToText.setTypeface(MyApplication.comic);
    }

}
