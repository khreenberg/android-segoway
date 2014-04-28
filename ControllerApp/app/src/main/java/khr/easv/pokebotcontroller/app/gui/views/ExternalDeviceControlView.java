package khr.easv.pokebotcontroller.app.gui.views;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.Logger;

public class ExternalDeviceControlView extends AbstractKnobView{

    private static final float AXIS_MULTIPLIER = 333;
    private float _leftX, _leftY, _rightX, _rightY, _dpadX, _dpadY;

    private InputManager _inputManager;

    public ExternalDeviceControlView(Context context) {
        super(context);
        init();
    }

    public ExternalDeviceControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExternalDeviceControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if( visibility != VISIBLE )return;
        requestFocus();
    }

    private void init() {
        setFocusableInTouchMode(true);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        _leftX = getAxis(event, MotionEvent.AXIS_X);
        _leftY = getAxis(event, MotionEvent.AXIS_Y);
        _rightX = getAxis(event, MotionEvent.AXIS_Z);
        _rightY = getAxis(event, MotionEvent.AXIS_RZ);
        _dpadX = getAxis(event, MotionEvent.AXIS_HAT_X);
        _dpadY = getAxis(event, MotionEvent.AXIS_HAT_Y);
        updateKnobPosition();
        return true;
//        return super.onGenericMotionEvent(event);
    }

    private float getAxis(MotionEvent event, int axis) {
        return event.getAxisValue(axis);
    }

    // Return input
    @Override protected float getKnobX() {
        return (_leftX + _rightX + _dpadX) * AXIS_MULTIPLIER;
    }
    @Override protected float getKnobY() {
        return (_leftY + _rightY + _dpadY) * AXIS_MULTIPLIER;
    }

    // Make the knob blue by default
    @Override protected int getDefaultKnobDrawableID() { return R.drawable.knob_blue; }

/*    @Override
    public void onInputDeviceAdded(int deviceId) {
        Logger.debug("Input device added! ID = " + deviceId);
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        Logger.debug("Input device removed! ID = " + deviceId);
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        Logger.debug("Input device changed! ID = " + deviceId);
    }*/
}
