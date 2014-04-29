package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.OnScreenJoystickView;

public class JoystickControlFragment extends Fragment {

    /** Required empty constructor */
    public JoystickControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View _root = inflater.inflate(R.layout.fragment_joystick_control, container, false);

        OnScreenJoystickView _jstkOnScreen =
                (OnScreenJoystickView) _root.findViewById(R.id.jstkOnScreen);
        _jstkOnScreen.addListener((AbstractKnobView.KnobUpdateListener) getActivity());

        return _root;
    }
}
