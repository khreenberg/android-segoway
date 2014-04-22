package khr.easv.pokebotbroadcaster.app.data;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.UUID;

import khr.easv.pokebotbroadcaster.app.entities.logger.Logger;

import static khr.easv.pokebotbroadcaster.app.data.BluetoothControllerHandler.ConnectionState.*;

/**
 * Inspired by
 * https://android.googlesource.com/platform/development/+/25b6aed7b2e01ce7bdc0dfa1a79eaf009ad178fe/samples/BluetoothChat/src/com/example/android/BluetoothChat/BluetoothChatService.java
 */
public class BluetoothControllerHandler {

    public static enum ConnectionState{
        NONE,
        LISTENING,
        CONNECTING,
        CONNECTED
    }

    HashSet<IControllerInputListener> _controllerListeners;

    private BluetoothAdapter _adapter;
    private ConnectionState _state;

    private AcceptThread _acceptThread;
    private ConnectedThread _connectedThread;

    private static final String NAME = "BluetoothController";

    // Random, and therefore, probably unique UUID -- Shouldn't matter much, as long as the controller uses the same UUID
    private static final UUID SERVICE_UUID = UUID.fromString("e3f67d60-ca60-11e3-a05a-0002a5d5c51b");

    public BluetoothControllerHandler() {
        _controllerListeners = new HashSet<IControllerInputListener>();
        _adapter = BluetoothAdapter.getDefaultAdapter();
        _state = NONE;
    }

    private synchronized void setState(ConnectionState state){
        _state = state;
    }

    public synchronized ConnectionState getState(){
        return _state;
    }

    public synchronized void start(){
        if( _connectedThread != null ){ _connectedThread.cancel(); _connectedThread = null; }
        if( _acceptThread != null ) return;
        _acceptThread = new AcceptThread();
        _acceptThread.start();
        setState(LISTENING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if( _connectedThread != null ) {_connectedThread.cancel(); _connectedThread = null; }
        if( _acceptThread != null ) {_acceptThread.cancel(); _acceptThread = null; }

        _connectedThread = new ConnectedThread(socket);
        _connectedThread.start();
        setState(CONNECTED);
    }

    public synchronized void stop(){
        if( _connectedThread != null ) {_connectedThread.cancel(); _connectedThread = null; }
        if( _acceptThread != null ) {_acceptThread.cancel(); _acceptThread = null; }
        setState(NONE);
    }

    public synchronized void connectionLost(){
        setState(NONE);
        start();
    }

    public void write(byte[] buffer) {
        ConnectedThread connectionCopy;
        synchronized (this) {
            if(_state != CONNECTED) return;
            connectionCopy = _connectedThread;
        }
        connectionCopy.write(buffer);
    }

    private class AcceptThread extends Thread{

        private final BluetoothServerSocket __serverSocket;

        public AcceptThread(){
            BluetoothServerSocket tmpSock = null;
            try{
                tmpSock = _adapter.listenUsingRfcommWithServiceRecord(NAME, SERVICE_UUID);
            } catch (IOException e) {
                Logger.exception("Could not listen for bluetooth connections!", e);
            }
            __serverSocket = tmpSock;
        }

        @Override
        public void run() {
            Logger.info("Waiting for controller...", this.toString());
            BluetoothSocket socket = null;
            while( _state != CONNECTED ){
                try {
                    __serverSocket.accept();
                } catch (IOException e) {
                    Logger.exception("Could not accept Bluetooth connection!", e);
                    break;
                }

                if( socket == null ) {
                    Logger.warn("Accepted controller socket is null!");
                    return;
                }

                synchronized (BluetoothControllerHandler.this){
                    switch (_state){
                        case LISTENING:
                        case CONNECTING:
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case NONE:
                        case CONNECTED:
                            try{
                                socket.close();
                            } catch (IOException e) {
                                Logger.exception("Could not close unwanted socket!", e);
                            }
                            break;
                    } // end switch( _state )
                }
            }
            Logger.info("Controller connection accepted!", "Socket: " + socket + "\nDevice: " + socket.getRemoteDevice());
        }

        public void cancel(){
            try {
                __serverSocket.close();
            } catch (IOException e) {
                Logger.exception("Could not close server socket!", e);
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
                Logger.exception("Temporary sockets were not created!", e);
            }

            __input = tmpInput;
            __output = tmpOutput;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[8]; // Each float is 32bit (4 byte)
            while(true) {
                try {
                    __input.read(buffer);
                    float x = ByteBuffer.wrap(buffer, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    float y = ByteBuffer.wrap(buffer, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                    notifyInputListeners(x, y);
                } catch (IOException e) {
                    Logger.exception("Controller connection lost!", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                __output.write(buffer);
            } catch (IOException e) {
                Logger.exception("Could not write to controller!", e);
            }
        }

        public void cancel() {
            try {
                __socket.close();
            } catch (IOException e) {
                Logger.exception("Could not close controller socket!", e);
            }
        }
    }

    public interface IControllerInputListener{
        void OnInput(float x, float y);
    }

    void notifyInputListeners(float x, float y) {
        for (IControllerInputListener listener : _controllerListeners) {
            listener.OnInput(x,y);
        }
    }
}
