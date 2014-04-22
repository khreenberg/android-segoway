package khr.easv.pokebotcontroller.app.gui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.adapters.BluetoothDeviceListAdapter;

public class InputDeviceSelectionFragment extends Fragment {

    public static final String BUNDLE_KEY_DEVICES = "bundle key external input devices";
    private View _root;

    private ListView _lstExternalInputDevices;

    public InputDeviceSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_input_device_selection, container, false);

        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(deviceSet);

        BluetoothDeviceListAdapter adapter = new BluetoothDeviceListAdapter(_root.getContext(), R.id.lstExternalInputDevices, deviceList);
        _lstExternalInputDevices = (ListView) _root.findViewById(R.id.lstExternalInputDevices);
        _lstExternalInputDevices.setAdapter(adapter);

        // Inflate the layout for this fragment
        return _root;
    }

}
