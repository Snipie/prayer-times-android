package com.metinkale.prayerapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.metinkale.prayer.BuildConfig;
import com.metinkale.prayerapp.vakit.WidgetService;
import com.metinkale.prayerapp.vakit.sounds.Sounds;
import com.metinkale.prayerapp.vakit.times.TimesHelper;

import io.fabric.sdk.android.Fabric;


public class App extends Application {
    public static final String API_URL = "http://5vkt.tk";
    private static Context mContext;

    public static void e(Exception e) {
        e.printStackTrace();

    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    public static boolean isOnline() {

        if (Sounds.needsCheck())
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Sounds.checkIfNeeded();

                }
            }).start();

        final ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    @SuppressLint("NewApi")
    public static void setExact(AlarmManager am, int type, long time, PendingIntent service) {
        if (Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(type, time, service);
        } else if (Build.VERSION.SDK_INT >= 19) {
            am.setExact(type, time, service);
        } else {
            am.set(type, time, service);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        
        Fabric.with(this, new Crashlytics());
        Utils.init();


        startService(new Intent(this, WidgetService.class));
        MainIntentService.setAlarms(this);
        TimesHelper.getInstance();


    }

    public static final class NotIds {
        public static final int ALARM = 1;
        public static final int ONGOING = 2;
    }


}
