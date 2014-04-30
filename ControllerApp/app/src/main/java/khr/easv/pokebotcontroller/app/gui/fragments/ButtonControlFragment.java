package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.views.ImageButtonIgnoreTransparency;

public class ButtonControlFragment extends Fragment {

    // Constants for assigning values to the buttons
    private static final float NO_INPUT = 0.000f;
    private static final float STRAIGHT = 1.000f;
    private static final float DIAGONAL = 0.707f;

    private View _root;

    private ImageButtonIgnoreTransparency
            _btnTurnLeft,       _btnMoveForward,    _btnTurnRight,
            _btnRotateLeft,     _btnCenter,         _btnRotateRight,
            _btnBackLeft,       _btnMoveBack,       _btnBackRight;

    private HashSet<ImageButtonIgnoreTransparency> _buttons;
    private HashSet<IButtonControlListener> _listeners;

    private float[] _xValues, _yValues;

    /** Required empty public constructor */
    public ButtonControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_button_control, container, false);
        initialize();
        // Inflate the layout for this fragment
        return _root;
    }

    private void initialize(){
        _xValues = new float[9];
        _yValues = new float[9];
        initializeButtons();
        attachClickListener();
        makeActivityListen();
    }

    private void makeActivityListen() {
        try{ addListener((IButtonControlListener) getActivity()); }
        catch (ClassCastException e) { /* We could handle this, but we won't */ }
    }

    private void initializeButtons(){
        _buttons = new HashSet<ImageButtonIgnoreTransparency>(9);
        _buttons.add(_btnTurnLeft       = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlTurnLeft));
        _buttons.add(_btnMoveForward    = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlMoveForward));
        _buttons.add(_btnTurnRight      = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlTurnRight));
        _buttons.add(_btnRotateLeft     = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlRotateLeft));
        _buttons.add(_btnCenter         = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlCenter));
        _buttons.add(_btnRotateRight    = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlRotateRight));
        _buttons.add(_btnBackLeft       = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlBackLeft));
        _buttons.add(_btnMoveBack       = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlMoveBack));
        _buttons.add(_btnBackRight      = (ImageButtonIgnoreTransparency) _root.findViewById(R.id.btnControlBackRight));
    }

    private void attachClickListener(){
        ControlClickListener listener = new ControlClickListener();
        for( ImageButtonIgnoreTransparency b : _buttons) b.setOnTouchListener(listener);
    }

    private class ControlClickListener implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if( event.getAction() == MotionEvent.ACTION_DOWN ) setValues(v);
            if( event.getAction() == MotionEvent.ACTION_UP)  clearValues(v);
            notifyListeners();
            return true;
        }

        public void setValues(View v) {
            if( v == _btnTurnLeft )     {_xValues[0] = -DIAGONAL; _yValues[0] =  DIAGONAL;}
            if( v == _btnMoveForward )  {_xValues[1] =  NO_INPUT; _yValues[1] =  STRAIGHT;}
            if( v == _btnTurnRight )    {_xValues[2] =  DIAGONAL; _yValues[2] =  DIAGONAL;}
            if( v == _btnRotateLeft )   {_xValues[3] = -STRAIGHT; _yValues[3] =  NO_INPUT;}
            if( v == _btnCenter )       {_xValues[4] =  NO_INPUT; _yValues[4] =  NO_INPUT;}
            if( v == _btnRotateRight )  {_xValues[5] =  STRAIGHT; _yValues[5] =  NO_INPUT;}
            if( v == _btnBackLeft )     {_xValues[6] = -DIAGONAL; _yValues[6] = -DIAGONAL;}
            if( v == _btnMoveBack )     {_xValues[7] =  NO_INPUT; _yValues[7] = -STRAIGHT;}
            if( v == _btnBackRight )    {_xValues[8] =  DIAGONAL; _yValues[8] = -DIAGONAL;}
        }

        private void clearValues(View v) {
            if( v == _btnTurnLeft )     {_xValues[0] = -NO_INPUT; _yValues[0] =  NO_INPUT;}
            if( v == _btnMoveForward )  {_xValues[1] =  NO_INPUT; _yValues[1] =  NO_INPUT;}
            if( v == _btnTurnRight )    {_xValues[2] =  NO_INPUT; _yValues[2] =  NO_INPUT;}
            if( v == _btnRotateLeft )   {_xValues[3] = -NO_INPUT; _yValues[3] =  NO_INPUT;}
            if( v == _btnCenter )       {_xValues[4] =  NO_INPUT; _yValues[4] =  NO_INPUT;}
            if( v == _btnRotateRight )  {_xValues[5] =  NO_INPUT; _yValues[5] =  NO_INPUT;}
            if( v == _btnBackLeft )     {_xValues[6] = -NO_INPUT; _yValues[6] = -NO_INPUT;}
            if( v == _btnMoveBack )     {_xValues[7] =  NO_INPUT; _yValues[7] = -NO_INPUT;}
            if( v == _btnBackRight )    {_xValues[8] =  NO_INPUT; _yValues[8] = -NO_INPUT;}
        }
    }

    public interface IButtonControlListener{
        void onButtonControl(float x, float y);
    }

    public void addListener(IButtonControlListener listener){
        if( _listeners == null ) _listeners = new HashSet<IButtonControlListener>();
        _listeners.add(listener);
    }

    public void removeListener(IButtonControlListener listener) {
        _listeners.remove(listener);
        if( _listeners.isEmpty() ) _listeners = null;
    }

    private void notifyListeners() {
        float x = getArraySum(_xValues);
        float y = getArraySum(_yValues);
        for (IButtonControlListener listener : _listeners)  listener.onButtonControl(x,y);
    }

    private float getArraySum(float[] b) {
        float sum = 0.0f;
        for (int i = 0; i < b.length; i++) sum += b[i];
        return sum;
    }
}
