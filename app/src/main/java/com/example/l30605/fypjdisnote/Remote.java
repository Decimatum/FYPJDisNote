package com.example.l30605.fypjdisnote;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l30605.fypjdisnote.Entity.Note;
import com.example.l30605.fypjdisnote.Logic.ConnectivityPageService;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Remote extends ActionBarActivity implements SensorEventListener, NumberPicker.OnValueChangeListener{
    // Debugging
    private static final String TAG = "ConnectivityPage";
    private static final boolean D = true;

    // Message types sent from the ConnectivityPageService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    final int handlerState = 0;

    // Key names received from the ConnectivityPageService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private ConnectivityPageService mChatService = null;

    /**Imports for sensors**/
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private ConnectedThread mConnectedThread;

    TextView tvReceived,tvPointerColor,tvConnect;

    LinearLayout toggleButtonLL,motionToggleLL;

    RelativeLayout LLSelect, rlDock,rlNote;

    Switch toggle,motionToggle;
    SharedPreferences.Editor editor;
    String pairedDeviceAddress,hasOnCreateOptionsMenuBeenCreated,isDeviceConnected = "", isMotionControlSelected="";
    Menu settingsMenu;
    MenuItem connectBT, disconnectBT,addNote,settings;
    Dialog loadingDialog;

    Button btnSelect,btnEdit;
    ImageButton btnUp,btnDown,btnLeft,btnRight,btnRotateLeft,btnRotateRight,btnGrab;

    private static Note note;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the window layout
        setContentView(R.layout.activity_remote);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        motionToggleLL = (LinearLayout)findViewById(R.id.motionToggleLL);
        motionToggle = (Switch)findViewById(R.id.motionToggle);
        motionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    isMotionControlSelected = "YES";
                } else {
                   isMotionControlSelected = "NO";
                }
            }
        });



        rlDock = (RelativeLayout)findViewById(R.id.rlDock);

        rlNote = (RelativeLayout)findViewById(R.id.rlNote);


        try {
            // Register for broadcasts on BluetoothAdapter state change
            this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        }
        catch(IllegalArgumentException ex){

            String str = ex.getMessage();
        }
        editor = getSharedPreferences("RMCSP", MODE_PRIVATE).edit();
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if(mBluetoothAdapter.isEnabled()) {
            SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
            String deviceName = prefs.getString("deviceName", null);
            String deviceAddress = prefs.getString("deviceAddress", null);

            if (deviceName == null && deviceAddress == null) {
                if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected =="") {
                    connectBT.setVisible(true);
                    disconnectBT.setVisible(false);
                    addNote.setVisible(true);
                }
            }
            else if (deviceName != null && deviceAddress != null) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                if (mChatService == null) setupChat();
                mChatService.connect(device, true);
                //Toast.makeText(getApplicationContext(),deviceName, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),deviceAddress, Toast.LENGTH_SHORT).show();
            }
        }


        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        toggle = (Switch) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    motionToggleLL.setVisibility(View.VISIBLE);
                    LLSelect.setVisibility(View.VISIBLE);
                    String pointerColor = sp.getString("pointerColor", "myColor");
                    sendMessage("PointerColor" + pointerColor);
                } else {
                    // The toggle is disabled
                    motionToggleLL.setVisibility(View.INVISIBLE);
                    rlDock.setVisibility(View.INVISIBLE);
                    LLSelect.setVisibility(View.GONE);
                    rlNote.setVisibility(View.GONE);

                    sendMessage("PointerColor" + "Cursor");
                }
            }
        });

        tvPointerColor = (TextView)findViewById(R.id.tvPointerColor);
        btnUp = (ImageButton) findViewById(R.id.btnUp);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Up");
            }
        });

        btnDown = (ImageButton) findViewById(R.id.btnDown);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Down");
            }
        });

        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Left");
            }
        });

        btnRight = (ImageButton) findViewById(R.id.btnRight);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Right");
            }
        });


        btnSelect = (Button)findViewById(R.id.btnSelect);
        btnSelect.setTextColor(Color.WHITE);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Select");
            }
        });


        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setTextColor(Color.WHITE);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView etTitle = (TextView)findViewById(R.id.tvTitle);
                //TextView etDescription = (TextView)findViewById(R.id.tvDescription);
                //Note note = new Note();
                // note.setNoteID(i);
                //String title = etTitle.getText().toString();
                //String description = etDescription.getText().toString();
                //note.setTitle(title);;
                //note.setDescription(description);

                // Toast.makeText(Remote.this, note.getTitle().toString(), Toast.LENGTH_SHORT).show();
                // Toast.makeText(Remote.this, note.getDescription().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Remote.this, EditNote.class);
                intent.putExtra("Note",note);
                startActivity(intent);
            }
        });

        LLSelect = (RelativeLayout) findViewById(R.id.LLSelect);

        toggleButtonLL = (LinearLayout) findViewById(R.id.toggleButtonLL);

        tvConnect = (TextView) findViewById(R.id.tvConnect);

        btnRotateLeft = (ImageButton) findViewById(R.id.btnRotateLeft);

        btnRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("RotateLeft");
            }
        });

        btnRotateRight= (ImageButton) findViewById(R.id.btnRotateRight);

        btnRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("RotateRight");
            }
        });
        btnGrab = (ImageButton) findViewById(R.id.btnGrab);
        btnGrab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Grab");
            }
        });



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    static final float ALPHA = 0.2f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
        int upGestureValueSensitivity = prefs.getInt("upGestureValue", 2);
        int downGestureValueSensitivity = prefs.getInt("downGestureValue", 2);
        String downGestureValueSensitivityConverted = "-"+downGestureValueSensitivity;
        int leftGestureValueSensitivity = prefs.getInt("leftGestureValue", 2);
        int rightGestureValueSensitivity = prefs.getInt("rightGestureValue", 2);
        String rightGestureValueSensitivityConverted = "-"+rightGestureValueSensitivity;

        if(isMotionControlSelected=="YES") {
            //Left
            if (Math.round(x) >= leftGestureValueSensitivity) {
                //Log.i("ACCELEROMETER X: ", "LEFT " + String.valueOf(x));
                //String message = "ppt pre";
                String accValue = "";
                if(Math.round(x)<10){
                    accValue = "0" + String.valueOf(Math.round(x)-1);
                }
                else if(Math.round(x)>=10){
                    accValue = String.valueOf(Math.round(x)-1);
                }

                //String message = "aLt  " + cursorSeekValue;
                String message = "aLt  " + accValue;
                sendMessage(message);
            }
            //Right
            else if (Math.round(x) <= Integer.parseInt(rightGestureValueSensitivityConverted)) {
                //Log.i("ACCELEROMETER X: ", "RIGHT" + String.valueOf(x));
                //String message = "ppt nex";
                String accValue = "";
                if(Math.round(Math.abs(x))<10){
                    accValue = "0" + String.valueOf(Math.round(Math.abs(x))-1);
                }
                else if(Math.round(Math.abs(x))>=10){
                    accValue = String.valueOf(Math.round(Math.abs(x))-1);
                }
                //String message = "aRt  " + cursorSeekValue;
                String message = "aRt  " + accValue;
                sendMessage(message);
            }
            //UP
            if (Math.round(y) >= upGestureValueSensitivity) {
                //Log.i("ACCELEROMETER Y: ", "UP " + String.valueOf(y));
                String accValue = "";
                if(Math.round(y)<10){
                    accValue = "0" + String.valueOf(Math.round(y)-1);
                }
                else if(Math.round(y)>=10){
                    accValue = String.valueOf(Math.round(y)-1);
                }
                //String message = "aUp  " + cursorSeekValue;
                String message = "aUp  " + accValue;
                sendMessage(message);
            }
            //Down
            else if (Math.round(y) <= Integer.parseInt(downGestureValueSensitivityConverted)) {
                //Log.i("ACCELEROMETER Y: ", "DOWN " +String.valueOf(y));
                String accValue = "";
                if(Math.round(Math.abs(y))<10){
                    accValue = "0" + String.valueOf(Math.round(Math.abs(y))-1);
                }
                else if(Math.round(Math.abs(y))>=10){
                    accValue = String.valueOf(Math.round(Math.abs(y))-1);
                }
                //String message = "aDn  " + cursorSeekValue;
                String message = "aDn  " + accValue;
                sendMessage(message);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();


        if (!mBluetoothAdapter.isEnabled()) {
            //Turn on bluetooth
            mBluetoothAdapter.getDefaultAdapter().enable();
        }

        if(D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == ConnectivityPageService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
        if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected=="YES") {
            connectBT.setVisible(false);
            disconnectBT.setVisible(true);
            addNote.setVisible(true);
        }
        else if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected =="") {
            connectBT.setVisible(true);
            disconnectBT.setVisible(false);
            addNote.setVisible(false);
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // Initialize the ConnectivityPageService to perform bluetooth connections
        mChatService = new ConnectivityPageService(this, mHandler);
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        if(mChatService != null) {
            sendMessage("DC");
            mChatService.stop();
        }
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatService.stop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        sendMessage("DC");
        if (mChatService != null)

            mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != ConnectivityPageService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the ConnectivityPageService to write
            byte[] send = message.getBytes();
          //  Toast.makeText(this,send.toString(),Toast.LENGTH_LONG).show();
            mChatService.write(send);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        sendMessage(message);
                    }
                    if(D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        pairedDeviceAddress = address;
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater inflater = getMenuInflater();
        this.settingsMenu = menu;
         inflater.inflate(R.menu.menu_remote, menu);
        connectBT = settingsMenu.findItem(R.id.secure_connect_scan);
        disconnectBT = settingsMenu.findItem(R.id.disconnectDevice);
        addNote = settingsMenu.findItem(R.id.addnote);
        settings = settingsMenu.findItem(R.id.settings);

        //Checking if user has previously connected to any device through this app
       SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
        String deviceName = prefs.getString("deviceName", null);
        String deviceAddress = prefs.getString("deviceAddress", null);
        hasOnCreateOptionsMenuBeenCreated = "YES";
        if (deviceName == null && deviceAddress == null) {
            if(isDeviceConnected =="") {
                connectBT.setVisible(true);
                disconnectBT.setVisible(false);
                addNote.setVisible(false);
            }
        } else{
           connectBT.setVisible(false);
            disconnectBT.setVisible(true);
            addNote.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                    // Launch the DeviceListActivity to see devices and do scan
                    serverIntent = new Intent(Remote.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

                return true;
            case R.id.disconnectDevice:
                sendMessage("DC");
                mChatService.stop();
                connectBT.setVisible(true);
                disconnectBT.setVisible(false);
                addNote.setVisible(false);
                return true;
            case R.id.addnote:
                Intent intent = new Intent(Remote.this,CreateNote.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent intentSettings = new Intent(Remote.this,Settings.class);
                startActivity(intentSettings);
                return true;
        }
        return false;
    }

    // The Handler that gets information back from the ConnectivityPageService
    private final Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == handlerState) {
                String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                tvReceived.setText(readMessage);
            }

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case ConnectivityPageService.STATE_CONNECTED:
                            toggleButtonLL.setVisibility(View.VISIBLE);
                            // LLSelect.setVisibility(View.VISIBLE);
                            tvConnect.setVisibility(View.GONE);
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(100);
                            Toast.makeText(getApplicationContext(),"Pairing successful", Toast.LENGTH_SHORT).show();
                            if(mConnectedDeviceName!=null && pairedDeviceAddress != null) {
                                editor.putString("deviceName", mConnectedDeviceName);
                                editor.putString("deviceAddress", pairedDeviceAddress);
                                Log.i(TAG, mConnectedDeviceName + " " + pairedDeviceAddress);
                                editor.commit();
                            }

                             isDeviceConnected = "YES";

                            addNote.setVisible(true);

                            if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected=="YES") {
                                connectBT.setVisible(false);
                                disconnectBT.setVisible(true);

                            }
                            if(isMotionControlSelected!="YES"){

                            }

                            loadingDialog.dismiss();


                            break;
                       case ConnectivityPageService.STATE_CONNECTING:


                            displayLoadingDialog("connect");
                            if(hasOnCreateOptionsMenuBeenCreated =="YES") {
                                connectBT.setVisible(false);
                                disconnectBT.setVisible(false);

                            }
                            break;

                        case ConnectivityPageService.STATE_LISTEN:
                        case ConnectivityPageService.STATE_NONE:

                            //addNote.setVisible(false);
                            isDeviceConnected = "";

                            toggle.setChecked(false);

                            if(loadingDialog!=null)
                                loadingDialog.dismiss();
                            if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected =="") {
                                addNote.setVisible(false);
                                connectBT.setVisible(true);
                                disconnectBT.setVisible(false);
                                LLSelect.setVisibility(View.GONE);
                                toggleButtonLL.setVisibility(View.GONE);
                                tvConnect.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //Toast.makeText(getApplicationContext(),writeMessage, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    final String readMessage = new String(readBuf, 0, msg.arg1);

                    if(readMessage.contains("Ne")){
                        //Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
                        TextView tvEdit = (TextView)findViewById(R.id.tvTitle);
                        TextView tvDescript = (TextView)findViewById(R.id.tvDescription);
                        String full = readMessage;
                        Log.e("Testing",full);
                        full = full.replace("Ne:","");

                        String[] split = full.split(",");

                        String uuid = split[0];
                        Log.e(null,uuid);
                        String title = split[1];
                        Log.e(null,title);
                        String description = split[2];
                        Log.e(null,description);
                        String noteType = split[3];
                        Log.e(null,noteType);
                        String noteSize = split[4];
                        Log.e(null,noteSize);
                        String textSize = split[5];
                        Log.e(null,textSize);
                        String lastModi = split[6];
                        Log.e(null,lastModi);

                        note = new Note();
                        note.setUUID(uuid);
                        note.setTitle(title);;
                        note.setDescription(description);
                        note.setNoteType(noteType);
                        note.setSize(noteSize);
                        note.setFontsize(textSize);
                        note.setLastModi(lastModi);

                        tvEdit.setText(title);
                        tvDescript.setText(description);

                        RelativeLayout rl = (RelativeLayout)findViewById(R.id.rlNote);
                        rl.setVisibility(View.VISIBLE);

                        rlDock.setVisibility(View.VISIBLE);
                    }

                    new CountDownTimer(100, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                        //    Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
                            if(!(readMessage.equals(null) || readMessage.equals(""))){

                            }
                        }
                    }.start();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    //Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // It means the user has changed his bluetooth state.
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                    // The user bluetooth is turning off yet, but it is not disabled yet.
                    mChatService.stop();
                    return;
                }

                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    // The user bluetooth is already disabled.
                    return;
                }

            }
        }
    };

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is",""+newVal);
    }




    public void displayLoadingDialog(String loadingType){
        loadingDialog = new Dialog(Remote.this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.loading_layout);
        ProgressBar pb = (ProgressBar)loadingDialog.findViewById(R.id.progress_bar);
        pb.setVisibility(View.VISIBLE);
        TextView tv = (TextView) loadingDialog.findViewById(R.id.loading_message);
        if(loadingType.equals("slides")) {
            tv.setText("Retrieving Slides, Please Wait...");
        }
        else if(loadingType.equals("connect")){
            tv.setText("Attempting To Connect...");
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }





//    @Override
//    public void onBackPressed() {
//        //Toast.makeText(getApplicationContext(),"Exiting...", Toast.LENGTH_SHORT).show();
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Closing Application")
//                .setMessage("Are you sure you want to close this application?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        sendMessage("Disconn");
//                        mChatService.stop();
//                        mBluetoothAdapter.disable();
//                        System.exit(0);
//                    }
//
//                })
//                .setNegativeButton("No", null)
//                .show();
//        //super.onBackPressed();
//    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    mHandler.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
