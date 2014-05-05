package khr.easv.pokebotbroadcaster.app.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BluetoothConnector {

    // Magic string from documentation:
    // http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord(java.util.UUID)
    public static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";

    private BluetoothDevice _device;
    private DataOutputStream _output;

    public BluetoothConnector( String deviceAddress, BluetoothAdapter adapter ){
        _device = adapter.getRemoteDevice(deviceAddress);
    }

    public void connect() throws IOException {
        BluetoothSocket socket = createSocket();
        socket.connect();
        _output = createOutputStream(socket);
    }

    private DataOutputStream createOutputStream(BluetoothSocket socket) throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    private BluetoothSocket createSocket() throws IOException {
        return _device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
    }

    public void sendCommand( short command ) throws IOException {
        _output.writeShort(command);
        _output.flush();
    }
}
