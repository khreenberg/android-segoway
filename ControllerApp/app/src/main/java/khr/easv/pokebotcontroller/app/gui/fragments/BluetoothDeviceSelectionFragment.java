package khr.easv.pokebotcontroller.app.gui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import khr.easv.pokebotcontroller.app.entities.Logger;
import khr.easv.pokebotcontroller.app.gui.adapters.BluetoothDeviceListAdapter;

public class BluetoothDeviceSelectionFragment extends Fragment {

    public static final int INTENT_ID_ENABLE_BLUETOOTH = 10;

    private View _root;

    private IDeviceSelectedListener _listener;
    private BluetoothDeviceListAdapter _adapter;

    private ListView _lstExternalInputDevices;
    private TextView _txtFragmentTitle;

    /** Required empty constructor */
    public BluetoothDeviceSelectionFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _root = inflater.inflate(R.layout.fragment_bluetooth_device_selection, container, false);
        _txtFragmentTitle = (TextView) _root.findViewById(R.id.txtBluetoothSelectionFragmentTitle);
        initializeList();

        return _root;
    }

    private void initializeList() {
        requestBluetoothIfNotEnabled();
        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(deviceSet);

        _adapter = new BluetoothDeviceListAdapter(_root.getContext(), R.id.lstExternalInputDevices, deviceList);
        _lstExternalInputDevices = (ListView) _root.findViewById(R.id.lstExternalInputDevices);
        _lstExternalInputDevices.setAdapter(_adapter);

        _lstExternalInputDevices.setOnItemClickListener(new DeviceClickedListener());
    }

    private void requestBluetoothIfNotEnabled(){
        if( BluetoothAdapter.getDefaultAdapter().isEnabled() ) return;
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, INTENT_ID_ENABLE_BLUETOOTH);
    }

    private void setupBluetoothChangeListener() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(_broadcastReceiver, filter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode != INTENT_ID_ENABLE_BLUETOOTH )  return;
        if( resultCode != Activity.RESULT_OK ) {
            Logger.warn("You must activate Bluetooth to use controller features!");
            return;
        }
        initializeList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _listener = (IDeviceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IDeviceSelectedListener");
        }
        setupBluetoothChangeListener();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(_broadcastReceiver);
        _listener = null;
    }

    /** This receiver handles the event that the user disables Bluetooth while the list of devices is visible */
    private final BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if( action != BluetoothAdapter.ACTION_STATE_CHANGED ) return;
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if( state != BluetoothAdapter.STATE_OFF ) return;
            _adapter.clear();
            Logger.warn("Bluetooth was turned off!");
            requestBluetoothIfNotEnabled();
        }
    };

    private class DeviceClickedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
            _listener.onDeviceSelected(device);
        }
    }

    /** Callback interface */
    public interface IDeviceSelectedListener { void onDeviceSelected(BluetoothDevice device); }
}
