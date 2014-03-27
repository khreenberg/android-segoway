package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashSet;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.Logger;
import khr.easv.pokebotcontroller.app.gui.customviews.ImageButtonIgnoreTransparency;

public class ButtonControlFragment extends Fragment {

    View root;

    ImageButtonIgnoreTransparency
        btnTurnLeft,    btnMoveForward, btnTurnRight,
        btnRotateLeft,  btnCenter,      btnRotateRight,
        btnBackLeft,    btnMoveBack,    btnBackRight;

    HashSet<ImageButtonIgnoreTransparency> buttons;

    // Required empty public constructor
    public ButtonControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_button_control, container, false);
        initialize();
        // Inflate the layout for this fragment
        return root;
    }

    void initialize(){
        initializeButtons();
        attachClickListener();
    }

    void initializeButtons(){
        buttons = new HashSet<ImageButtonIgnoreTransparency>(9);
        buttons.add(btnTurnLeft     = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlTurnLeft));
        buttons.add(btnMoveForward  = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlMoveForward));
        buttons.add(btnTurnRight    = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlTurnRight));
        buttons.add(btnRotateLeft   = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlRotateLeft));
        buttons.add(btnCenter       = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlCenter));
        buttons.add(btnRotateRight  = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlRotateRight));
        buttons.add(btnBackLeft     = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlBackLeft));
        buttons.add(btnMoveBack     = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlMoveBack));
        buttons.add(btnBackRight    = (ImageButtonIgnoreTransparency) root.findViewById(R.id.btnControlBackRight));
    }

    void attachClickListener(){
        ControlClickListener listener = new ControlClickListener();
        for( ImageButtonIgnoreTransparency b : buttons ) b.setOnClickListener(listener);
    }

    private class ControlClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if( v == null ) {
                return; // Shouldn't happen, but it makes the following lines look prettier. :P
            }else if(v == btnTurnLeft ) {
                Logger.debug("btnTurnLeft clicked!");
            }else if(v == btnMoveForward ) {
                Logger.debug("btnMoveForward clicked!");
            }else if(v == btnTurnRight ) {
                Logger.debug("btnTurnRight clicked!");
            }else if(v == btnRotateLeft ) {
                Logger.debug("btnRotateLeft clicked!");
            }else if(v == btnCenter ) {
                Logger.debug("btnCenter clicked!");
            }else if(v == btnRotateRight ) {
                Logger.debug("btnRotateRight clicked!");
            }else if(v == btnBackLeft ) {
                Logger.debug("btnBackLeft clicked!");
            }else if(v == btnMoveBack ) {
                Logger.debug("btnMoveBack clicked!");
            }else if(v == btnBackRight ){
                Logger.debug("btnBackRight clicked!");
            }
        }
    }
}
