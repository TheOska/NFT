package cm.nfx.util;

/**
 * Created by TheOSka on 28/4/2016.
 */

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import cm.nfx.Utils;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "cm.nfx.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;
    private long startTime = 1000000;
    private long totalTimeInMilliSec = 360000;

    IBinder mIBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting timer...");


        cdt = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                totalTimeInMilliSec = millisUntilFinished;
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                sendBroadcast(new Intent("finishActivity"));
            }
        };

        cdt.start();
    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    public class LocalBinder extends Binder {
        public BroadcastService getServerInstance() {
            return BroadcastService.this;
        }

    }

    public void setIncreaseTimeMilliSec(long increaseTimeInMilliSec) {
        if(Utils.identity==1) {
            totalTimeInMilliSec += increaseTimeInMilliSec;
        }else{
            totalTimeInMilliSec += increaseTimeInMilliSec*2;


        }
        Intent incIntent = new Intent("finishActivity");
        incIntent.putExtra("current", 0);
        sendBroadcast(incIntent);

        cdt.cancel();
        cdt = new CountDownTimer(totalTimeInMilliSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                totalTimeInMilliSec = millisUntilFinished;
                sendBroadcast(bi);


            }

            @Override
            public void onFinish() {
                // no time left, game over
                Utils.timerFinished = true;


                Log.i(TAG, "Timer finished");
                sendBroadcast(new Intent("finishActivity"));
            }
        };

        cdt.start();
    }

    public void setDecreaseTimeMilliSec(long increaseTimeInMilliSec) {
        totalTimeInMilliSec -= increaseTimeInMilliSec;
        Intent decIntent = new Intent("finishActivity");
        decIntent.putExtra("current", 1);
        sendBroadcast(decIntent);
        cdt.cancel();
        cdt = new CountDownTimer(totalTimeInMilliSec, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                totalTimeInMilliSec = millisUntilFinished;
                sendBroadcast(bi);


            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                sendBroadcast(new Intent("finishActivity"));
            }
        };

        cdt.start();
    }
}