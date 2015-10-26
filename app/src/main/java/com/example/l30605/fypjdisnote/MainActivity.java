package com.example.l30605.fypjdisnote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private static final String TAG = MainActivity.class.toString();
    private BluetoothAdapter BA;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);


        setContentView(R.layout.activity_main);

        try{
        BA = BluetoothAdapter.getDefaultAdapter();


        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BA.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        if(BA == null){
            Intent intent = new Intent(MainActivity.this,home.class);
            startActivity(intent);
            MainActivity.this.finish();
        }

        else{
            if (!BA.isEnabled()) {
            //Turn on bluetooth
                BA.getDefaultAdapter().enable();
            }
            if (BA.isEnabled()) {
                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {

                    }
                    public void onFinish() {
                        Intent intent = new Intent(MainActivity.this,home.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                }.start();
            }
        }
        }catch(Exception ex){
            String message = ex.getMessage();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            //user rejected to turn on bluetooth/search
            Log.i(TAG, Integer.toString(resultCode));
            finish();
        } else if(resultCode == -1){
            //user accepted to turn on bluetooth
            Log.i(TAG, Integer.toString(resultCode));
            if (BA.isEnabled()) {
                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {

                    }
                    public void onFinish() {
                        Intent intent = new Intent(MainActivity.this,home.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }
                }.start();
            }
        }
        else if(resultCode == 120){
            Log.i(TAG, Integer.toString(resultCode));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:
                        Intent mainPage = new Intent(MainActivity.this,home.class);
                        startActivity(mainPage);
                        MainActivity.this.finish();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }



    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
