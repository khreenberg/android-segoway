package khr.easv.pokebotbroadcaster.app.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashSet;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.data.BluetoothConnector;
import khr.easv.pokebotbroadcaster.app.data.IOrientationListener;
import khr.easv.pokebotbroadcaster.app.data.OrientationWrapper;


public class MainActivity extends ActionBarActivity implements IOrientationListener {

    public static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    // Device address MUST be uppercase hex.. :o
    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1"; // John
    //public static final String DEVICE_ADDRESS = "00:16:53:1A:D8:44"; // Bob

    private boolean _isConnected = false;

    private BluetoothAdapter _adapter;
    private BluetoothConnector _bluetooth;

    private PacketSenderThread _packetSender;
    private OrientationReaderThread _orientationReader;

    private TextView _txtAccelX, _txtAccelY, _txtAccelZ;
    private TextView _txtLog;

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
        setupBluetooth();
        setupOrientationReader();
    }

    private void initializeViews(){
        _txtAccelX = (TextView) findViewById(R.id.txtAccelX);
        _txtAccelY = (TextView) findViewById(R.id.txtAccelY);
        _txtAccelZ = (TextView) findViewById(R.id.txtAccelZ);
        _txtLog = (TextView) findViewById(R.id.txtLog);
        _txtLog.setText("");
    }

    private void setupBluetooth(){
        _adapter = BluetoothAdapter.getDefaultAdapter();
        if(!_adapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
            return;
        }
        createConnectorAndConnect();
    }

    private void setupOrientationReader(){
        _orientationReader = new OrientationReaderThread(this);
        _orientationReader.addListener(this);
        _orientationReader.start();
    }

    private void createConnectorAndConnect() {
        // TODO: Find a way to reuse the _bluetooth object
        _bluetooth = new BluetoothConnector(DEVICE_ADDRESS, _adapter);
        new BluetoothConnectionTask().execute(_bluetooth);
        if (_packetSender != null) return;
        _packetSender = new PacketSenderThread(_bluetooth);
        _packetSender.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode != INTENT_ID_ENABLE_BLUETOOTH )  return;
        if( resultCode != RESULT_OK ) {finish(); return;}
        createConnectorAndConnect();
    }

    void log(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _txtLog.append(str + "\n");
            }
        });
        Log.d("LOG", str);
    }

    @Override
    public void onOrientationRead(final double azimuth, final double pitch, final double roll) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _txtAccelX.setText("Azimuth: " + azimuth);
                _txtAccelY.setText("Pitch: " + pitch);
                _txtAccelZ.setText("Rotation: " + roll);
            }
        });

    }

    class BluetoothConnectionTask extends AsyncTask<BluetoothConnector, Void, Boolean> {

        Exception e;

        @Override
        protected void onPreExecute() {
            log("Attempting to connect via Bluetooth...");
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
            String s = "Successfully _isConnected to " + DEVICE_ADDRESS;
            String f = String.format("Could not connect to %s.\n -> %s", DEVICE_ADDRESS, e);
            String msg = wasSuccess ? s : f;
            _isConnected = wasSuccess;
            log(msg);
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
            log("Packet queued!");
            _packet = packet;
            _hasChanged = true;
        }

        @Override
        public synchronized void start() {
            super.start();
            log("PacketSenderThread started!");
        }

        @Override
        public void run() {
            while( !done ) {
                if( _connector.isReady )
                    if( !_hasChanged ) yield();
                    else send();
            }
        }

        private void send(){
            _hasChanged = false;
            try {
                _connector.sendCommand(_packet);
                log("Packet sent: " + _packet);
            } catch (IOException e) {
                log(e.toString());
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
