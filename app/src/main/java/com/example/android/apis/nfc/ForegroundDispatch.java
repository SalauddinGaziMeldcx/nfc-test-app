package com.example.android.apis.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An example of how to use the NFC foreground dispatch APIs. This will intercept any MIME data
 * based NDEF dispatch as well as all dispatched for NfcF tags.
 */
public class ForegroundDispatch extends Activity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private TextView mText;
    private List<Long> timeList = Collections.synchronizedList(new ArrayList<>());

    private long startTime = System.currentTimeMillis();

    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedState) {
        Log.d("ForegroundDispatch", "onCreate");
        super.onCreate(savedState);

        setContentView(R.layout.foreground_dispatch);
        mText = (TextView) findViewById(R.id.text);
        mText.setText("Scan a tag");
        mText.setMovementMethod(new ScrollingMovementMethod());

        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d("ForegroundDispatch", "Tag dispatch start time - Tag dispatch end time = Difference ,  Average time ");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("ForegroundDispatch", "onResume");
        if (mAdapter != null)
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        startTime = System.currentTimeMillis();

    }

    @Override
    public void onNewIntent(Intent intent) {
        long endTime = System.currentTimeMillis();
        long sTime = startTime;
        long diff = endTime - startTime;
        timeList.add(diff);
        startTime = System.currentTimeMillis();
        executor.execute(() -> {
            int count = timeList.size();
            long total = 0;
            for (int i = 0; i < count; i++) {
                total += timeList.get(i);
            }
            double average = (double) total / count;
            Log.d("ForegroundDispatch", "" + endTime + " - " + sTime + " = " + diff + "   average time : " + average + " Total dispatch : " + count);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }
}
