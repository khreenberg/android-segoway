package khr.easv.pokebotbroadcaster.app.gui.activities;

import android.app.Activity;
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
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.data.BluetoothConnector;
import khr.easv.pokebotbroadcaster.app.data.IOrientationListener;
import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;
import khr.easv.pokebotbroadcaster.app.entities.LogEntry;
import khr.easv.pokebotbroadcaster.app.gui.fragments.LogFragment;
import khr.easv.pokebotbroadcaster.app.gui.Logger;
import khr.easv.pokebotbroadcaster.app.logic.BalanceManager;

public class MainActivity extends ActionBarActivity implements IOrientationListener, LogFragment.OnLogEntryClickedListener {

    public static final int INTENT_ID_ENABLE_BLUETOOTH = 10;
    public static final int MAX_BLUETOOTH_FAILURE_COUNT = 3; // Amount of IOExceptions allowed before the connection is considered broken

    // Device address MUST be uppercase hex.. :o
//    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1"; // John
    public static final String DEVICE_ADDRESS = "00:16:53:1A:D8:44"; // Bob

    private LogEntry _ioErrorEntry = null;
    private int _ioErrorCount = 0;
    private boolean _isConnected = false;

    private BluetoothAdapter _adapter;
    private BluetoothConnector _bluetooth;

    private DecimalFormat _orientationFormatter = new DecimalFormat("#.###");

    private LogFragment _logFragment;

    //    private PacketSenderThread _packetSender;
    private OrientationReaderThread _orientationReader;

    private BalanceManager _PIDController;

    private TextView _txtAzimuth, _txtPitch, _txtRoll;
    private Button _btnClearLog, _btnConnect;

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
        setupOrientationReader();
        setupPIDController();
    }

    private void setupPIDController() {
        this._PIDController = new BalanceManager();
        _PIDController.start();
    }

    private void initializeViews(){
        // Orientation text views
        _txtAzimuth = (TextView) findViewById(R.id.txtAzimuth);
        _txtPitch = (TextView) findViewById(R.id.txtPitch);
        _txtRoll = (TextView) findViewById(R.id.txtRoll);
        // Buttons
        _btnClearLog = (Button) findViewById(R.id.btnClearLog);
        _btnConnect = (Button) findViewById(R.id.btnConnect);
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

    private void setupBluetooth(){
        _adapter = BluetoothAdapter.getDefaultAdapter();
        if(!_adapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
            return;
        }
    }

    private void setupOrientationReader(){
        _orientationReader = new OrientationReaderThread(this);
        _orientationReader.addListener(this);
        _orientationReader.start();
    }

    private void bluetoothConnect() {
        if( _bluetooth == null )
            _bluetooth = new BluetoothConnector(DEVICE_ADDRESS, _adapter);
        new BluetoothConnectionTask().execute(_bluetooth);
//        if (_packetSender != null) return;
//        _packetSender = new PacketSenderThread(_bluetooth);
//        _packetSender.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode != INTENT_ID_ENABLE_BLUETOOTH )  return;
        if( resultCode != RESULT_OK ) {finish(); return;}
        _PIDController.start();
        bluetoothConnect();

    }

    @Override
    public void onOrientationRead(final double azimuth, final double pitch, final double roll) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _txtAzimuth.setText(_orientationFormatter.format(azimuth));
                _txtPitch.setText(_orientationFormatter.format(pitch));
                _txtRoll.setText(_orientationFormatter.format(roll));
//                if (_isConnected) try {
//                    _bluetooth.sendCommand(_PIDController.createPacketFromOrientation(azimuth, pitch, roll));
//                } catch (IOException e) {
//                    handlePacketIOException(e);
//                }
////                _packetSender.sendPacket(BalanceManager.createPacketFromOrientation(azimuth,pitch,roll));
            }
        });
    }

    private void handlePacketIOException(IOException e){
        if( _ioErrorEntry == null ) {
            _ioErrorEntry = new LogEntry("Couldn't send packet. Attempts: ", "", LogEntry.LogTag.ERROR );
            Logger.log(_ioErrorEntry);
        }
//        Logger.exception("Couldn't send packet", e);
        _ioErrorCount++;
        _ioErrorEntry.setTitle("Couldn't send packet. Lost packets: " + _ioErrorCount);
        if( _ioErrorCount >= MAX_BLUETOOTH_FAILURE_COUNT ){
            _isConnected = false;
            _ioErrorEntry = null;
            Logger.warn("Max lost packets allowed count reached!", "Packets lost: " + _ioErrorCount);
        }
        _btnConnect.setEnabled(!_isConnected);
        updateLogList();
    }

    @Override
    public void onLogEntryClicked(LogEntry entry) {
        if( entry.getDetails().isEmpty() ) return;
        Toast.makeText(this, entry.getDetails(), Toast.LENGTH_SHORT).show();
    }

    private void updateLogList() {
        _logFragment.getListView().invalidateViews();
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
            _isConnected = wasSuccess;
            _ioErrorCount = 0;
            _btnConnect.setEnabled(!_isConnected);
        }
    }

    class PacketSenderThread extends Thread{

        boolean done = false;

        BluetoothConnector _connector;

        volatile boolean _hasChanged;
        volatile int _packet;

        public PacketSenderThread(BluetoothConnector connector) {
            _hasChanged = false;
            _packet = 0;

            _connector = connector;
        }

        public void sendPacket(int packet){
            _packet = packet;
            _hasChanged = true;
        }

        @Override
        public synchronized void start() {
            super.start();
            Logger.debug("PacketSenderThread started!");
        }

        @Override
        public void run() {
            while( !done ) {
                if( _connector._isReady)
                    if( !_hasChanged ) yield();
                    else send();
            }
        }

        private void send(){
            _hasChanged = false;
            try {
                _connector.sendCommand(_packet);
                Logger.debug("Packet sent: " + _packet);
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
    }

    class OrientationReaderThread extends Thread{

        public static final int ORIENTATION_READ_INTERVAL = 100; // milliseconds

        private OrientationWrapper _wrapper;

        private boolean _isEnabled;
        private boolean _isAlive;

        private HashSet<IOrientationListener> _listeners;

        public OrientationReaderThread(Activity activity){
            _isEnabled = false;
            _isAlive = false;
            _wrapper = new OrientationWrapper(activity);
        }

        @Override
        public synchronized void start() {
            _isAlive = true;
            enable();
            super.start();
        }

        @Override
        public void run() {
            while(_isAlive){
                while( !_isEnabled ) yield();
                notifyListeners(_wrapper.getOrientation());
                try {
                    Thread.sleep(ORIENTATION_READ_INTERVAL);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }

        public void kill(){
            _isAlive = false;
        }

        public void enable(){
            if( _isEnabled ) return;
            _isEnabled = true;
            _wrapper.startListening();
        }

        public void disable(){
            if( !_isEnabled ) return;
            _isEnabled = false;
            _wrapper.stopListening();
        }

        public void addListener(IOrientationListener listener){
            if( _listeners == null ) _listeners = new HashSet<IOrientationListener>();
            _listeners.add(listener);
        }

        public void removeListener(IOrientationListener listener){
            if( _listeners == null ) return;
            _listeners.remove(listener);
            if( _listeners.isEmpty() ) _listeners = null; // Try to conserve memory
        }

        private void notifyListeners(float[] orientation){
            double azimuth = Math.toDegrees(orientation[0]);
            double pitch = Math.toDegrees(orientation[1]);
            double roll = Math.toDegrees(orientation[2]);
            for( IOrientationListener l : _listeners )
                l.onOrientationRead(azimuth, pitch, roll);
        }

    }
}
