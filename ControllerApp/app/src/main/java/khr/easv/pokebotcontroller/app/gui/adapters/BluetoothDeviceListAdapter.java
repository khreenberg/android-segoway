package khr.easv.pokebotcontroller.app.gui.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import khr.easv.pokebotcontroller.app.R;

public class BluetoothDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> _devices;

    public BluetoothDeviceListAdapter(Context context, int resource, List<BluetoothDevice> devices) {
        super(context, resource, devices);
        _devices = devices;
    }

    @Override
    public View getView(int index, View view, ViewGroup parent) {
        if (view == null)  view = inflateView();

        BluetoothDevice device = _devices.get(index);
        TextView txtBluetoothDeviceName = (TextView) view.findViewById(R.id.txtBluetoothDeviceName);
        txtBluetoothDeviceName.setText(device.getName());

        return view;
    }

    private View inflateView() {
        View view;LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.list_item_bluetooth_device, null);
        return view;
    }
}
