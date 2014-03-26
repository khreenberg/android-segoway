package khr.easv.pokebotbroadcaster.app.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BluetoothConnector {

    // Magic "number" from documentation:
    // http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord(java.util.UUID)
    public static final String UUIDString = "00001101-0000-1000-8000-00805F9B34FB";

    String deviceAddress;
    BluetoothAdapter adapter;
    BluetoothSocket socket;
    BluetoothDevice device;

    public BluetoothConnector( String deviceAddress ){
        adapter = BluetoothAdapter.getDefaultAdapter();
        this.deviceAddress = deviceAddress;
        this.device = adapter.getRemoteDevice(deviceAddress);
    }

    public void connect() throws IOException {
        this.socket = device.createRfcommSocketToServiceRecord(UUID.fromString(UUIDString));
        this.socket.connect();
    }

    public void sendMessage( String message ) throws IOException {
        OutputStreamWriter output = null;

        output = new OutputStreamWriter(socket.getOutputStream());
        output.write(message);
        output.flush();
    }

    public String readMessage() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader input = null;

        input = new InputStreamReader(socket.getInputStream());
        char[] b = new char[128];
        int byteCount  = input.read(b);
        sb.append(b, 0, byteCount);
        return sb.toString();
    }
}
