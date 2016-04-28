package cm.nfx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import cm.nfx.util.BroadcastService;

/**
 * Created by TheOSka on 27/4/2016.
 */
public class PlayTimeActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback,NavigationView.OnNavigationItemSelectedListener {
    // CreateNdefMessageCallback ->A callback to be invoked when another NFC device capable of
    // NDEF push (Android Beam) is within range
    TextView textInfo;
    EditText textOut;
    String GLOBAL_TRACK_LOG = "oska";
    String LOG_TAG_ACTIVITY = "PlayTimeActivity";
    NfcAdapter nfcAdapter;

    TextView serviceViewTimer;
    private Toolbar mToolbar;
    boolean hasNFC = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + " onCreate");
        setContentView(R.layout.activity_main);
        textInfo = (TextView) findViewById(R.id.info);
        textOut = (EditText) findViewById(R.id.textout);
        initToolbar();
        initDrawer();
        initTimer();
        hasNFC = hasNFCSupport();
        if (hasNFC)
            initNFC();
    }

    private boolean hasNFCSupport() {
        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
            return false;

        } else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(PlayTimeActivity.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PlayTimeActivity.this,
                    "Set Callback(s)",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    private void initTimer() {
        serviceViewTimer = (TextView) findViewById(R.id.serviceViewTimer);
        serviceViewTimer.setText("00:03:00");
    }

    // receive message here
    @Override
    protected void onResume() {
        super.onResume();
//        getNFCMessage();
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY+ " onResume");
        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i("MainActivity", "Registered broacast receiver");
    }

    private void getNFCMessage() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage) parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord pushMessage = inNdefRecords[0];
            String inMsg = new String(pushMessage.getPayload());
            textInfo.setText(inMsg);
//            timer.start();
            startService(new Intent(PlayTimeActivity.this, BroadcastService.class));

        }

    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "transmitted", Toast.LENGTH_LONG).show();
//                timer.start();
                startService(new Intent(PlayTimeActivity.this, BroadcastService.class));
            }
        });

    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String stringOut = textOut.getText().toString();
        byte[] bytesOut = stringOut.getBytes();

        NdefRecord ndefRecordOut = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[]{},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }


    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        MenuInflater inflater = getMenuInflater();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(GLOBAL_TRACK_LOG,LOG_TAG_ACTIVITY+ " onPause");

//        unregisterReceiver(br);
        Log.i("MainActivity", "Unregistered broacast receiver");
    }

    @Override
    protected void onStop() {
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY+" onStop");

//        try {
//            Log.i("MainActivity", "On Stop");
//            unregisterReceiver(br);
//        } catch (Exception e) {
//            // Receiver was probably already stopped in onPause()
//        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this, BroadcastService.class));
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY +"Stopped service");
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.i(LOG_TAG_ACTIVITY, "Countdown seconds remaining: " + millisUntilFinished / 1000);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
            System.out.println(hms);
            serviceViewTimer.setText(hms);
        }
    }
    

}