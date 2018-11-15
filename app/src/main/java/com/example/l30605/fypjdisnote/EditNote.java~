package com.example.l30605.fypjdisnote;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l30605.fypjdisnote.Entity.Note;
import com.example.l30605.fypjdisnote.Logic.ConnectivityPageService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditNote extends ActionBarActivity {

    EditText etTitle,etDescription;
    Button btnSave,btnDelete;



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

    String pairedDeviceAddress,hasOnCreateOptionsMenuBeenCreated,isDeviceConnected = "", isMotionControlSelected="";
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    MenuItem connectBT, disconnectBT;
    Menu settingsMenu;
    SharedPreferences.Editor editor;
    Dialog loadingDialog;

    Spinner spinFontSize,spinNoteSize,spinNoteType;
    // Member object for the chat services
    private ConnectivityPageService mChatService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        if(D) Log.e(TAG, "+++ ON CREATE +++");

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


        Note note = (Note)getIntent().getSerializableExtra("Note");

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        String str = note.getTitle().toString();
        Log.d("TitleFirst",str);
        etTitle.setText(str);
        etDescription.setText(note.getDescription().toString());

        spinFontSize = (Spinner)findViewById(R.id.spinFontSize);
        ArrayAdapter<CharSequence> FontSizeArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Size, android.R.layout.simple_spinner_item);
        FontSizeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFontSize.setAdapter(FontSizeArrayAdapter);
        String fontSize = note.getFontsize();
        if (!fontSize.equals(null)) {
            int spinnerPosition = FontSizeArrayAdapter.getPosition(fontSize);
            spinFontSize.setSelection(spinnerPosition);
        }

        spinNoteSize = (Spinner)findViewById(R.id.spinNoteSize);
        spinNoteSize.setAdapter(FontSizeArrayAdapter);
        String noteSize = note.getSize();
        if (!noteSize.equals(null)) {
            int spinnerPosition = FontSizeArrayAdapter.getPosition(noteSize);
            spinNoteSize.setSelection(spinnerPosition);
        }

        spinNoteType = (Spinner)findViewById(R.id.spinNoteType);
        ArrayAdapter<CharSequence> BackgroundArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Background, android.R.layout.simple_spinner_item);
        BackgroundArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNoteType.setAdapter(BackgroundArrayAdapter);
        String noteType = note.getNoteType();
        if (!noteSize.equals(null)) {
            int spinnerPosition = BackgroundArrayAdapter.getPosition(noteType);
            spinNoteType.setSelection(spinnerPosition);
        }

        spinNoteType.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;
        spinNoteSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;

        spinFontSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EditNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note i = (Note)getIntent().getSerializableExtra("Note");

                Note note = new Note();
                EditText etTitle = (EditText)findViewById(R.id.etTitle);
                EditText etDescription = (EditText)findViewById(R.id.etDescription);

                String uuid =  i.getUUID();
                Log.d("UUIDFirst",uuid);

                String fontSize = spinFontSize.getSelectedItem().toString();
                String noteSize = spinNoteSize.getSelectedItem().toString();
                String NoteType = spinNoteType.getSelectedItem().toString();
                String Title = etTitle.getText().toString();
                Log.d("TitleFirst",Title);

                note.setUUID(uuid);
                note.setTitle(Title);
                note.setDescription(etDescription.getText().toString());
                note.setFontsize(fontSize);
                note.setSize(noteSize);
                note.setNoteType(NoteType);

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
                note.setLastModi(dateFormat.format(date).toString());

                // int id = i.getNoteID();
                // sendMessage("pdate:Title:" + etTitle.getText().toString()+
                //        ",Description:" + etDescription.getText().toString());

                sendMessage("pdate:" + i.getUUID() + "," + i.getTitle() + ","
                        + i.getDescription() + "," + i.getNoteType() + "," + i.getSize()
                        + "," + i.getFontsize() + "," + i.getLastModi());

                Intent intent = new Intent(EditNote.this,Remote.class);
                startActivity(intent);

            }
        });
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditNote.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deleting note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Note i = (Note)getIntent().getSerializableExtra("note");
                                int id = i.getNoteID();

                                sendMessage("Delete:ID" + id);
                                Toast.makeText(EditNote.this, "Delete successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditNote.this,Remote.class);
                                startActivity(intent);

                                //System.exit(0);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
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
            if (mChatService == null)
                setupChat();
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
        }
        else if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected =="") {
            connectBT.setVisible(true);
            disconnectBT.setVisible(false);
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

        if (mChatService != null) {
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

        if (mChatService != null)
            sendMessage("DC");
        mChatService.stop();
        mBluetoothAdapter.disable();
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
        inflater.inflate(R.menu.menu_create_note, menu);
        connectBT = settingsMenu.findItem(R.id.secure_connect_scan);
        disconnectBT = settingsMenu.findItem(R.id.disconnectDevice);


        //Checking if user has previously connected to any device through this app
        SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
        String deviceName = prefs.getString("deviceName", null);
        String deviceAddress = prefs.getString("deviceAddress", null);
        hasOnCreateOptionsMenuBeenCreated = "YES";
        if (deviceName == null && deviceAddress == null) {
            if(isDeviceConnected =="") {
                connectBT.setVisible(true);
                disconnectBT.setVisible(false);
            }
        } else{
            connectBT.setVisible(false);
            disconnectBT.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:

                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(EditNote.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);

                return true;
            case R.id.disconnectDevice:
                sendMessage("DC");
                mChatService.stop();
                connectBT.setVisible(true);
                disconnectBT.setVisible(false);
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
                //tvReceived.setText(readMessage);
            }

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case ConnectivityPageService.STATE_CONNECTED:
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

                            isDeviceConnected = "";

                            if(loadingDialog!=null)
                                loadingDialog.dismiss();
                            if(hasOnCreateOptionsMenuBeenCreated =="YES" && isDeviceConnected =="") {
                                connectBT.setVisible(true);
                                disconnectBT.setVisible(false);
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
                    new CountDownTimer(100, 100) {
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
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




    public void displayLoadingDialog(String loadingType){
        loadingDialog = new Dialog(EditNote.this);
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





    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditNote.this,listofnotes.class);
        startActivity(intent);
    }


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
