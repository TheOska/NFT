package cm.nfx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
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

public class TranTime extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback ,NavigationView.OnNavigationItemSelectedListener{

    // CreateNdefMessageCallback ->A callback to be invoked when another NFC device capable of
    // NDEF push (Android Beam) is within range
    private EditText mEditText;
//    CounterClass timer;
    String LOG_TAG_ACTIVITY = "TranTime";
    String GLOBAL_TRACK_LOG = "oska";
    private Toolbar mToolbar;
    boolean hasNFC = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tran_time);

        mEditText = (EditText) findViewById(R.id.edt_tran_time);

        Log.i(GLOBAL_TRACK_LOG ,LOG_TAG_ACTIVITY+" onCreate");
        initToolbar();
        initDrawer();
        hasNFC = hasNFCSupport();
        if(hasNFC) {
            NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(this);
            mAdapter.setNdefPushMessageCallback(this, this);
        }
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

        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    // receive message here
    @Override
    protected void onResume() {
        super.onResume();
        getNFCMessage();
        Log.i(GLOBAL_TRACK_LOG , LOG_TAG_ACTIVITY+" onResume");
//        registerReceiver(br, new IntentFilter(BroadcastService.COUNTDOWN_BR));
//        Log.i(LOG_TAG_ACTIVITY, "Registered broacast receiver");
    }

    private void getNFCMessage() {
        Intent intent = getIntent();
        String action = intent.getAction();
//        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
//            Parcelable[] parcelables =  intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage inNdefMessage = (NdefMessage)parcelables[0];
//            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
//            NdefRecord pushMessage = inNdefRecords[0];
//            String inMsg = new String(pushMessage.getPayload());
//            timer.start();

//            startService(new Intent(MainActivity.this, BroadcastService.class));

//        }

    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }



    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String message = mEditText.getText().toString();
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", message.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        Toast.makeText(this,"done read beam",Toast.LENGTH_LONG).show();
        return ndefMessage;
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

        if (id == R.id.nav_status) {
            // Handle the camera action
        } else if (id == R.id.nav_color_setting) {

        } else if (id == R.id.nav_immune) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}