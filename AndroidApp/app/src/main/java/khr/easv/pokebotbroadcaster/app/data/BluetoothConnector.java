package khr.easv.pokebotbroadcaster.app.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothConnector {

    // Magic string from documentation:
    // http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord(java.util.UUID)
    public static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";

    String _deviceAddress;
    BluetoothAdapter _adapter;
    BluetoothSocket _socket;
    BluetoothDevice _device;

    DataOutputStream _output;


    public BluetoothConnector( String deviceAddress, BluetoothAdapter adapter ){
        _adapter = adapter;
        _deviceAddress = deviceAddress;
        _device = adapter.getRemoteDevice(deviceAddress);
    }

    public void connect() throws IOException {
        _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
        _socket.connect();
        _output = new DataOutputStream(_socket.getOutputStream());
    }

    public void sendCommand( short command ) throws IOException {
        _output.writeInt(command);
        _output.flush();
    }

    public String readMessage() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader input = new InputStreamReader(_socket.getInputStream());
        char[] b = new char[128];
        int byteCount  = input.read(b);
        sb.append(b, 0, byteCount);
        return sb.toString();
    }

}
