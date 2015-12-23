package ua.marinovskiy.wordsgame;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {

    public static Typeface permanent;
    public static Typeface comic;

    @Override
    public void onCreate() {
        super.onCreate();
        initAssets();
    }

    private void initAssets() {
        permanent = Typeface.createFromAsset(getAssets(), getString(R.string.font_p));
        comic = Typeface.createFromAsset(getAssets(), getString(R.string.font_comic));
    }
}
