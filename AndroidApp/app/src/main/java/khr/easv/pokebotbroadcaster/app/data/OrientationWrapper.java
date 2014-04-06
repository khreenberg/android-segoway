package khr.easv.pokebotbroadcaster.app.data;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OrientationWrapper implements SensorEventListener{

    public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    /** Orientation as azimuth, pitch & roll */
    private float[] _orientation;

    // Helpers for calculating orientation
    private float[]
            _accelerometerValues,
            _magnetometerValues,
            _inclinationMatrix,
            _rotationMatrix;



    // The activity that is responsible for this object
    private Activity _activity;

    // Sensor stuff
    private SensorManager _sensorManager;
    private Sensor _accelerometer;
    private Sensor _magnetometer;

    // Flag for checking if we're listening for sensor changes
    private boolean _isRegistered = false;

    public OrientationWrapper(Activity activity){
        _activity = activity;
        setupSensors(_activity);

        _orientation = new float[3];
        _accelerometerValues = new float[3];
        _magnetometerValues = new float[3];
        _inclinationMatrix = new float[9];
        _rotationMatrix = new float[9];
    }

    void setupSensors(Activity activity){
        _sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _magnetometer = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        updateOrientation(event);
        if( _orientation == null ) return;
    }

    private void updateOrientation(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            copyArrayValues(_accelerometerValues, event.values);
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            copyArrayValues(_magnetometerValues, event.values);

        boolean success = SensorManager.getRotationMatrix(_rotationMatrix, _inclinationMatrix, _accelerometerValues, _magnetometerValues);
        if (!success) return;
        SensorManager.getOrientation(_rotationMatrix, _orientation);
    }

    // Method used in an attempt to reduce memory consumption
    private void copyArrayValues( float[] a, float[] b){
        int min = Math.min(a.length, b.length);
        for( int i = 0; i < min; i++ )
            a[i] = b[i];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    public void startListening(){
        if(_isRegistered) return;
        _isRegistered = true;
        _sensorManager.registerListener(this, _accelerometer, SENSOR_DELAY);
        _sensorManager.registerListener(this, _magnetometer, SENSOR_DELAY);
    }

    public void stopListening(){
        if( !_isRegistered) return;
        _isRegistered = false;
        _sensorManager.unregisterListener(this);
    }

    public float[] getOrientation(){
        return _orientation;
    }

}
