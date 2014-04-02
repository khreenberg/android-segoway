package khr.easv.pokebotbroadcaster.app.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.text.DecimalFormat;

import khr.easv.pokebotbroadcaster.app.R;
import khr.easv.pokebotbroadcaster.app.data.BluetoothConnector;
import khr.easv.pokebotbroadcaster.app.logic.BalanceManager;


public class MainActivity extends ActionBarActivity implements SensorEventListener {

    // Device address MUST be uppercase hex.. :o
    //public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1"; // John
    public static final String DEVICE_ADDRESS = "00:16:53:1A:D8:44"; // Bob

    static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

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
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
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

    void initialize(){
        initializeViews();
        setupBluetooth();
        setupSensors();
    }

    void initializeViews(){
        txtAccelX = (TextView) findViewById(R.id.txtAccelX);
        txtAccelY = (TextView) findViewById(R.id.txtAccelY);
        txtAccelZ = (TextView) findViewById(R.id.txtAccelZ);
        txtLog    = (TextView) findViewById(R.id.txtLog);
        txtLog.setText("");
    }

    void setupSensors(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer  = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_FASTEST);
    }

    void setupBluetooth(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(!adapter.isEnabled()){
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
            return;
        }
        // createConnectorAndConnect();
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
        // createConnectorAndConnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] orientation = getOrientation(event);
        if( orientation == null ) return;
        DecimalFormat df = new DecimalFormat("#0.00");
        double  azimuth = Math.toDegrees(orientation[0]),
                pitch   = Math.toDegrees(orientation[1]),
                roll    = Math.toDegrees(orientation[2]);
        txtAccelX.setText("Azimuth: " + df.format(azimuth));
        txtAccelY.setText("Pitch: " + df.format(pitch));
        txtAccelZ.setText("Roll: " + df.format(roll));

//        if( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
//            float x = event.values[0],
//                    y = event.values[1],
//                    z = event.values[2];
//            txtAccelX.setText("X acceleration: " + x);
//            txtAccelY.setText("Y acceleration: " + y);
//            txtAccelZ.setText("Z acceleration: " + z);
//            if (bluetooth == null) return;
//            handleAccelerometerData(x, y, z);
//        }
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

    float[] accelerometerValues;
    float[] magnetometerValues;
    float[] getOrientation(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            accelerometerValues = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magnetometerValues = event.values;
        if (accelerometerValues != null && magnetometerValues != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accelerometerValues, magnetometerValues);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                return orientation;
            }
        }
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    void log(String str){
        txtLog.append(str + "\n");
    }
}
