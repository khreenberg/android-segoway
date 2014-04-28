package khr.easv.pokebotbroadcaster.app.data;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.HashSet;

public class OrientationWrapper implements SensorEventListener{

    public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_FASTEST;

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
    private HashSet<OrientationListener> _listeners;

    public OrientationWrapper(Activity activity){
        _listeners = new HashSet<OrientationListener>();
        setupSensors(activity);

        _orientation = new float[3];
        _rawRotation = new float[9];
        _rotationMatrix = new float[9];
    }

    private void setupSensors(Activity activity){
        _sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        _rotationVector = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if( event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) return;
        updateOrientation(event);
        notifyListeners();
    }

    private void updateOrientation(SensorEvent event){
        SensorManager.getRotationMatrixFromVector(_rawRotation, event.values);
        SensorManager.remapCoordinateSystem(_rawRotation, SensorManager.AXIS_X, SensorManager.AXIS_Z, _rotationMatrix);
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

    public interface OrientationListener{
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }
    public void addListener(OrientationListener listener){ _listeners.add(listener); }
    public void removeListener(OrientationListener listener){ _listeners.remove(listener); }

    /** Notifies listeners of the new orientation in degrees */
    private void notifyListeners(){
        float azimuth = (float) Math.toDegrees(_orientation[0]);
        float pitch = (float) Math.toDegrees(_orientation[1]);
        float roll = (float) Math.toDegrees(_orientation[2]);

        for (OrientationListener l : _listeners)
            l.onOrientationChanged(azimuth, pitch, roll);
    }
}
