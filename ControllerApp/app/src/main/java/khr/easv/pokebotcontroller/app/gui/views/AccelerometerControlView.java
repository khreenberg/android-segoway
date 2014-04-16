package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.Logger;

public class AccelerometerControlView extends AbstractKnobView implements SensorEventListener{

    private static final float GRAVITY_CONSTANT = 9.8f;

    private SensorManager _sensorManager;
    private Sensor _accelerometer;

    private float _x, _y;

    public AccelerometerControlView(Context context) {
        super(context);
        init();
    }

    public AccelerometerControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AccelerometerControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        _sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected int getKnobX() {
        return (int) (_x * (radius / GRAVITY_CONSTANT));
    }

    @Override
    protected int getKnobY() {
        return (int) (_y * (radius / GRAVITY_CONSTANT));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        _x = -event.values[0];
        _y = event.values[1];
        updateKnobPosition();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Do nothing */ }

    @Override
    protected int getDefaultKnobDrawableID() {
        return R.drawable.knob_green;
    }

    // This is run when the containing fragment is replaced by another
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        _sensorManager.unregisterListener(this);
    }
}
