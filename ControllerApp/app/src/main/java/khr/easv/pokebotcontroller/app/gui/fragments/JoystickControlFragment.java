package khr.easv.pokebotcontroller.app.gui.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class JoystickControlFragment extends Fragment {


    public JoystickControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joystick_control, container, false);
    }


}
