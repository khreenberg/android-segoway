package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import khr.easv.pokebotcontroller.app.R;
import khr.easv.pokebotcontroller.app.gui.adapters.BluetoothDeviceListAdapter;

public class BluetoothDeviceSelectionFragment extends Fragment {

    public static final String BUNDLE_KEY_DEVICES = "bundle key external input devices";
    private View _root;

    private OnDeviceSelectedListener _listener;

    private ListView _lstExternalInputDevices;
    private TextView _txtFragmentTitle;

    public BluetoothDeviceSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_bluetooth_device_selection, container, false);

        _txtFragmentTitle = (TextView) _root.findViewById(R.id.txtBluetoothSelectionFragmentTitle);

        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(deviceSet);

        BluetoothDeviceListAdapter adapter = new BluetoothDeviceListAdapter(_root.getContext(), R.id.lstExternalInputDevices, deviceList);
        _lstExternalInputDevices = (ListView) _root.findViewById(R.id.lstExternalInputDevices);
        _lstExternalInputDevices.setAdapter(adapter);

        _lstExternalInputDevices.setOnItemClickListener(new DeviceClickedListener());

        // Inflate the layout for this fragment
        return _root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _listener = (OnDeviceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDeviceSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _listener = null;
    }

    public String getTitle(){
        return _txtFragmentTitle.getText().toString();
    }

    public void setTitle(final String title){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                _txtFragmentTitle.setText(title);
            }
        });
    }

    private class DeviceClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
            _listener.OnDeviceSelected(device);
        }
    }

    public interface OnDeviceSelectedListener{
        void OnDeviceSelected(BluetoothDevice device);
    }
}
