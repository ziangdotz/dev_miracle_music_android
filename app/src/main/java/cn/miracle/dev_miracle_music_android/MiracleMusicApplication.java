package cn.miracle.dev_miracle_music_android;

import android.app.Application;

public class MiracleMusicApplication extends Application {

    private static MiracleMusicApplication miracleMusicApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        miracleMusicApplication = this;
    }

    public static MiracleMusicApplication getInstance() {
        return miracleMusicApplication;
    }

}
