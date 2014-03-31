package khr.easv.pokebotbroadcaster.app.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.data.BluetoothConnector;
import khr.easv.pokebotbroadcaster.app.logic.BalanceManager;
import khr.easv.pokebotbroadcaster.app.logic.PacketCreator;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    // Device address MUST be uppercase hex.. :o
    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1";

    static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    SensorManager sensorManager;
    BluetoothAdapter adapter;
    BluetoothConnector bluetooth;

    TextView txtAccelX, txtAccelY, txtAccelZ;
    TextView txtLog;

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initialize(){
        initializeViews();
        setupBluetooth();
        attachSensorListener();
    }

    void initializeViews(){
        txtAccelX = (TextView) findViewById(R.id.txtAccelX);
        txtAccelY = (TextView) findViewById(R.id.txtAccelY);
        txtAccelZ = (TextView) findViewById(R.id.txtAccelZ);
        txtLog    = (TextView) findViewById(R.id.txtLog);
        txtLog.setText("");
    }

    void attachSensorListener(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    void setupBluetooth(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
            return;
        }
        createConnectorAndConnect();
    }

    private void createConnectorAndConnect() {
        bluetooth = new BluetoothConnector(DEVICE_ADDRESS, adapter);
        try { bluetooth.connect(); log("Connected to " + DEVICE_ADDRESS);}
        catch (IOException e) { log("IOError connecting bluetooth: " + e); }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode != INTENT_ID_ENABLE_BLUETOOTH )  return;
        if( resultCode != RESULT_OK ) {finish(); return;}
        createConnectorAndConnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float   x = event.values[0],
                y = event.values[1],
                z = event.values[2];
        txtAccelX.setText("X acceleration: " + x);
        txtAccelY.setText("Y acceleration: " + y);
        txtAccelZ.setText("Z acceleration: " + z);
        if( bluetooth == null ) return;
        handleAccelerometerData(x,y,z);
    }

    void handleAccelerometerData(float x, float y, float z){
        // TODO: Thread this out if necessary
        try{
            int packet = BalanceManager.createPacketFromAcceleration(x,y,z);
            bluetooth.sendCommand(packet);
        }catch (IOException e){
            log("Error while sending packet: " + e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    void log(String str){
        txtLog.append(str + "\n");
    }
}
