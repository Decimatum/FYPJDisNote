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

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l30605.fypjdisnote.Entity.Note;
import com.example.l30605.fypjdisnote.Logic.ConnectivityPageService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CreateNote extends ActionBarActivity {
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



    private ConnectedThread mConnectedThread;

    TextView tvReceived,tvPointerColor,tvConnect;

    LinearLayout toggleButtonLL;

    RelativeLayout LLSelect;

    Switch toggle;
    SharedPreferences.Editor editor;
    String pairedDeviceAddress,hasOnCreateOptionsMenuBeenCreated,isDeviceConnected = "", isMotionControlSelected="";
    Menu settingsMenu;
    MenuItem connectBT, disconnectBT;
    Dialog loadingDialog;

    Button btnCreate,btnAddImage,btnCreateImage;

    ImageView ivImage;


    EditText etTitle,etDescription,etImageTitle;

    Spinner spinFontSize, spinNoteSize, spinNoteType,spinType;

    LinearLayout LLText,LLImage;

    private static final int SELECT_PICTURE = 6;

    private String selectedImagePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        try {
            if(mBluetoothAdapter == null){}
            else {
                // Register for broadcasts on BluetoothAdapter state change
                this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            }
        }
        catch(IllegalArgumentException ex){

            String str = ex.getMessage();

        }
        editor = getSharedPreferences("RMCSP", MODE_PRIVATE).edit();
        // Get local Bluetooth adapter
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // If the adapter is null, then Bluetooth is not supported
            if (mBluetoothAdapter == null) {
                //Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                //finish();
                //return;
            }
            else {
                if (mBluetoothAdapter.isEnabled()) {
                    SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
                    String deviceName = prefs.getString("deviceName", null);
                    String deviceAddress = prefs.getString("deviceAddress", null);

                    if (deviceName == null && deviceAddress == null) {
                        if (hasOnCreateOptionsMenuBeenCreated == "YES" && isDeviceConnected == "") {
                            connectBT.setVisible(true);
                            disconnectBT.setVisible(false);
                        }
                    } else if (deviceName != null && deviceAddress != null) {
                        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                        if (mChatService == null) setupChat();
                        mChatService.connect(device, true);
                        //Toast.makeText(getApplicationContext(),deviceName, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(),deviceAddress, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch(Exception ex){
            String message = ex.getMessage();
        }
        // Set up the window layout
        setContentView(R.layout.activity_create_note);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        final NoteApplication na = NoteApplication.getInstance();
        btnCreate = (Button)findViewById(R.id.buttonCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");

                String title = etTitle.getText().toString();
                String description = etDescription.getText().toString();
                String fontSize = spinFontSize.getSelectedItem().toString();
                String noteSize = spinNoteSize.getSelectedItem().toString();
                String NoteType = spinNoteType.getSelectedItem().toString();
                UUID uuid = UUID.randomUUID();

                String id = uuid.toString();



              //  Note n = new Note(etTitle.getText().toString(),etDescription.getText().toString()
              //          ,dateFormat.format(date).toString(),0,0,BGcolor,TextColor,0,0);

                Note n = new Note(title,description,dateFormat.format(date).toString(),
                        fontSize,dateFormat.format(date).toString(),noteSize,id,NoteType);

                Long b = na.addToDatabase(n, CreateNote.this);
                Toast.makeText(CreateNote.this,"Note has been created", Toast.LENGTH_SHORT).show();

                if(isDeviceConnected.equals("YES")) {

                    sendMessage("Note:" + n.getUUID() + "," + n.getTitle() + ","
                            + n.getDescription() + "," + n.getNoteType() + "," + n.getSize()
                            + "," + n.getFontsize() + "," + n.getLastModi());

                    Intent intent = new Intent(CreateNote.this, Remote.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(CreateNote.this, listofnotes.class);
                    startActivity(intent);
                }
            }
        });

        spinFontSize = (Spinner)findViewById(R.id.spinFontSize);
        ArrayAdapter<CharSequence> FontSizeArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Size, android.R.layout.simple_spinner_item);
        FontSizeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFontSize.setAdapter(FontSizeArrayAdapter);

        spinNoteSize = (Spinner)findViewById(R.id.spinNoteSize);
        spinNoteSize.setAdapter(FontSizeArrayAdapter);

        spinNoteType = (Spinner)findViewById(R.id.spinNoteType);
        ArrayAdapter<CharSequence> BackgroundArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.Background, android.R.layout.simple_spinner_item);
        BackgroundArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNoteType.setAdapter(BackgroundArrayAdapter);


        spinNoteType.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(CreateNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;
        spinNoteSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(CreateNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;

        spinFontSize.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(CreateNote.this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        }) ;

        spinType = (Spinner)findViewById(R.id.spinType);
        ArrayAdapter<CharSequence> TypeArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        TypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(TypeArrayAdapter);

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                LLImage = (LinearLayout) findViewById(R.id.LLImage);
                LLText = (LinearLayout) findViewById(R.id.LLText);
                String a = parentView.getItemAtPosition(position).toString();
                if (a.equals("Image")) {
                    LLImage.setVisibility(View.VISIBLE);
                    LLText.setVisibility(View.GONE);
                } else {
                    LLImage.setVisibility(View.INVISIBLE);
                    LLText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        etImageTitle = (EditText)findViewById(R.id.etImageTitle);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        btnAddImage = (Button)findViewById(R.id.btnAddImage);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });

        btnCreateImage = (Button)findViewById(R.id.btnCreateImage);
        btnCreateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note n = new Note();
                etImageTitle = (EditText)findViewById(R.id.etImageTitle);
                n.setTitle(etImageTitle.getText().toString());

                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm");
                String createDate = dateFormat.format(date).toString();
                n.setCreateDate((createDate));
                n.setLastModi(createDate);
                n.setDescription("");
                n.setImage(selectedImagePath);

                UUID uuid = UUID.randomUUID();

                String id = uuid.toString();
                n.setUUID(id);
                n.setSize("Small");
                n.setFontsize("Small");
                n.setNoteType("RustGold");

                Log.d("Notey", n.getTitle());
                Log.d("Notey",n.getDescription());
                Log.d("Notey",n.getUUID());
                Log.d("Notey",n.getLastModi());
                Log.d("Notey",n.getImage());
                Log.d("Notey",n.getCreateDate());
                Log.d("Notey",n.getFontsize());
                Log.d("Notey",n.getSize());
                Log.d("Notey",n.getNoteType());

                Long b = na.addToDatabase(n, CreateNote.this);
                Toast.makeText(CreateNote.this,"Note has been created", Toast.LENGTH_SHORT).show();

                if(isDeviceConnected.equals("YES")) {

                    File sourceFile = new File(selectedImagePath);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(sourceFile));
                    startActivity(intent);


                    //Bitmap bitmap = ((BitmapDrawable)ivImage.getDrawable()).getBitmap();
                    //Bitmap resized;
//                    resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*0.2)
//                            , (int)(bitmap.getHeight()*0.2), true);
//
//                    String str = BitMapToString(resized);
//
//                    String message = "ImageNote:" + n.getUUID() + "," + n.getTitle() + ","
//                            + n.getDescription() + "," + n.getNoteType() + "," + n.getSize()
//                            + "," + n.getFontsize() + "," + n.getLastModi() + "," + str;
//
//                    sendMessage(message);
//                    int length = message.length();

                  //  Log.e("ImageNoteLength:",String.valueOf(length));
//                    if(length > 500){
//                        double doubleLength = Math.ceil(length/500);
//                        Log.e("ImageCount",String.valueOf(doubleLength));
//                        int noOfMessages = (int)doubleLength;
//                        String[] subMessages = new String[noOfMessages];
//                        for (int i = 0 ; i < noOfMessages;i++){
//                            Log.e("ForHelo:",String.valueOf(i+1));
//                            subMessages[i] = "ImageNote:Part:"+ String.valueOf(i+1) + "/" + noOfMessages + "," + message.substring(0,500);
//                            Log.e("ImageNote:",subMessages[i]);
//                            sendMessage(subMessages[i]);
//                            message = message.replace(message.substring(0,500),"");
//                        }
//
//
//                    }
                    //Log.e("MessageNoteHello", message);

                    //sendMessage(message);

                  //  Intent intent2 = new Intent(CreateNote.this, Remote.class);
                  //  startActivity(intent2);
                }
                else{
                    Intent intent = new Intent(CreateNote.this, listofnotes.class);
                    startActivity(intent);
                }
            }
        });

    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult

        if(mBluetoothAdapter == null){}
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            } else {
                if (mChatService == null)
                    setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        try {
            if(mBluetoothAdapter == null) {

            }
            else {
                if (!mBluetoothAdapter.isEnabled()) {
                    //Turn on bluetooth
                    mBluetoothAdapter.getDefaultAdapter().enable();
                }

                if (D) Log.e(TAG, "+ ON RESUME +");
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
                if (hasOnCreateOptionsMenuBeenCreated == "YES" && isDeviceConnected == "YES") {
                    connectBT.setVisible(false);
                    disconnectBT.setVisible(true);
                } else if (hasOnCreateOptionsMenuBeenCreated == "YES" && isDeviceConnected == "") {
                    connectBT.setVisible(true);
                    disconnectBT.setVisible(false);
                }
            }

        }catch(Exception ex){
            String message = ex.getMessage();
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
        if(mBluetoothAdapter == null){}
        else {
            mChatService.stop();
            if (D) Log.e(TAG, "-- ON STOP --");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBluetoothAdapter == null){}
        else {
            if (mChatService != null)
                sendMessage("DC");
            mChatService.stop();
            mBluetoothAdapter.disable();
            if (D) Log.e(TAG, "--- ON DESTROY ---");
        }
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
            case SELECT_PICTURE:
                try {
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = getPath(selectedImageUri);
                    ivImage = (ImageView) findViewById(R.id.ivImage);

                    File imgFile = new File(selectedImagePath);
                    if (imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        ImageView myImage = (ImageView) findViewById(R.id.ivImage);

                        myImage.setImageBitmap(myBitmap);

                    }
                }catch(Exception ex){
                    String message = ex.getMessage();
                }

                try {
                    if(mBluetoothAdapter == null){}
                    else {
                        // Register for broadcasts on BluetoothAdapter state change
                        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                    }
                }
                catch(IllegalArgumentException ex){

                    String str = ex.getMessage();

                }
                editor = getSharedPreferences("RMCSP", MODE_PRIVATE).edit();
                // Get local Bluetooth adapter
                try {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    // If the adapter is null, then Bluetooth is not supported
                    if (mBluetoothAdapter == null) {
                        //Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                        //finish();
                        //return;
                    }
                    else {
                        if (mBluetoothAdapter.isEnabled()) {
                            SharedPreferences prefs = getSharedPreferences("RMCSP", MODE_PRIVATE);
                            String deviceName = prefs.getString("deviceName", null);
                            String deviceAddress = prefs.getString("deviceAddress", null);

                            if (deviceName == null && deviceAddress == null) {
                                if (hasOnCreateOptionsMenuBeenCreated == "YES" && isDeviceConnected == "") {
                                    connectBT.setVisible(true);
                                    disconnectBT.setVisible(false);
                                }
                            } else if (deviceName != null && deviceAddress != null) {
                                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                                if (mChatService == null) setupChat();
                                mChatService.connect(device, true);
                                //Toast.makeText(getApplicationContext(),deviceName, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(),deviceAddress, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }catch(Exception ex){
                    String message = ex.getMessage();
                }

                break;
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
                serverIntent = new Intent(CreateNote.this, DeviceListActivity.class);
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
                tvReceived.setText(readMessage);
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

    float x1,x2;
    float y1, y2;
    float diffx, diffy;

    public boolean onTouchEvent(MotionEvent touchevent)
    {
        switch (touchevent.getAction())
        {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();

                diffx = x2-x1;
                diffy = y2-y1;

                //if left to right sweep event on screen
                if (x1 < x2 && Math.abs(diffy) < Math.abs(diffx))
                {
                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if right to left sweep event on screen
                if (x2 < x1 && Math.abs(diffy) < Math.abs(diffx))
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                }

                // if UP to Down sweep event on screen
                if (y1 < y2 && Math.abs(diffx) < Math.abs(diffy))
                {
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }

                //if Down to UP sweep event on screen
                if (y2 < y1 && Math.abs(diffy) < Math.abs(diffx))
                {
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return false;
    }


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
        loadingDialog = new Dialog(CreateNote.this);
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
        Intent intent = new Intent(CreateNote.this,home.class);
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
            byte[] buffer = new byte[8192];
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