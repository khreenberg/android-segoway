package khr.easv.pokebotbroadcaster.app.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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
//    public static final String DEVICE_ADDRESS = "00:16:53:1A:05:C1"; // John
    public static final String DEVICE_ADDRESS = "00:16:53:1A:D8:44"; // Bob

    static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    private boolean connected = false;

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
        sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
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
        if( bluetooth == null ) bluetooth = new BluetoothConnector(DEVICE_ADDRESS, adapter);
        new BluetoothConnectionTask().execute(bluetooth);
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
        float[] orientation = getOrientation(event);
        if( orientation == null ) return;
        DecimalFormat df = new DecimalFormat("#0.00");
        double  azimuth = Math.toDegrees(orientation[0]),
                pitch   = Math.toDegrees(orientation[1]),
                roll    = Math.toDegrees(orientation[2]);
        txtAccelX.setText("Azimuth: " + df.format(azimuth));
        txtAccelY.setText("Pitch: " + df.format(pitch));
        txtAccelZ.setText("Roll: " + df.format(roll));
        handleSensorData(azimuth, pitch, roll);
    }

    void handleSensorData(double azimuth, double pitch, double roll){
        // TODO: Thread this out if necessary
        if( !connected ) return;
        try{
            int packet = BalanceManager.createPacketFromOrientation(azimuth, pitch, roll);
            bluetooth.sendCommand(packet);
        }catch (IOException e){
            log("Error while sending packet: " + e);
        }
    }

    float[] accelerometerValues = new float[3];
    float[] magnetometerValues = new float[3];
    float[] getOrientation(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            copyArrayValues(accelerometerValues, event.values);
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            copyArrayValues(magnetometerValues, event.values);
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

    void copyArrayValues( float[] a, float[] b){
        a[0] = b[0];
        a[1] = b[1];
        a[2] = b[2];
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    void log(String str){
        txtLog.append(str + "\n");
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
            String s = "Successfully connected to " + DEVICE_ADDRESS;
            String f = String.format("Could not connect to %s.\n%s", DEVICE_ADDRESS, e);
            String msg = wasSuccess ? s : f;
            connected = wasSuccess;
            log(msg);
        }
    }
}
