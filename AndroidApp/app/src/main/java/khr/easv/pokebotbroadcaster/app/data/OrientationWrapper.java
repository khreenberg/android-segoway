package khr.easv.pokebotbroadcaster.app.data;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashSet;

public class OrientationWrapper implements SensorEventListener {

    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

    /** Orientation as azimuth, pitch & roll */
    private float[] _orientation;

    // Helpers for calculating orientation
    private float[]
            _rawRotation,
            _rotationMatrix;

    // Sensor stuff
    private SensorManager _sensorManager;
    private Sensor _rotationVector;

    // Flag for checking if we're listening for sensor changes
    private boolean _isRegistered = false;

    // Listeners
    private HashSet<IOrientationListener> _listeners;

    public OrientationWrapper(Activity activity){
        setupSensors(activity);
        initializeFloatArrays();
    }

    private void initializeFloatArrays() {
        _orientation    = new float[3];
        _rawRotation    = new float[9];
        _rotationMatrix = new float[9];
    }

    private void setupSensors(Activity activity){
        _sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE); // set up the sensor manager
        _rotationVector = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); // here we define what type sensor we use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if( event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return; // check if the sensor we get is TYPE_ROTATION_VECTOR, if not just return;
        updateOrientation(event); // if it is, updateOrientation.
        notifyListeners(); // and notify listeners.
    }

    /*
     * In this method the orientation gets read from the sensor event.values and set into _rawRotation
      * variable, then it gets remapped and the orientation is calculated.
     */
    private void updateOrientation(SensorEvent event){
        SensorManager.getRotationMatrixFromVector(_rawRotation, event.values);
        SensorManager.remapCoordinateSystem(_rawRotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, _rotationMatrix); // remapping x with z to get the right degrees in a vertical position of the phone.
        SensorManager.getOrientation(_rotationMatrix, _orientation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    public void startListening(){
        if(_isRegistered) return;
        _isRegistered = true;
        _sensorManager.registerListener(this, _rotationVector, SENSOR_DELAY);
    }

    public void stopListening(){
        if( !_isRegistered) return;
        _isRegistered = false;
        _sensorManager.unregisterListener(this);
    }

    /** Returns the orientation as a float[] with azimuth, pitch & roll in radians */
    public float[] getOrientation(){ return _orientation; }

    public interface IOrientationListener {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }
    public void addListener(IOrientationListener listener){
        if( _listeners == null ) _listeners = new HashSet<IOrientationListener>();
        _listeners.add(listener);
    }

    public void removeListener(IOrientationListener listener){
        if( _listeners == null ) return;
        _listeners.remove(listener);
        if( _listeners.isEmpty() ) _listeners = null;
    }

    /** Notifies listeners of the new orientation in degrees */
    private void notifyListeners(){
        float azimuth = (float) Math.toDegrees(_orientation[0]);
        float pitch = (float) Math.toDegrees(_orientation[1]);
        float roll = (float) Math.toDegrees(_orientation[2]);

        for (IOrientationListener l : _listeners)
            l.onOrientationChanged(azimuth, pitch, roll);
    }
}
