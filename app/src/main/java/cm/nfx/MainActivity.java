package cm.nfx;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback{

    // CreateNdefMessageCallback ->A callback to be invoked when another NFC device capable of
    // NDEF push (Android Beam) is within range
    TextView textInfo;
    EditText textOut;

    NfcAdapter nfcAdapter;
    CounterClass timer;

    Button btnStart, btnStop;
    TextView textViewTime;

    boolean hasNFC = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textInfo = (TextView)findViewById(R.id.info);
        textOut = (EditText)findViewById(R.id.textout);

        initTimer();
        hasNFC = hasNFCSupport();
        if(hasNFC)
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

        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void initNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter==null){
            Toast.makeText(MainActivity.this,
                    "nfcAdapter==null, no NFC adapter exists",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,
                    "Set Callback(s)",
                    Toast.LENGTH_LONG).show();
            nfcAdapter.setNdefPushMessageCallback(this, this);
            nfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    private void initTimer() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        textViewTime.setText("00:03:00");

        timer = new CounterClass(180000, 1000);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timer.start();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timer.cancel();
            }
        });
    }

    // receive message here
    @Override
    protected void onResume() {
        super.onResume();
        getNFCMessage();
    }

    private void getNFCMessage() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Parcelable[] parcelables =  intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage)parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord pushMessage = inNdefRecords[0];
            String inMsg = new String(pushMessage.getPayload());
            textInfo.setText(inMsg);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"transmitted",Toast.LENGTH_LONG).show();
                timer.start();

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
                new byte[] {},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {

            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            textViewTime.setText(hms);
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            textViewTime.setText("Completed.");
        }

    }
}