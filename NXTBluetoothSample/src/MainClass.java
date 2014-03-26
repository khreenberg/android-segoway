import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class MainClass{

    private static OutputThread outputThread;
    private static InputThread inputThread;

    public static void main( String[] args ){
        setupButtons();
        DataInputStream input = null;
        DataOutputStream output = null;
        NXTConnection connection = null;
        try{
            System.out.println("Waiting for connection...");
            connection = Bluetooth.waitForConnection();
            System.out.println("Connection established!");
            connection.setIOMode( NXTConnection.RAW );
            input = connection.openDataInputStream();
            output = connection.openDataOutputStream();

            inputThread = new InputThread( input );
            outputThread = new OutputThread( output, inputThread );

            inputThread.start();
            outputThread.start();

            outputThread.sendMessage( "Hello from John!" );

            inputThread.join();
            outputThread.join();
        }
        catch( InterruptedException e ){
            dbg("Thread interrupted.\n" + e);
        }
        finally{
            closeStreams(input, output);
            if( connection != null ) connection.close();
        }
    }

    static void closeStreams(Closeable... streams){
        try{
            for( int i = 0; i < streams.length; i++ )
                if( streams[i] != null ) streams[i].close();
        }
        catch( IOException e ){
            dbg("Error closing streams.\n" + e );
        }
    }

    static void setupButtons() {
        Button.ESCAPE.addButtonListener( new ButtonListener(){
            @Override
            public void buttonReleased( Button b ){ }
            @Override
            public void buttonPressed( Button b ){ System.exit( 0 ); }
        } );
        Button.ENTER.addButtonListener( new ButtonListener(){
            @Override
            public void buttonReleased( Button b ){ }
            @Override
            public void buttonPressed( Button b ){ outputThread._messageToSend = "Hi from NXT!";}
        } );
    }

    static void dbg(String msg) {
        outputThread.sendMessage( msg );
        System.out.println(msg);
    }
}
