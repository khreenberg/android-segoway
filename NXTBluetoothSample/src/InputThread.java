import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.Sound;


public class InputThread extends Thread{

    private OutputThread outputThread;
    private DataInputStream input;

    boolean isFinished = false;

    public InputThread( DataInputStream input ) {
        this.input = input;
    }

    @Override
    public void run(){
        while( !isFinished ) handleInput();
    }

    void handleInput(){
        try{
            byte[] b = new byte[256];
            int byteCount = input.read( b );
            String str = new String( b, 0, byteCount );
            System.out.println(str);
            Sound.beep();
        }
        catch( IOException e ){
            System.out.println("Error while reading from stream.\n"+e);
        }
    }

    public void Finish() {
        isFinished = true;
    }

    public void setOutputThread( OutputThread outputThread ) {
        this.outputThread = outputThread;
    }
}
