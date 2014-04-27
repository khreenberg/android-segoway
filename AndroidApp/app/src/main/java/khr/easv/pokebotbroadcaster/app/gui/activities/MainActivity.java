package khr.easv.pokebotbroadcaster.app.gui.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.data.BluetoothConnector;
import khr.easv.pokebotbroadcaster.app.data.BluetoothControllerServer;
import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;
import khr.easv.pokebotbroadcaster.app.entities.logger.LogEntry;
import khr.easv.pokebotbroadcaster.app.entities.logger.Logger;
import khr.easv.pokebotbroadcaster.app.gui.fragments.LogEntryDetailsFragment;
import khr.easv.pokebotbroadcaster.app.gui.fragments.LogFragment;
import khr.easv.pokebotbroadcaster.app.logic.BalanceManager;

import static khr.easv.pokebotbroadcaster.app.data.BluetoothControllerServer.*;
import static khr.easv.pokebotbroadcaster.app.data.OrientationWrapper.*;
import static khr.easv.pokebotbroadcaster.app.gui.fragments.LogFragment.OnLogEntryClickedListener;
import static khr.easv.pokebotbroadcaster.app.logic.BalanceManager.*;

public class MainActivity extends ActionBarActivity implements  OnLogEntryClickedListener,
                                                                OrientationListener,
                                                                PIDListener,
                                                                IControllerInputListener {

    // ID used when starting bluetooth activity for result
    public static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    // Device address must be UPPERCASE hex!
    // (Hardcoded because we know exactly what we want to connect to)
    public static final String DEVICE_ADDRESS = "00:16:53:1A:D8:44"; // Bob

    // Only update the TextViews every X milliseconds to prevent crashing
    private static final long UI_TEXT_UPDATE_INTERVAL = 100; // in millis

    private boolean _isConnectedToBrick = false;

    private BluetoothAdapter _adapter;
    private BluetoothConnector _bluetooth;

    private BluetoothControllerServer _controllerServer;

    private DecimalFormat _orientationFormatter = new DecimalFormat("#.###");
    private long _lastUiTextUpdate;

    private LogFragment _logFragment;

    private BalanceManager _balanceManager;

    private TextView _txtLeftMotor, _txtPitch, _txtRightMotor;
    private Button _btnClearLog, _btnConnect;

    private float _lastPitch = 0;
    private int _lastPowerLeft  = 0;
    private int _lastPowerRight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // if (id == R.id.action_settings) {
        //    return true;
        // }
        return super.onOptionsItemSelected(item);
    }

    private void initialize(){
        initializeViews();
        setupButtons();
        setupLogFragment();
        setupBluetooth();
        setupBalanceManager();
        setupOrientation();
    }

    private void setupOrientation() {
        OrientationWrapper orientationWrapper = new OrientationWrapper(this);
        orientationWrapper.addListener(this);
        orientationWrapper.addListener(_balanceManager);
        orientationWrapper.startListening();
    }

    private void setupBalanceManager() {
        _balanceManager = new BalanceManager();
        _balanceManager.addListener(this);
        _balanceManager.start();
    }

    private void initializeViews(){
        // Orientation text views
        _txtPitch = (TextView) findViewById(R.id.txtPitch);
        _txtLeftMotor = (TextView) findViewById(R.id.txtLeftMotor);
        _txtRightMotor = (TextView) findViewById(R.id.txtRightMotor);
        // Buttons
        _btnConnect = (Button) findViewById(R.id.btnConnect);
        _btnClearLog = (Button) findViewById(R.id.btnClearLog);
    }

    private void setupButtons(){
        _btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothConnect();
            }
        });
        _btnClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.clearEntries();
                _logFragment.clear();
                updateLogList();
            }
        });
    }

    private void setupLogFragment(){
        _logFragment = new LogFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.logFragmentContainer, _logFragment)
                .commit();
    }

    // Should check if the device supports bluetooth, but we know it does, so there's no reason
    // to spend time writing GUI for it.
    // (if the device doesn't have a bluetooth adapter, BluetoothAdapter.getDefaultAdapter() would return null)
    private void setupBluetooth(){
        _adapter = BluetoothAdapter.getDefaultAdapter();
        _controllerServer = new BluetoothControllerServer();
        if(_adapter.isEnabled()) { _controllerServer.start(); return; }
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
    }

    private void bluetoothConnect() {
        if( _bluetooth == null )
            _bluetooth = new BluetoothConnector(DEVICE_ADDRESS, _adapter);
        new BluetoothConnectionTask().execute(_bluetooth);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode != INTENT_ID_ENABLE_BLUETOOTH )  return;
        if( resultCode != RESULT_OK ) {finish(); return;}
        bluetoothConnect();
        _controllerServer.start();
    }

    private void handlePacketIOException(IOException e){
        Logger.exception("Connection lost!", e);
        _isConnectedToBrick = false;
        _btnConnect.setEnabled(true);
        updateLogList();
    }

    @Override
    public void onLogEntryClicked(LogEntry entry) {
        if( entry.getDetails().isEmpty() ) return;
        LogEntryDetailsFragment detailsFragment = new LogEntryDetailsFragment();
        Bundle fragmentExtras = new Bundle(1);
        fragmentExtras.putSerializable(LogEntryDetailsFragment.BUNDLE_KEY_ENTRY, entry);
        detailsFragment.setArguments(fragmentExtras);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.logFragmentContainer, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateLogList() {
        if( _logFragment.isVisible() )
            _logFragment.getListView().invalidateViews();
    }

    private void updateUiText(){
        long time = System.currentTimeMillis();
        long deltaTime = time - _lastUiTextUpdate;
        if (deltaTime < UI_TEXT_UPDATE_INTERVAL) return;
        _lastUiTextUpdate = time;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _txtPitch.setText(_orientationFormatter.format(_lastPitch));
                _txtLeftMotor.setText(_orientationFormatter.format(_lastPowerLeft));
                _txtRightMotor.setText(_orientationFormatter.format(_lastPowerRight));
            }
        });
    }

    @Override
    public void onOrientationChanged(final float azimuth, final float pitch, final float roll) {
        _lastPitch = pitch;
        updateUiText();
    }

    @Override
    public void onPID(short packet) {
        updateLastMotorPowerVariables(packet);
        if(!_isConnectedToBrick) return;
        try {
            _bluetooth.sendCommand(packet);
        } catch (final IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handlePacketIOException(e);
                }
            });
        }
    }

    private void updateLastMotorPowerVariables(short packet) {
        int powerLeftSign = (1 & packet) == 1 ? 1 : -1;
        _lastPowerLeft = powerLeftSign * (127 & packet >> 1);
        int powerRightSign = (1 & packet) == 1 ? 1 : -1;
        _lastPowerRight = powerRightSign * (127 & packet >> 1);
    }

    @Override
    public void OnInput(final float x, final float y) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: Make TextViews instead of logging.
                Logger.debug(String.format("Input received: (%.3f, %.3f)", x, y));
            }
        });
    }

    class BluetoothConnectionTask extends AsyncTask<BluetoothConnector, Void, Boolean> {

        Exception e;

        @Override
        protected void onPreExecute() {
            _btnConnect.setEnabled(false);
            Logger.info("Attempting to connect via Bluetooth...");
        }

        @Override
        protected Boolean doInBackground(BluetoothConnector... connectors) {
            if( connectors.length == 0 )
                throw new IllegalArgumentException("You must supply the BluetoothConnection task with a BluetoothConnector object!");
            try {
                connectors[0].connect();
            }
            catch (IOException e) {
                this.e = e; return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean wasSuccess) {
            if( wasSuccess ) Logger.info("Successfully connected to " + DEVICE_ADDRESS);
            else Logger.exception("Could not connect to " + DEVICE_ADDRESS, e);
            _isConnectedToBrick = wasSuccess;
            _btnConnect.setEnabled(!_isConnectedToBrick);
        }
    }
}
