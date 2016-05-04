package cm.nfx;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import android.os.Build;
import android.os.Bundle;

import android.os.IBinder;
import android.os.Parcelable;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cm.nfx.util.BroadcastService;

/**
 * Created by TheOSka on 27/4/2016.
 */
public class PlayTimeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // CreateNdefMessageCallback ->A callback to be invoked when another NFC device capable of
    // NDEF push (Android Beam) is within range
//    TextView textInfo;
    EditText txtTagContent, txtTagContentReduceTime;
    boolean mBounded;
    private String GLOBAL_TRACK_LOG = "oska";
    private String LOG_TAG_ACTIVITY = "PlayTimeActivity";
    private String TAG_ADD_CARD = "+60";
    private String TAG_MINUS_CARD = "-60";
    private String TEXT_MESSAGE_PREFIX = "You got secret message:\n\n";
    private String MESSAGE_DIVIDER = "%";

    private String substrMessage = "";
    private String substrNum = "";

    private TextView readNFCMessage, writeCardHints, readCardHints;

    NfcAdapter nfcAdapter;
    ToggleButton tglReadWrite;

    TextView serviceViewTimer;
    private Toolbar mToolbar;
    boolean hasNFC = false;

    private View btnView;
    private long TAG_ONE_MINUTE = 60000;
    BroadcastService mBroadcastService;
    private Activity mActivity;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mBroadcastService = new BroadcastService();
//        startService(new Intent(PlayTimeActivity.this, mBroadcastReceiver.getClass()));
//        startService(new Intent(MainActivity.this, BroadcastService.class));


        mActivity = this;
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + " onCreate");

        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_play_time);

        findAllView();
        initToolbar();
        initDrawer();
        initTimer();
        hasNFC = hasNFCSupport();
        if (hasNFC) {
            initNFC();
        }
        handleToggleBtn();
        btnView = findViewById(R.id.test_add_time);
        Utils.changeBgCOlor(mActivity, btnView);
        View header = navigationView.getHeaderView(0);
        Utils.changeToTitleCOlor(mActivity, header);
        registerReceiver(finishActivity, new IntentFilter("finishActivity"));

    }

    private void findAllView() {

        tglReadWrite = (ToggleButton) findViewById(R.id.tglReadWrite);

        // write will show
        txtTagContent = (EditText) findViewById(R.id.txtTagContent);
        readNFCMessage = (TextView) findViewById(R.id.nfc_read_text);
        writeCardHints = (TextView) findViewById(R.id.hints_1);
        txtTagContentReduceTime = (EditText) findViewById(R.id.txt_tag_reduce_time);

        // read will show
        readCardHints = (TextView) findViewById(R.id.hints_2);


    }

    private void handleToggleBtn() {
        tglReadWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // current is write

                if (!tglReadWrite.isChecked()) {
                    txtTagContent.setVisibility(View.VISIBLE);
                    txtTagContentReduceTime.setVisibility(View.VISIBLE);
                    writeCardHints.setVisibility(View.VISIBLE);

                    readCardHints.setVisibility(View.GONE);

                } else {
                    txtTagContent.setVisibility(View.GONE);
                    txtTagContentReduceTime.setVisibility(View.GONE);
                    writeCardHints.setVisibility(View.GONE);
                    readCardHints.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG_ACTIVITY, "onStart");
        if (!isMyServiceRunning(BroadcastService.class)) {


            Intent mIntent = new Intent(this, BroadcastService.class);

            bindService(mIntent, mConnection, BIND_AUTO_CREATE);


        }
    }


    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(PlayTimeActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mBounded = false;
            mBroadcastService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
//            Toast.makeText(PlayTimeActivity.this, "Service is connected", 1000).show();
            mBounded = true;

            BroadcastService.LocalBinder mLocalBinder = (BroadcastService.LocalBinder) service;
            mBroadcastService = mLocalBinder.getServerInstance();
        }
    };

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
        }
    }

    private void initTimer() {
        serviceViewTimer = (TextView) findViewById(R.id.serviceViewTimer);
        serviceViewTimer.setText("");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

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
//            textInfo.setText(inMsg);
//            timer.start();
            startService(new Intent(PlayTimeActivity.this, BroadcastService.class));

        }

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            if (tglReadWrite.isChecked()) {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if (parcelables != null && parcelables.length > 0) {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                } else {
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String passMessage ="";
                if(!txtTagContentReduceTime.getText().toString().isEmpty() ) {
                    mBroadcastService.setDecreaseTimeMilliSec(Long.parseLong(txtTagContentReduceTime.getText().toString()) * TAG_ONE_MINUTE);
                    passMessage = txtTagContentReduceTime.getText().toString() + MESSAGE_DIVIDER;
                }
                if(txtTagContent.getText().toString() != ""){
                    passMessage += txtTagContent.getText().toString();
                }
                NdefMessage ndefMessage = createNdefMessage(passMessage + "");

                writeNdefMessage(tag, ndefMessage);
            }

        }
    }

    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, PlayTimeActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {

        Log.i(LOG_TAG_ACTIVITY, LOG_TAG_ACTIVITY + "disableForegroundDispatchSystem");
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Message Writed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            if (tag == null) {

                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();

                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                // alert user the NFC tag  cannot write
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }



                ndef.writeNdefMessage(ndefMessage);

                ndef.close();


                Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();


            }


        } catch (Exception e) {

            Log.e("writeNdefMessage", e.getMessage());

        }

    }


    private NdefRecord createTextRecord(String content) {
       // if (Utils.timerFinished == true) { // still have time - allow create Text Record

            try {

                byte[] language;

                language = Locale.getDefault().getLanguage().getBytes("UTF-8");


                final byte[] text = content.getBytes("UTF-8");

                final int languageSize = language.length;

                final int textLength = text.length;

                final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);


                payload.write((byte) (languageSize & 0x1F));

                payload.write(language, 0, languageSize);

                payload.write(text, 0, textLength);


                return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());


            } catch (UnsupportedEncodingException e) {

                Log.e("createTextRecord", e.getMessage());
            }
       // }
        return null;


    }


    private NdefMessage createNdefMessage(String content) {
        //if (Utils.timerFinished == true) { // still have time - allow create Ndef Message

            NdefRecord ndefRecord = createTextRecord(content);

            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

            return ndefMessage;

      //  }

    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
      //  if (Utils.timerFinished == true) { // still have time - allow read nfc

            String tagContent = null;

            try {

                byte[] payload = ndefRecord.getPayload();

                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

                int languageSize = payload[0] & 0063;

                tagContent = new String(payload, languageSize + 1,

                        payload.length - languageSize - 1, textEncoding);

            } catch (UnsupportedEncodingException e) {

                Log.e("getTextFromNdefRecord", e.getMessage(), e);

            }

            return tagContent;

    }


    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = "";
            tagContent = getTextFromNdefRecord(ndefRecord);
            if(!tagContent.equals(TAG_ADD_CARD) && !tagContent.equals(TAG_MINUS_CARD)) {


                readNFCMessage.setText(null);

                //Read people write message
                tagContent = handleSubStr(tagContent);


                Log.i(LOG_TAG_ACTIVITY,"tagContent" + tagContent );

                if(tagContent != null && !tagContent.isEmpty()) {
                    Utils.simpleAlertDialog(mActivity, TEXT_MESSAGE_PREFIX + tagContent);
                }

                // tagContent = TEXT_MESSAGE_PREFIX +  tagContent;

              //  readNFCMessage.setText(tagContent);

            }
            if(tagContent.toString().equals(TAG_ADD_CARD))
                mBroadcastService.setIncreaseTimeMilliSec(TAG_ONE_MINUTE);

            if (tagContent.toString().equals(TAG_MINUS_CARD))
                mBroadcastService.setDecreaseTimeMilliSec(TAG_ONE_MINUTE);
            Log.i("oskackh","content" + tagContent);
//            mBroadcastReceiver.set
        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }

    }

    private String handleSubStr(String tagContent) {


        if (tagContent.indexOf(MESSAGE_DIVIDER) >= 0) {

            substrNum = tagContent.substring(0, tagContent.indexOf(MESSAGE_DIVIDER));
            substrMessage = tagContent.substring(tagContent.indexOf(MESSAGE_DIVIDER) + 1, tagContent.length());
            mBroadcastService.setDecreaseTimeMilliSec(Long.parseLong(substrNum) * TAG_ONE_MINUTE);
            return substrMessage;

        } else
            return tagContent;
//        if(isSetNumber && isSetMessage) {
//            substrNum = tagContent.substring(0, tagContent.indexOf(MESSAGE_DIVIDER));
//            substrMessage = tagContent.substring(tagContent.indexOf(MESSAGE_DIVIDER) + 1, tagContent.length());
//            mBroadcastService.setDecreaseTimeMilliSec(Long.parseLong(substrNum) * TAG_ONE_MINUTE);
//            resetStatus();
//            return substrMessage;
//        }
//        if(isSetMessage && !isSetNumber){
//            resetStatus();
//            return tagContent;
//        }
//        if(isSetNumber && ! isSetMessage){
//            resetStatus();
//            substrNum = tagContent.substring(0, tagContent.indexOf(MESSAGE_DIVIDER));
//            mBroadcastService.setDecreaseTimeMilliSec(Long.parseLong(substrNum) * TAG_ONE_MINUTE);
//            return "";
//        }
//        return tagContent;
    }


    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

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
//startActivity(new Intent(MainActivity.this, BroadcastService.class))
        if (id == R.id.nav_status) {
            Toast.makeText(this, "You already in this page!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_color_setting) {
            startActivity(new Intent(mActivity, ColorSettingActivity.class));
        } else if (id == R.id.nav_immune) {


        } else if (id == R.id.nav_share) {
            Utils.popUpAlertDialogWithQR(mActivity);
        } else if (id == R.id.nav_about_us) {
            Utils.popUpAlertDialog(mActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // receive message here
    @Override
    protected void onResume() {
        super.onResume();



        enableForegroundDispatchSystem();

        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + " onResume");

        registerReceiver(mBroadcastReceiver, new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(LOG_TAG_ACTIVITY, "Registered broacast receiver");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + " onPause");
        disableForegroundDispatchSystem();

        this.unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(br);
        Log.i(LOG_TAG_ACTIVITY, "Unregistered broacast receiver");
    }

    @Override
    protected void onStop() {
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + " onStop");
        super.onStop();

        // reset to 0 for next time enter this activity and start broadcase again
        // this.unregisterReceiver(mBroadcastReceiver);
        // Utils.BroadCastState = 0;


//        if(mBounded) {
//            unbindService(mConnection);
//            mBounded = false;
//        }
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


        this.unregisterReceiver(mBroadcastReceiver);
        this.unregisterReceiver(finishActivity);
        Log.i(GLOBAL_TRACK_LOG, LOG_TAG_ACTIVITY + "Stopped service");
        super.onDestroy();
    }

    private final BroadcastReceiver finishActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            int currentNum = intent.getIntExtra("current", 2);

            if (currentNum == 2) {
                getIntent().removeExtra("current");

                Intent exitintent = new Intent(Intent.ACTION_MAIN);
                exitintent.addCategory(Intent.CATEGORY_HOME);
                exitintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(exitintent);
                System.exit(0);
            } else if (currentNum == 0) {

                getIntent().removeExtra("current");
                Utils.simpleAlertDialog(mActivity, "Your time increased! :) ");

            } else if (currentNum == 1) {
                getIntent().removeExtra("current");
                Utils.simpleAlertDialog(mActivity, "Your time decreased! :( ");

            }
        }
    };

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