package khr.easv.pokebotcontroller.app.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.views.AbstractKnobView;
import khr.easv.pokebotcontroller.app.gui.views.AccelerometerControlView;

public class AccelerometerControlFragment extends Fragment {

    /** Required empty constructor */
    public AccelerometerControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View _root = inflater.inflate(R.layout.fragment_accelerometer_control, container, false);

        AccelerometerControlView _jstkAccelerometer =
                (AccelerometerControlView) _root.findViewById(R.id.jstkAccelerometer);
        _jstkAccelerometer.addListener((AbstractKnobView.KnobUpdateListener) getActivity());

        return _root;
    }
}
