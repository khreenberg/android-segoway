package khr.easv.pokebotbroadcaster.app.data;

import android.util.Log;

import java.io.IOException;
import java.util.Observable;

public class ReaderThread extends Observable{

    BluetoothConnector _connector;
    Thread _thread;
    boolean done = false;

    public ReaderThread( BluetoothConnector connector ){
        _connector = connector;
    }

    public void start(){
        _thread = new Thread(new Runnable() {
            @Override
            public void run() {
                listen();
            }
        });
        _thread.start();
    }

    void listen() {
        while( !done ) {
            String msg = "";
            try{
                msg = _connector.readMessage();
            }catch (IOException e){
                Log.d("READER_THREAD", "Couldn't read. Retrying."); continue; }
            if( msg.isEmpty() ) continue;
            Log.d("READER_THREAD", "Message read: " + msg);
            setChanged();
            notifyObservers(msg);
        }
    }
}
