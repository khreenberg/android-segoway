package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.entities.Logger;
import khr.easv.pokebotcontroller.app.gui.views.ImageButtonIgnoreTransparency;

public class ButtonControlFragment extends Fragment {

    private View _root;

    private ImageButtonIgnoreTransparency
            _btnTurnLeft,       _btnMoveForward,    _btnTurnRight,
            _btnRotateLeft,     _btnCenter,         _btnRotateRight,
            _btnBackLeft,       _btnMoveBack,       _btnBackRight;

    private HashSet<ImageButtonIgnoreTransparency> _buttons;

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
        initializeButtons();
        attachClickListener();
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
        for( ImageButtonIgnoreTransparency b : _buttons) b.setOnClickListener(listener);
    }

    private class ControlClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( v == null ) {
                return; // Shouldn't happen, but it makes the following lines look prettier. :P
            }else if(v == _btnTurnLeft) {
                Logger.debug("btnTurnLeft clicked!");
            }else if(v == _btnMoveForward) {
                Logger.debug("btnMoveForward clicked!");
            }else if(v == _btnTurnRight) {
                Logger.debug("btnTurnRight clicked!");
            }else if(v == _btnRotateLeft) {
                Logger.debug("btnRotateLeft clicked!");
            }else if(v == _btnCenter) {
                Logger.debug("btnCenter clicked!");
            }else if(v == _btnRotateRight) {
                Logger.debug("btnRotateRight clicked!");
            }else if(v == _btnBackLeft) {
                Logger.debug("btnBackLeft clicked!");
            }else if(v == _btnMoveBack) {
                Logger.debug("btnMoveBack clicked!");
            }else if(v == _btnBackRight){
                Logger.debug("btnBackRight clicked!");
            }
        }
    }
}
