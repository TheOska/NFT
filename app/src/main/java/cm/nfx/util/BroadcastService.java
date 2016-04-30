package cm.nfx.util;

/**
 * Created by TheOSka on 28/4/2016.
 */
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

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
        totalTimeInMilliSec += increaseTimeInMilliSec;
        cdt.cancel();
        cdt = new CountDownTimer(totalTimeInMilliSec, 1000){
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
            }
        };

        cdt.start();
    }

    public void setDecreaseTimeMilliSec(long increaseTimeInMilliSec) {
        totalTimeInMilliSec -= increaseTimeInMilliSec;
        cdt.cancel();
        cdt = new CountDownTimer(totalTimeInMilliSec, 1000){
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
            }
        };

        cdt.start();
    }
}