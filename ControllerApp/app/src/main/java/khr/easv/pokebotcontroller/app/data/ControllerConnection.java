package khr.easv.pokebotcontroller.app.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.UUID;

import khr.easv.pokebotcontroller.app.entities.Logger;

public class ControllerConnection {

    // Random, and therefore, probably unique UUID -- Shouldn't matter much, as long as the brain uses the same UUID
    private static final UUID SERVICE_UUID = UUID.fromString("e3f67d60-ca60-11e3-a05a-0002a5d5c51b");

    private HashSet<IBrainMessageListener> _listeners = null;

    private BluetoothAdapter _adapter;

    private ConnectThread _connectThread;
    private ConnectedThread _connectedThread;

    public ControllerConnection(){
        _adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized void connect(BluetoothDevice device){
        Logger.info("Connecting to brain!", device.toString());
        if( _connectThread   != null ) { _connectThread.cancel();   _connectThread   = null; }
        if( _connectedThread != null ) { _connectedThread.cancel(); _connectedThread = null; }
        _connectThread = new ConnectThread(device);
        _connectThread.start();
    }

    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Logger.info("Connected to brain!", device.toString());
//        if( _connectThread   != null ) { _connectThread.cancel();   _connectThread   = null; }
//        if( _connectedThread != null ) { _connectedThread.cancel(); _connectedThread = null; }
        _connectedThread = new ConnectedThread(socket);
        _connectedThread.start();
    }

    public synchronized void stop(){
        if( _connectThread   != null ) { _connectThread.cancel();   _connectThread   = null; }
        if( _connectedThread != null ) { _connectedThread.cancel(); _connectedThread = null; }
        Logger.info("Disconnected from Brain.");
    }

    public void write(float x, float y){
        ConnectedThread sync;
        synchronized (this) {
            if (_connectedThread == null ) return;
            sync = _connectedThread;
        }
        sync.write(x,y);
    }

    private class ConnectThread extends Thread{

        private final BluetoothDevice __device;
        private final BluetoothSocket __socket;

        private ConnectThread _connectThread;
        private ConnectedThread _connectedThread;

        public ConnectThread(BluetoothDevice device){
            __device = device;
            BluetoothSocket tmpSocket = null;
            try {
                tmpSocket = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
            }catch (IOException e){
                Logger.exception("Could not create socket to Brain!", e);
            }
            __socket = tmpSocket;
        }

        @Override
        public void run() {
            _adapter.cancelDiscovery();
            try{
                Logger.debug("Connecting socket...");
                __socket.connect();
            }catch (IOException e){
                Logger.exception("Connection failed!",e);
                try {
                    __socket.close();
                } catch (IOException e1) {
                    Logger.exception("Could not close socket after failed connection!", e1);
                }
            }
            synchronized (ControllerConnection.this){
                _connectThread = null;
            }
            connected(__socket, __device);
        }

        public void cancel() {
            try {
                __socket.close();
            } catch (IOException e) {
                Logger.exception("Could not close connect socket!", e);
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket __socket;
        private final InputStream __input;
        private final OutputStream __output;

        public ConnectedThread(BluetoothSocket socket) {
            __socket = socket;
            InputStream tmpInput = null;
            OutputStream tmpOutput = null;

            try {
                tmpInput = socket.getInputStream();
                tmpOutput = socket.getOutputStream();
            } catch (IOException e) {
                Logger.exception("Temporary streams were not created!", e);
            }

            __input = tmpInput;
            __output = tmpOutput;
        }

        @Override
        public void run() {
            Logger.debug("ConnectedThread running...");
            byte[] buffer = new byte[1024];
            int bytes;
            while(true) {
                try {
                    bytes = __input.read(buffer);
                    notifyListeners(ByteBuffer.wrap(buffer, 0, bytes).array());
                } catch (IOException e) {
                    Logger.exception("Connection lost!", e);
                    break;
                }
            }
        }

        public void write(float x, float y){
            try {
                byte[] buffer = new byte[8];
                ByteBuffer.wrap(buffer, 0, 4).putFloat(x);
                ByteBuffer.wrap(buffer, 4, 4).putFloat(y);
                __output.write(buffer);
            }catch (IOException e){
                Logger.exception("Could not send to Brain!", e);
            }
        }

        public void cancel(){
            try{
                __socket.close();
            }catch (IOException e){
                Logger.exception("Could not close connected socket!", e);
            }
        }
    }

    public interface IBrainMessageListener{
        void OnBrainMessage(byte[] msg);
    }

    public void notifyListeners(byte[] msg){
        if( _listeners == null ) return;
        for (IBrainMessageListener listener : _listeners) {
            listener.OnBrainMessage(msg);
        }
    }

    public void addBrainMessageListener(IBrainMessageListener listener) {
        if( _listeners == null ) _listeners = new HashSet<IBrainMessageListener>();
        _listeners.add(listener);
    }

    public void removeBrainMessageListener(IBrainMessageListener listener) {
        if (_listeners == null) return;
        _listeners.remove(listener);
        if( _listeners.size() == 0 ) _listeners = null;
    }
}
